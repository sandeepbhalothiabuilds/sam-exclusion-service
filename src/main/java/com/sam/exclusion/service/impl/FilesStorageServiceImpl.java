package com.sam.exclusion.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sam.exclusion.entity.NyRentStabilizedProperty;
import com.sam.exclusion.entity.NyRentStabilizedPropertyAddress;
import com.sam.exclusion.model.NycRentControlledPropertiesResponse;
import com.sam.exclusion.repository.NycRcuListingsAddressRepository;
import com.sam.exclusion.repository.NycRcuListingsRepository;
import com.sam.exclusion.repository.SAMExclusionsDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sam.exclusion.service.CSVParser;
import com.sam.exclusion.service.FilesStorageService;
import com.sam.exclusion.service.PDFParser;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    @Autowired
    PDFParser pdfParser;

    @Autowired
    CSVParser csvParser;

    @Autowired
    NycRcuListingsAddressRepository nycRcuListingsAddressRepository;

    @Autowired
    NycRcuListingsRepository nycRcuListingsRepository;

    @Autowired
    SAMExclusionsDataRepository nycStblzdPropertyDataRepository;

    @Override
    public void save(MultipartFile file) {

       if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().contains(".csv")) {

            try {
                File file1 = convertToFile(file, file.getOriginalFilename());
                csvParser.extractExcelData(file1);
            } catch (Exception e) {
                if (e instanceof FileAlreadyExistsException) {
                    throw new RuntimeException("A file of that name already exists.");
                }

                throw new RuntimeException(e.getMessage());
            }
        }
        ;

    }






    private File convertToFile(MultipartFile file, String originalFilename) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + originalFilename);
        file.transferTo(convFile.toPath());
        return convFile;
    }

}
