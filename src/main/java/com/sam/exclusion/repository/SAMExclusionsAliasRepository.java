package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsAlias;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SAMExclusionsAliasRepository extends JpaRepository<SAMExclusionsAlias, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SAMExclusionsAlias WHERE samExclusionAliasId > 0") 
    void deleteAllRecords();

    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(sea.alias_name) like upper( CONCAT('%', :name, '%')) limit 999", nativeQuery =  true)
    List<Long> findDistinctIDByAliasNameIgnoreCaseContaining (@Param("name") String name);

    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where alias_name in (select distinct alias_name from sam.sam_exclusions_alias sea where sam_exclusion_data_id in (select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(alias_name) like upper( CONCAT('%', :name, '%')))) except select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(alias_name) like upper( CONCAT('%', :name, '%')) limit 999",
    nativeQuery = true)
    List<Long> findSecondaryDataByName (@Param("name") String name);


}
