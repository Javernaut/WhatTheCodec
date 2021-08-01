package com.javernaut.mediafile

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaFileTest {

    private val testVideoFileName = "test_video.mkv"
    private val testAudioFileName = "test_audio.aac"

    @Test
    fun testVideoFile() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val assetFileDescriptor = context.assets.openFd(testVideoFileName)

        val mediaFile = MediaFileBuilder(MediaType.VIDEO).from(assetFileDescriptor, "matroska").create()

        assertThat(mediaFile).isNotNull()

        assertThat(mediaFile!!.fileFormatName).isEqualTo("Matroska / WebM")

        // Video stream
        assertThat(mediaFile.videoStream).isNotNull()

        val videoStream = mediaFile.videoStream!!

        assertThat(videoStream.basicInfo.index).isEqualTo(0)
        assertThat(videoStream.basicInfo.codecName).isEqualTo("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10")
        assertThat(videoStream.basicInfo.title).isNull()
        assertThat(videoStream.basicInfo.language).isNull()
        assertThat(videoStream.basicInfo.disposition).isEqualTo(DispositionHelper.DispositionFeature.DEFAULT.mask)

        assertThat(videoStream.frameHeight).isEqualTo(2160)
        assertThat(videoStream.frameWidth).isEqualTo(3840)

        // Audio stream
        assertThat(mediaFile.audioStreams).isNotNull()
        assertThat(mediaFile.audioStreams).hasSize(1)

        val audioStream = mediaFile.audioStreams.first()
        assertThat(audioStream.basicInfo.index).isEqualTo(1)
        assertThat(audioStream.basicInfo.codecName).isEqualTo("ATSC A/52A (AC-3)")
        assertThat(audioStream.basicInfo.title).isNull()
        assertThat(audioStream.basicInfo.language).isNull()
        assertThat(audioStream.bitRate).isEqualTo(320000)
        assertThat(audioStream.sampleFormat).isEqualTo("fltp")
        assertThat(audioStream.sampleRate).isEqualTo(48000)
        assertThat(audioStream.channels).isEqualTo(6)
        assertThat(audioStream.channelLayout).isEqualTo("5.1(side)")
        assertThat(audioStream.basicInfo.disposition).isEqualTo(DispositionHelper.DispositionFeature.DEFAULT.mask)

        // Subtitle stream
        assertThat(mediaFile.subtitleStreams).isNotNull()
        assertThat(mediaFile.subtitleStreams).hasSize(1)

        val subtitleStream = mediaFile.subtitleStreams.first()

        assertThat(subtitleStream.basicInfo.index).isEqualTo(2)
        assertThat(subtitleStream.basicInfo.codecName).isEqualTo("SubRip subtitle")
        assertThat(subtitleStream.basicInfo.title).isNull()
        assertThat(subtitleStream.basicInfo.language).isEqualTo("eng")
        assertThat(subtitleStream.basicInfo.disposition).isEqualTo(0)

        // Clean up
        mediaFile.release()
        assetFileDescriptor.close()
    }

    @Test
    fun testAudioFile() {
        val context = InstrumentationRegistry.getInstrumentation().context

        // Actual test
        val assetFileDescriptor = context.assets.openFd(testAudioFileName)

        val mediaFile = MediaFileBuilder(MediaType.AUDIO).from(assetFileDescriptor, "aac").create()

        assertThat(mediaFile).isNotNull()

        // Video stream
        assertThat(mediaFile!!.videoStream).isNull()

        // Audio stream
        assertThat(mediaFile.audioStreams).isNotNull()
        assertThat(mediaFile.audioStreams).hasSize(1)

        val audioStream = mediaFile.audioStreams.first()
        assertThat(audioStream.basicInfo.index).isEqualTo(0)
        assertThat(audioStream.basicInfo.codecName).isEqualTo("AAC (Advanced Audio Coding)")
        assertThat(audioStream.basicInfo.title).isNull()
        assertThat(audioStream.basicInfo.language).isNull()
        assertThat(audioStream.bitRate).isEqualTo(98625)
        assertThat(audioStream.sampleFormat).isEqualTo("fltp")
        assertThat(audioStream.sampleRate).isEqualTo(48000)
        assertThat(audioStream.channels).isEqualTo(1)
        assertThat(audioStream.channelLayout).isEqualTo("mono")
        assertThat(audioStream.basicInfo.disposition).isEqualTo(0)

        // Subtitle stream
        assertThat(mediaFile.subtitleStreams).isNotNull()
        assertThat(mediaFile.subtitleStreams).isEmpty()

        // Clean up
        mediaFile.release()
        assetFileDescriptor.close()
    }

}
