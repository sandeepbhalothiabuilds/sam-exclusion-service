package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SAMExclusionsDataRepository extends JpaRepository<SAMExclusionsData, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SAMExclusionsData WHERE samExclusionDataId > 0")
    void deleteAllRecords();

}
