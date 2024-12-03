package com.nyc.properties.service;

import com.nyc.properties.model.NycRentControlledPropertiesResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FilesStorageService {
    public void save(MultipartFile file);

    List<NycRentControlledPropertiesResponse> getProperties(String borough);
}
