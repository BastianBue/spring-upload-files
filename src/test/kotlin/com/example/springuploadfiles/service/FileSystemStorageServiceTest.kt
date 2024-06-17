package com.example.springuploadfiles.service

import com.example.springuploadfiles.controller.advice.exception.StorageFileNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.io.File
import java.nio.charset.StandardCharsets

class FileSystemStorageServiceTest {
    private val properties = mockk<StorageProperties>()
    private lateinit var subject: FileSystemStorageService

    private val location = ".uploads"

    private val content = "sample file content"
    private val contentType = "text/plain"

    private fun createMockFile(name: String): MockMultipartFile = MockMultipartFile(
        name, // filename
        name, // original filename
        contentType, // content type
        content.toByteArray(StandardCharsets.UTF_8) // file content
    )


    @BeforeEach
    fun setUp() {
        every { properties.location } returns "location"
        subject = FileSystemStorageService(properties)
        subject.init()
        subject.store(createMockFile("file.txt"))
        subject.store(createMockFile("file2.txt"))
    }

    @AfterEach
    fun tearDown() {
        subject.deleteAll()
    }

    @Test
    fun `should save a file to a given storage directory`() {
        val newFileName = "file3.txt"
        subject.store(createMockFile(newFileName))

        val file = File("$location/$newFileName")

        assert(file.exists())
    }

    @Test
    fun `should retrieve all files from a given storage directory`() {
        val files = subject.loadAll().toList()
        val names = files.map { it.fileName.toString() }

        assert(files.size == 2)
        assert(names.contains("file.txt"))
        assert(names.contains("file2.txt"))
    }

    @Test
    fun `should load a given file as a resource if it exists`(){
        val resource = subject.loadAsResource("file.txt")

        assert(resource.isFile)
    }

    @Test
    fun `should throw an exception when trying to load a file that does not exist`(){
        assertThrows<StorageFileNotFoundException> {
            subject.loadAsResource("file3.txt")
        }
    }
}