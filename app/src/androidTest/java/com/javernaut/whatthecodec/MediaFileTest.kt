package com.javernaut.whatthecodec

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.javernaut.whatthecodec.domain.MediaFileBuilder
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MediaFileTest {

    private val testFileName = "test_video_trimmed.mp4"

    @Test
    fun testMediaFileAccessors() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Writing a video file from assets to internal memory
        val inputStream = context.assets.open(testFileName)

        val testFile = File(targetContext.filesDir, testFileName)
        testFile.createNewFile()

        val outputStream = testFile.outputStream()
        inputStream.copyTo(outputStream)

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        // Actual test
        val descriptor = targetContext.contentResolver.openFileDescriptor(Uri.parse("file://" + testFile.absolutePath), "r")

        val mediaFile = MediaFileBuilder().from(descriptor!!).create()
        assertThat(mediaFile).isNotNull()

        if (mediaFile != null) {
            val videoStream = mediaFile.videoStream!!

            assertThat(videoStream.frameHeight).isEqualTo(1080)
            assertThat(videoStream.frameWidth).isEqualTo(1920)

            assertThat(mediaFile.fileFormatName).isEqualTo("QuickTime / MOV")
            assertThat(videoStream.codecName).isEqualTo("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10")

            mediaFile.release()
        }

        // Clean up
        testFile.delete()
    }

}
