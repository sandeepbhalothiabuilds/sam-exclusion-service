package com.sam.exclusion.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import com.sam.exclusion.entity.NyRentStabilizedProperty;
import com.sam.exclusion.entity.NyRentStabilizedPropertyAddress;
import com.sam.exclusion.entity.NycStblzdPropertyData;
import com.sam.exclusion.model.NycRentControlledPropertiesResponse;
import com.sam.exclusion.repository.NycRcuListingsAddressRepository;
import com.sam.exclusion.repository.NycRcuListingsRepository;
import com.sam.exclusion.repository.NycStblzdPropertyDataRepository;
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
    NycStblzdPropertyDataRepository nycStblzdPropertyDataRepository;

    @Override
    public void save(MultipartFile file) {

        if (file.getOriginalFilename().contains(".pdf")) {
            try {
                File file1 = convertToFile(file, file.getOriginalFilename());
                pdfParser.extractTables(file1);

            } catch (Exception e) {
                if (e instanceof FileAlreadyExistsException) {
                    throw new RuntimeException("A file of that name already exists.");
                }

                throw new RuntimeException(e.getMessage());
            }
        } else if (file.getOriginalFilename().contains(".csv")) {

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

    @Override
    public List<NycRentControlledPropertiesResponse> getProperties(String borough) {
        List<NyRentStabilizedPropertyAddress> nyRentStabilizedPropertyAddressList = nycRcuListingsAddressRepository.findByBorough(borough);
        List<NyRentStabilizedProperty> nyRentStabilizedPropertyList = nycRcuListingsRepository.findByBorough(borough);
        List<NycStblzdPropertyData> nycStblzdPropertyDataList = nycStblzdPropertyDataRepository.findByBorough(borough);
        List<NycRentControlledPropertiesResponse> nycRentControlledPropertiesResponseList = new ArrayList<>();
        nyRentStabilizedPropertyAddressList.parallelStream().forEach(nyRentStabilizedPropertyAddress->{
            NycRentControlledPropertiesResponse nycRentControlledPropertiesResponse = new NycRentControlledPropertiesResponse();

            try {

                setAddressDetails(nycRentControlledPropertiesResponse, nyRentStabilizedPropertyAddress);
                NycRentControlledPropertiesResponse finalNycRentControlledPropertiesResponse = nycRentControlledPropertiesResponse;
                NyRentStabilizedProperty nyRentStabilizedProperty = nyRentStabilizedPropertyList.stream().filter(p -> finalNycRentControlledPropertiesResponse.getUcbblNumber().equals(p.getUcbblNumber())).findFirst().get();
                setRentStabilizedPropertyData(nycRentControlledPropertiesResponse, nyRentStabilizedProperty);

                NycStblzdPropertyData nycStblzdPropertyData = nycStblzdPropertyDataList.stream().filter(p -> finalNycRentControlledPropertiesResponse.getUcbblNumber().equals(p.getUcbblNumber())).findFirst().get();
                setStblzdPropertyDataData(nycRentControlledPropertiesResponse, nycStblzdPropertyData);
                nycRentControlledPropertiesResponseList.add(nycRentControlledPropertiesResponse);
            } catch (Exception e) {
                //System.out.println("Error Occurred when setting data for UBCCL Number: "+ nycRentControlledPropertiesResponse.getUcbblNumber()+" and error: "+e);
                nycRentControlledPropertiesResponseList.add(nycRentControlledPropertiesResponse);
            }
        });
      //  for (NyRentStabilizedPropertyAddress nyRentStabilizedPropertyAddress : nyRentStabilizedPropertyAddressList)
        return nycRentControlledPropertiesResponseList;
    }

    private void setStblzdPropertyDataData(NycRentControlledPropertiesResponse nycRentControlledPropertiesResponse, NycStblzdPropertyData nycStblzdPropertyData) {
        nycRentControlledPropertiesResponse.setDataConsolidationYear(nycStblzdPropertyData.getDataConsolidationYear());
        nycRentControlledPropertiesResponse.setUc(nycStblzdPropertyData.getUc());

        nycRentControlledPropertiesResponse.setEst(nycStblzdPropertyData.getEst());
        nycRentControlledPropertiesResponse.setDhcr(nycStblzdPropertyData.getDhcr());
        nycRentControlledPropertiesResponse.setAbat(nycStblzdPropertyData.getAbat());
        nycRentControlledPropertiesResponse.setCd(nycStblzdPropertyData.getCd());
        nycRentControlledPropertiesResponse.setCt(nycStblzdPropertyData.getCt());
        nycRentControlledPropertiesResponse.setCb(nycStblzdPropertyData.getCb());
        nycRentControlledPropertiesResponse.setCouncil(nycStblzdPropertyData.getCouncil());
        nycRentControlledPropertiesResponse.setUnitAddress(nycStblzdPropertyData.getUnitAddress());
        nycRentControlledPropertiesResponse.setOwnerName(nycStblzdPropertyData.getOwnerName());
        nycRentControlledPropertiesResponse.setNumberOfBuildings(nycStblzdPropertyData.getNumberOfBuildings());
        nycRentControlledPropertiesResponse.setNumOfFloors(nycStblzdPropertyData.getNumOfFloors());

        nycRentControlledPropertiesResponse.setUnitRes(nycStblzdPropertyData.getUnitRes());

        nycRentControlledPropertiesResponse.setUnitTotal(nycStblzdPropertyData.getUnitTotal());
        nycRentControlledPropertiesResponse.setYearBuilt(nycStblzdPropertyData.getYearBuilt());
        nycRentControlledPropertiesResponse.setCondoNumber(nycStblzdPropertyData.getCondoNumber());
        nycRentControlledPropertiesResponse.setLongitude(nycStblzdPropertyData.getLongitude());
        nycRentControlledPropertiesResponse.setLatitude(nycStblzdPropertyData.getLatitude());

    }

    private void setRentStabilizedPropertyData(NycRentControlledPropertiesResponse nycRentControlledPropertiesResponse, NyRentStabilizedProperty nyRentStabilizedProperty) {
        nycRentControlledPropertiesResponse.setStatus1(nyRentStabilizedProperty.getStatus1());
        nycRentControlledPropertiesResponse.setStatus2(nyRentStabilizedProperty.getStatus2());
        nycRentControlledPropertiesResponse.setStatus3(nyRentStabilizedProperty.getStatus3());
    }

    private void setAddressDetails(NycRentControlledPropertiesResponse nycRentControlledPropertiesResponse, NyRentStabilizedPropertyAddress nyRentStabilizedPropertyAddress) {
        nycRentControlledPropertiesResponse.setUcbblNumber(nyRentStabilizedPropertyAddress.getUcbblNumber());
        nycRentControlledPropertiesResponse.setBorough(nyRentStabilizedPropertyAddress.getBorough());
        nycRentControlledPropertiesResponse.setBlock(nyRentStabilizedPropertyAddress.getBlock());
        nycRentControlledPropertiesResponse.setLot(nyRentStabilizedPropertyAddress.getLot());
        nycRentControlledPropertiesResponse.setBuildingNumber(nyRentStabilizedPropertyAddress.getBuildingNumber());
        nycRentControlledPropertiesResponse.setStreet(nyRentStabilizedPropertyAddress.getStreet());
        nycRentControlledPropertiesResponse.setStateSuffix(nyRentStabilizedPropertyAddress.getStateSuffix());
        nycRentControlledPropertiesResponse.setCity(nyRentStabilizedPropertyAddress.getCity());
        nycRentControlledPropertiesResponse.setCounty(nyRentStabilizedPropertyAddress.getCounty());
        nycRentControlledPropertiesResponse.setZip(nyRentStabilizedPropertyAddress.getZip());

    }

    private File convertToFile(MultipartFile file, String originalFilename) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + originalFilename);
        file.transferTo(convFile.toPath());
        return convFile;
    }

}
