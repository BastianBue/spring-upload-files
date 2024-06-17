package com.example.springuploadfiles

import com.example.springuploadfiles.service.StorageProperties
import com.example.springuploadfiles.service.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class SpringUploadFilesApplication{
    @Bean
    fun init(storageService: StorageService): CommandLineRunner? {
        return CommandLineRunner {
            storageService.deleteAll()
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringUploadFilesApplication>(*args)
}
