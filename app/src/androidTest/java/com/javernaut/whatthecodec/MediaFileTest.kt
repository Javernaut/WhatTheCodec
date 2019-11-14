package com.javernaut.whatthecodec

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.javernaut.whatthecodec.domain.MediaFileBuilder
import com.javernaut.whatthecodec.domain.MediaType
import com.javernaut.whatthecodec.presentation.stream.helper.DispositionHelper
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MediaFileTest {

    private val testVideoFileName = "test_video.mkv"
    private val testAudioFileName = "test_audio.aac"

    @Test
    fun testVideoFile() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Writing a video file from assets to internal memory
        val inputStream = context.assets.open(testVideoFileName)

        val testFile = File(targetContext.filesDir, testVideoFileName)
        testFile.createNewFile()

        val outputStream = testFile.outputStream()
        inputStream.copyTo(outputStream)

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        // Actual test
        val descriptor = targetContext.contentResolver.openFileDescriptor(Uri.parse("file://" + testFile.absolutePath), "r")

        val mediaFile = MediaFileBuilder(MediaType.VIDEO).from(descriptor!!).create()

        assertThat(mediaFile).isNotNull()

        // Video stream
        assertThat(mediaFile!!.videoStream).isNotNull()

        val videoStream = mediaFile.videoStream!!

        assertThat(videoStream.frameHeight).isEqualTo(2160)
        assertThat(videoStream.frameWidth).isEqualTo(3840)

        assertThat(mediaFile.fileFormatName).isEqualTo("Matroska / WebM")
        assertThat(videoStream.codecName).isEqualTo("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10")

        // Audio stream
        assertThat(mediaFile.audioStreams).isNotNull()
        assertThat(mediaFile.audioStreams).hasSize(1)

        val audioStream = mediaFile.audioStreams.first()
        assertThat(audioStream.index).isEqualTo(1)
        assertThat(audioStream.codecName).isEqualTo("ATSC A/52A (AC-3)")
        assertThat(audioStream.title).isNull()
        assertThat(audioStream.language).isNull()
        assertThat(audioStream.bitRate).isEqualTo(320000)
        assertThat(audioStream.sampleFormat).isEqualTo("fltp")
        assertThat(audioStream.sampleRate).isEqualTo(48000)
        assertThat(audioStream.channels).isEqualTo(6)
        assertThat(audioStream.channelLayout).isEqualTo("5.1(side)")
        assertThat(audioStream.disposition).isEqualTo(DispositionHelper.DispositionFeature.DEFAULT.mask)

        // Subtitle stream
        assertThat(mediaFile.subtitleStreams).isNotNull()
        assertThat(mediaFile.subtitleStreams).hasSize(1)

        val subtitleStream = mediaFile.subtitleStreams.first()

        assertThat(subtitleStream.index).isEqualTo(2)
        assertThat(subtitleStream.codecName).isEqualTo("SubRip subtitle")
        assertThat(subtitleStream.title).isNull()
        assertThat(subtitleStream.language).isEqualTo("eng")
        assertThat(subtitleStream.disposition).isEqualTo(0)

        // Clean up
        mediaFile.release()

        testFile.delete()
    }

    @Test
    fun testAudioFile() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Writing a video file from assets to internal memory
        val inputStream = context.assets.open(testAudioFileName)

        val testFile = File(targetContext.filesDir, testAudioFileName)
        testFile.createNewFile()

        val outputStream = testFile.outputStream()
        inputStream.copyTo(outputStream)

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        // Actual test
        val descriptor = targetContext.contentResolver.openFileDescriptor(Uri.parse("file://" + testFile.absolutePath), "r")

        val mediaFile = MediaFileBuilder(MediaType.AUDIO).from(descriptor!!).create()

        assertThat(mediaFile).isNotNull()

        // Video stream
        assertThat(mediaFile!!.videoStream).isNull()

        // Audio stream
        assertThat(mediaFile.audioStreams).isNotNull()
        assertThat(mediaFile.audioStreams).hasSize(1)

        val audioStream = mediaFile.audioStreams.first()
        assertThat(audioStream.index).isEqualTo(0)
        assertThat(audioStream.codecName).isEqualTo("AAC (Advanced Audio Coding)")
        assertThat(audioStream.title).isNull()
        assertThat(audioStream.language).isNull()
        assertThat(audioStream.bitRate).isEqualTo(98625)
        assertThat(audioStream.sampleFormat).isEqualTo("fltp")
        assertThat(audioStream.sampleRate).isEqualTo(48000)
        assertThat(audioStream.channels).isEqualTo(1)
        assertThat(audioStream.channelLayout).isEqualTo("mono")
        assertThat(audioStream.disposition).isEqualTo(0)

        // Subtitle stream
        assertThat(mediaFile.subtitleStreams).isNotNull()
        assertThat(mediaFile.subtitleStreams).isEmpty()

        // Clean up
        mediaFile.release()

        testFile.delete()
    }

}
