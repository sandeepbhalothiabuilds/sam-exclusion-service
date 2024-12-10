package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsData;
import com.sam.exclusion.entity.SAMExclusionsDataCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface SAMExclusionsDataRepositoryCopy extends JpaRepository<SAMExclusionsDataCopy, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SAMExclusionsDataCopy WHERE terminationDate = '' or to_date(REPLACE(terminationDate, 'Indefinite', to_char(current_date, 'mm/dd/yyyy')) , 'mm/dd/yyyy') >= current_date")
    void deleteAllRecords();

    @Query(value = "select distinct sed.sam_number from  sam.sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids) and sed.sam_number is not null limit 2000", nativeQuery = true)
    List<String> findSamNumberByIDList (@Param("ids") Collection<Long> ids);

    @Query(value = "select distinct sed.unique_entity_id from  sam.sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids) and sed.unique_entity_id is not null limit 2000", nativeQuery = true)
    List<String> findUniqueEntityIDByIDList (@Param("ids") Collection<Long> ids);

    @Query(value = "select * from sam.sam_exclusions_data sed where (sed.sam_number in (:samNumber) and sed.sam_number <> '') or (sed.unique_entity_id in (:uniqueEntityID) and sed.unique_entity_id <> '') order by similarity(sed.full_name, :fullName) desc limit 2000", nativeQuery = true)
    List<SAMExclusionsData> findBySamNumberListAndUniqueEntityIDListAndName (@Param("samNumber") Collection<String> samNumber, @Param("uniqueEntityID") Collection<String> uniqueEntityID, @Param("fullName") String fullName);

    @Query(value = "select * from sam.sam_exclusions_data sed where upper(sed.full_address) like upper( CONCAT('%', :address, '%')) or similarity(sed.full_address, :fullAddress) > .4 order by similarity(sed.full_address, :fullAddress) desc limit 2000", nativeQuery = true)
    List<SAMExclusionsData> findRecordsByAddress(@Param("address") String address, @Param("fullAddress") String fullAddress);

    @Query(value = "select * from sam.sam_exclusions_data sed where (sed.sam_number in (:samNumber) and sed.sam_number <> '') or (sed.unique_entity_id in (:uniqueEntityID) and sed.unique_entity_id <> '') order by similarity(sed.full_address, :address) desc limit 2000", nativeQuery = true)
    List<SAMExclusionsData> findBySamNumberListAndUniqueEntityIDListAndAddress(@Param("samNumber") Collection<String> samNumber, @Param("uniqueEntityID") Collection<String> uniqueEntityID, @Param("address") String address);
}
