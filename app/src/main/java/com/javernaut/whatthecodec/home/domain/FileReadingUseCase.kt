package com.javernaut.whatthecodec.home.domain

import com.javernaut.whatthecodec.di.IoDispatcher
import com.javernaut.whatthecodec.home.presentation.MediaFileArgument
import com.javernaut.whatthecodec.home.presentation.MediaFileProvider
import io.github.javernaut.mediafile.factory.MediaFileContext
import io.github.javernaut.mediafile.model.MediaFile
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
            val context = mediaFileProvider.obtainMediaFile(arg)
                ?: return@withContext Result.failure(IOException("Couldn't read the file"))

            val mediaFile = context.readMetaData()
                ?: return@withContext Result.failure(IOException("Couldn't read the file's meta data"))

            Result.success(ReadingResult(context, mediaFile))
        }

    // Reusing the data types from MediaFile library
    data class ReadingResult(
        val context: MediaFileContext,
        val mediaFile: MediaFile
    )
}
