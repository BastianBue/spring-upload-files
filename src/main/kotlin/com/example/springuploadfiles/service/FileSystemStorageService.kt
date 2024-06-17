package com.example.springuploadfiles.service

import com.example.springuploadfiles.controller.advice.exception.StorageException
import com.example.springuploadfiles.controller.advice.exception.StorageFileNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream

private val logger = LoggerFactory.getLogger(FileSystemStorageService::class.java)
@Service
class FileSystemStorageService @Autowired constructor(properties: StorageProperties) : StorageService {
    private val rootLocation: Path = if (properties.location.trim()
            .isEmpty()
    ) throw StorageException("File upload location can not be Empty.") else Paths.get(properties.location)

    override fun store(file: MultipartFile) {
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file.")
            }

            val destinationFile = rootLocation.resolve(
                Paths.get(file.originalFilename ?: throw StorageException("Failed to find original file name."))
            ).normalize().toAbsolutePath()

            if (destinationFile.parent != rootLocation.toAbsolutePath()) {
                // This is a security check
                throw StorageException(
                    "Cannot store file outside current directory."
                )
            }
            file.inputStream.use { inputStream ->
                Files.copy(
                    inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            logger.info("File stored successfully: ${destinationFile.fileName}")
        } catch (e: IOException) {
            throw StorageException("Failed to store file.", e)
        }
    }

    override fun loadAll(): Stream<Path> {
        return try {
            Files.walk(rootLocation, 1)
                .filter { path: Path -> path != rootLocation }
                .map { other: Path? ->
                    other?.let { rootLocation.relativize(it) }
                }
        } catch (e: IOException) {
            throw StorageException("Failed to read stored files", e)
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        return try {
            val file = load(filename)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException(
                    "Could not read file: $filename"
                )
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: $filename", e)
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage", e)
        }
    }
}

