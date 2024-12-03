package com.sam.exclusion.service;

import com.sam.exclusion.model.NycRentControlledPropertiesResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FilesStorageService {
    public void save(MultipartFile file);
}
