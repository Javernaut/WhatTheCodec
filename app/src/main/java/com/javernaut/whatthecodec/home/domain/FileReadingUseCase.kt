package com.javernaut.whatthecodec.home.domain

import com.javernaut.whatthecodec.di.IoDispatcher
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileProvider
import io.github.javernaut.mediafile.MediaFile
import io.github.javernaut.mediafile.model.MediaInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class FileReadingUseCase @Inject constructor(
    private val mediaFileProvider: MediaFileProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun readFile(arg: MediaFileArgument): Result<ReadingResult> =
        withContext(ioDispatcher) {
            val mediaFile = mediaFileProvider.obtainMediaFile(arg.uri)
                ?: return@withContext Result.failure(IOException("Couldn't read the file"))

            val mediaInfo = mediaFile.readMediaInfo()
                ?: return@withContext Result.failure(IOException("Couldn't read the file's meta data"))

            Result.success(ReadingResult(mediaFile, mediaInfo))
        }

    // Reusing the data types from MediaFile library
    data class ReadingResult(
        val context: MediaFile,
        val metaData: MediaInfo
    )
}
