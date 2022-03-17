package com.codesample.checker.utils

import android.content.Context
import com.codesample.checker.entities.details.Image
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

class ImageUtil @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val remoteRepo: AvitoRepository
) {

    fun deleteImages(files: List<File>) {
        files.forEach {
            it.delete()
        }
    }

    suspend fun downloadImages(images: List<Image>): List<File> {
        return images.map {
            downloadImage(it)
        }
    }

    private suspend fun downloadImage(image: Image): File {
        val url = image.img1280x960
        val filesDir = File("${context.filesDir}/images").also { it.mkdir() }
        val md5Name = MessageDigest.getInstance("MD5").run {
            update(url.toByteArray())
            digest().joinToString(separator = "") { byteToString(it) }
        }
        val file = File(filesDir, "${md5Name}.jpg")
        if (!file.exists()) {
            remoteRepo.downloadFile(url, file)
        }
        return file
    }

    // Use this workaround, because android not supporting DatatypeConverter.printHexBinary()
    private fun byteToString(byte: Byte): String {
        return Integer.toHexString(byte.toInt()).run {
            when (length) {
                1 -> "0$this"
                2 -> this
                else -> substring(length -2, length)
            }
        }
    }
}