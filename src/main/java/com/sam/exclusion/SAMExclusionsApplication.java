package com.sam.exclusion;

import com.sam.exclusion.service.FilesStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SAMExclusionsApplication {
    @Resource
    FilesStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(SAMExclusionsApplication.class, args);
    }

}