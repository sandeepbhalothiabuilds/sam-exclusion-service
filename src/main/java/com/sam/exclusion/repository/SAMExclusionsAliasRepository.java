package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsAlias;
import com.sam.exclusion.entity.SAMExclusionsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SAMExclusionsAliasRepository extends JpaRepository<SAMExclusionsAlias, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SAMExclusionsAlias WHERE samExclusionAliasId > 0")
    void deleteAllRecords();

}
