package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsData;
import com.sam.exclusion.model.SAMExclusionsDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface SAMExclusionsDataRepository extends JpaRepository<SAMExclusionsData, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SAMExclusionsData WHERE samExclusionDataId > 0")
    void deleteAllRecords();

    @Query(value = "select distinct sed.sam_number from  sam.sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids) and sed.sam_number is not null limit 999", nativeQuery = true)
    List<String> findSamNumberByIDList (@Param("ids") Collection<Long> ids);

    @Query(value = "select distinct sed.unique_entity_id from  sam.sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids) and sed.unique_entity_id is not null limit 999", nativeQuery = true)
    List<String> findUniqueEntityIDByIDList (@Param("ids") Collection<Long> ids);

    @Query(value = "select * from sam.sam_exclusions_data sed where (sed.sam_number in (:samNumber) and sed.sam_number <> '') or (sed.unique_entity_id in (:uniqueEntityID) and sed.unique_entity_id <> '') limit 500;", nativeQuery = true)
    List<SAMExclusionsData> findBySamNumberListAndUniqueEntityIDList (@Param("samNumber") Collection<String> samNumber, @Param("uniqueEntityID") Collection<String> uniqueEntityID);
    
}
