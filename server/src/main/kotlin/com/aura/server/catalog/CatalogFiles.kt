package com.aura.server.catalog

import com.aura.server.auth.Akun
import com.aura.server.plugins.ApiException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.UUID

internal class CatalogFileStore(private val directory: Path) {
    fun write(fileId: UUID, bytes: ByteArray) {
        Files.createDirectories(directory)
        Files.write(
            directory.resolve(fileId.toString()),
            bytes,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE,
        )
    }

    fun read(fileId: UUID): ByteArray = Files.readAllBytes(directory.resolve(fileId.toString()))

    fun delete(fileId: UUID) {
        Files.deleteIfExists(directory.resolve(fileId.toString()))
    }
}

internal class CatalogFileService(
    private val catalogService: CatalogService,
    private val fileStore: CatalogFileStore,
) {
    fun upload(account: Akun, mimeType: String, bytes: ByteArray): FileUploadResponse {
        if (mimeType !in ALLOWED_MIME_TYPES) {
            throw ApiException(HttpStatusCode.UnsupportedMediaType, "TIPE_FILE_TIDAK_DIDUKUNG", "Tipe file harus JPEG, PNG, atau WebP.")
        }
        if (bytes.isEmpty()) {
            throw ApiException(HttpStatusCode.BadRequest, "FILE_TIDAK_VALID", "File tidak boleh kosong.")
        }
        if (bytes.size > MAX_FILE_BYTES) {
            throw ApiException(HttpStatusCode.PayloadTooLarge, "UKURAN_FILE_TIDAK_VALID", "Ukuran file harus lebih dari 0 dan maksimal 10 MiB.")
        }
        val metadata = FileMetadata(
            id = UUID.randomUUID(),
            idToko = account.idToko,
            mimeType = mimeType,
            ukuran = bytes.size.toLong(),
            dibuatPada = System.currentTimeMillis(),
        )
        try {
            fileStore.write(metadata.id, bytes)
            catalogService.registerFile(metadata)
        } catch (failure: Exception) {
            try {
                fileStore.delete(metadata.id)
            } catch (cleanupFailure: IOException) {
                failure.addSuppressed(cleanupFailure)
            }
            throw failure
        }
        return FileUploadResponse(metadata.id, metadata.mimeType, metadata.ukuran)
    }

    fun download(account: Akun, fileId: UUID): Pair<FileMetadata, ByteArray> {
        val metadata = catalogService.findFile(account, fileId)
        return metadata to fileStore.read(metadata.id)
    }

    companion object {
        const val MAX_FILE_BYTES = 10L * 1024 * 1024
        private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}

internal data class CatalogComponents(
    val service: CatalogService,
    val fileService: CatalogFileService,
)

internal fun catalogComponents(
    uploadDirectory: Path,
    repository: CatalogRepository = ExposedCatalogRepository(),
): CatalogComponents {
    val service = CatalogService(repository)
    val fileService = CatalogFileService(service, CatalogFileStore(uploadDirectory))
    return CatalogComponents(service, fileService)
}
