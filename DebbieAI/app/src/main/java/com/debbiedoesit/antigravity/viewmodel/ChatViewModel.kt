package com.debbiedoesit.antigravity.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.antigravity.R
import com.debbiedoesit.antigravity.ai.ContractorLLMEngine
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(val text: String, val isUser: Boolean, val isError: Boolean = false)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = ContractorLLMEngine(application)
    val messages = mutableStateListOf<ChatMessage>()

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded = _isModelLoaded.asStateFlow()

    private val _isPreparingModel = MutableStateFlow(false)
    val isPreparingModel = _isPreparingModel.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _expression = MutableStateFlow(R.drawable.neutral_listening)
    val expression = _expression.asStateFlow()

    private val _sttText = MutableStateFlow("")
    val sttText = _sttText.asStateFlow()

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    init {
        val modelFile = File(application.filesDir, "gemma-2b-it-gpu-int4.bin")
        viewModelScope.launch {
            _isPreparingModel.value = true
            messages.add(ChatMessage("Debbie is waking up... ☕", false))
            try {
                if (!modelFile.exists()) {
                    messages.add(
                            ChatMessage(
                                    "Downloading knowledge base (this may take a moment)...",
                                    false
                            )
                    )
                    ContractorLLMEngine.copyModelFromAssets(
                            application,
                            "gemma-2b-it-gpu-int4.bin",
                            modelFile
                    )
                }

                Log.d("ChatViewModel", "Starting model load from: ${modelFile.absolutePath}")
                messages.add(
                        ChatMessage(
                                "Connecting to AI Core (Optimizing for your hardware)...",
                                false
                        )
                )
                engine.load(modelFile.absolutePath)
                _isModelLoaded.value = true
                messages.add(
                        ChatMessage(
                                "Debbie is online and ready for the job site. How can I help you today?",
                                false
                        )
                )
                Log.d("ChatViewModel", "Model loaded successfully.")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error preparing AI: ${e.message}", e)
                messages.add(
                        ChatMessage(
                                "Error preparing AI: ${e.message}. Please check your internet or disk space.",
                                false,
                                true
                        )
                )
            } finally {
                _isPreparingModel.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        messages.add(ChatMessage(text, true))
        _isGenerating.value = true
        _expression.value = R.drawable.deep_thought

        val responseMessageIndex = messages.size
        messages.add(ChatMessage("Thinking...", false))

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { engine.chatSync(text) }
                messages[responseMessageIndex] = ChatMessage(response, false)
                updateExpressionBasedOnResponse(response)
            } catch (e: Exception) {
                messages[responseMessageIndex] =
                        ChatMessage("Sorry, I encountered an error: ${e.message}", false, true)
                _expression.value = R.drawable.awkward
            } finally {
                _isGenerating.value = false
                if (_expression.value == R.drawable.deep_thought) {
                    _expression.value = R.drawable.neutral_listening
                }
            }
        }
    }

    private fun updateExpressionBasedOnResponse(response: String) {
        val lower = response.lowercase()
        _expression.value =
                when {
                    lower.contains("congratulations") ||
                            lower.contains("happy") ||
                            lower.contains("great") -> R.drawable.happy_pleased
                    lower.contains("error") ||
                            lower.contains("sorry") ||
                            lower.contains("unfortunately") -> R.drawable.concerned
                    lower.contains("problem") ||
                            lower.contains("fix") ||
                            lower.contains("repair") -> R.drawable.confident_authoritative
                    lower.contains("?") -> R.drawable.curious
                    else -> R.drawable.neutral_listening
                }
    }

    fun startSpeechToText() {
        val intent =
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

        speechRecognizer.setRecognitionListener(
                object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _sttText.value = "Listening..."
                        _expression.value = R.drawable.listens_intently
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        Log.e("ChatViewModel", "Speech Recognizer Error: $error")
                        _sttText.value = ""
                        _expression.value = R.drawable.neutral_listening
                    }
                    override fun onResults(results: Bundle?) {
                        val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        _sttText.value = data?.get(0) ?: ""
                        _expression.value = R.drawable.neutral_listening
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val data =
                                partialResults?.getStringArrayList(
                                        SpeechRecognizer.RESULTS_RECOGNITION
                                )
                        _sttText.value = data?.get(0) ?: ""
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                }
        )

        speechRecognizer.startListening(intent)
    }

    fun clearSttText() {
        _sttText.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        engine.release()
    }
}
