package com.example.springuploadfiles.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("storage")
class StorageProperties {
    var location = ".uploads"
}

