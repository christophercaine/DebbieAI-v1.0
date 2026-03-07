package com.debbiedoesit.antigravity.ai

import com.antonkarpenko.ffmpegkit.FFmpegKit
import com.antonkarpenko.ffmpegkit.ReturnCode
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Job Site Video Engine — Complete Video Pipeline Built on FFmpegKit. Works on all Android devices.
 */
class JobSiteVideoEngine(private val cacheDir: File) {

    // Before/After compilation — most requested contractor video
    suspend fun createBeforeAfterReel(
            beforePhotos: List<String>,
            afterPhotos: List<String>,
            outputPath: String,
            projectName: String,
            companyName: String = "Debbie Does It"
    ) {
        // 1. Create before sequence (2 sec per photo)
        val beforeVideo = File(cacheDir, "before_raw.mp4").absolutePath
        createSlideshow(beforePhotos, beforeVideo, 2)

        // 2. Create after sequence
        val afterVideo = File(cacheDir, "after_raw.mp4").absolutePath
        createSlideshow(afterPhotos, afterVideo, 2)

        // 3. Add "BEFORE" / "AFTER" title cards
        val beforeTitled = File(cacheDir, "before_titled.mp4").absolutePath
        addTextOverlay(beforeVideo, "BEFORE", beforeTitled, 60, 60, 80)
        val afterTitled = File(cacheDir, "after_titled.mp4").absolutePath
        addTextOverlay(afterVideo, "AFTER", afterTitled, 60, 60, 80)

        // 4. Concatenate before + after
        val combined = File(cacheDir, "combined_raw.mp4").absolutePath
        concatenate(listOf(beforeTitled, afterTitled), combined)

        // 5. Add company watermark + project title
        addProjectBranding(combined, projectName, companyName, outputPath)
    }

    // Job site walkthrough — stabilize
    suspend fun processWalkthrough(inputPath: String, outputPath: String) {
        val transformFile = File(cacheDir, "transforms.trf").absolutePath
        execute("-i \"$inputPath\" -vf vidstabdetect=stepsize=6:shakiness=8:accuracy=9 -f null -")
        execute(
                "-i \"$inputPath\" -vf vidstabtransform=smoothing=30:input=\"$transformFile\" \"$outputPath\""
        )
    }

    suspend fun createTimelapse(imageFolder: String, output: String, fps: Int = 8) {
        execute(
                "-framerate $fps -pattern_type glob -i \"$imageFolder/*.jpg\" -c:v libx264 -pix_fmt yuv420p -crf 23 \"$output\""
        )
    }

    private suspend fun createSlideshow(images: List<String>, output: String, secPerFrame: Int) {
        val fileList = images.joinToString("\n") { "file '$it'\nduration $secPerFrame" }
        val listFile = writeToTemp(fileList, "concat_list.txt")
        execute(
                "-f concat -safe 0 -i \"${listFile.absolutePath}\" -vf \"scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)/2:(oh-ih)/2\" -c:v libx264 -pix_fmt yuv420p \"$output\""
        )
    }

    private suspend fun addTextOverlay(
            input: String,
            text: String,
            output: String,
            x: Int,
            y: Int,
            fontSize: Int
    ) {
        execute(
                "-i \"$input\" -vf \"drawtext=text='$text':x=$x:y=$y:fontsize=$fontSize:fontcolor=white:shadowcolor=black:shadowx=3:shadowy=3\" \"$output\""
        )
    }

    private suspend fun concatenate(inputs: List<String>, output: String) {
        val listContent = inputs.joinToString("\n") { "file '$it'" }
        val listFile = writeToTemp(listContent, "concat.txt")
        execute("-f concat -safe 0 -i \"${listFile.absolutePath}\" -c copy \"$output\"")
    }

    private suspend fun addProjectBranding(
            input: String,
            project: String,
            company: String,
            output: String
    ) {
        execute(
                "-i \"$input\" -vf \"drawtext=text='$company':x=20:y=20:fontsize=32:fontcolor=white:shadowcolor=black:shadowx=2:shadowy=2,drawtext=text='$project':x=20:y=60:fontsize=22:fontcolor=yellow:shadowcolor=black:shadowx=2:shadowy=2\" \"$output\""
        )
    }

    private fun writeToTemp(content: String, filename: String): File {
        val file = File(cacheDir, filename)
        file.writeText(content)
        return file
    }

    private suspend fun execute(cmd: String) =
            suspendCancellableCoroutine<Unit> { cont ->
                FFmpegKit.executeAsync("-y $cmd") { session ->
                    if (ReturnCode.isSuccess(session.returnCode)) {
                        cont.resume(Unit)
                    } else {
                        cont.resumeWithException(
                                Exception("FFmpeg failed: ${session.getLogsAsString()}")
                        )
                    }
                }
            }
}
