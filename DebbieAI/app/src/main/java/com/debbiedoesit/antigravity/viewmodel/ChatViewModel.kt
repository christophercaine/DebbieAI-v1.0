package com.debbiedoesit.antigravity.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        val modelFile = File(application.filesDir, "gemma-2b-it-gpu-int4.bin")
        viewModelScope.launch {
            try {
                if (!modelFile.exists()) {
                    _isPreparingModel.value = true
                    ContractorLLMEngine.copyModelFromAssets(
                            application,
                            "gemma-2b-it-gpu-int4.bin",
                            modelFile
                    )
                    _isPreparingModel.value = false
                }

                engine.load(modelFile.absolutePath)
                _isModelLoaded.value = true
            } catch (e: Exception) {
                _isPreparingModel.value = false
                messages.add(ChatMessage("Error preparing AI: ${e.message}", false, true))
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        messages.add(ChatMessage(text, true))
        _isGenerating.value = true

        val responseMessageIndex = messages.size
        messages.add(ChatMessage("Thinking...", false))

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { engine.chatSync(text) }
                messages[responseMessageIndex] = ChatMessage(response, false)
            } catch (e: Exception) {
                messages[responseMessageIndex] =
                        ChatMessage("Sorry, I encountered an error: ${e.message}", false, true)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.release()
    }
}
