package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsData;
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

    //Currently only accepting one ID
    //select * from  sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids);
    @Query(value = "select * from  sam.sam_exclusions_data sed where sed.sam_exclusion_data_id in (:ids)", nativeQuery = true)
    List<SAMExclusionsData> findExclusionDataByIDList (@Param("ids") Collection<Long> ids);
    
}
