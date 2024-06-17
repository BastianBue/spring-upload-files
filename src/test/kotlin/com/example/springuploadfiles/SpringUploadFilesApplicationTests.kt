package com.example.springuploadfiles

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
class SpringUploadFilesApplicationTests {
    @Test
    fun contextLoads() {
    }

    @Test
    fun `should create a storage directory on startup`(){
        Files.isDirectory(Paths.get(".uploads"))
    }
}
