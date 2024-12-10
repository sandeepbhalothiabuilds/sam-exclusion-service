package com.sam.exclusion.repository;

import com.sam.exclusion.entity.SAMExclusionsAlias;
import java.util.List;

import com.sam.exclusion.entity.SAMExclusionsData;
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
    @Query(value = "delete FROM sam.sam_exclusions_alias WHERE sam_exclusion_data_id not in (select sam_exclusion_data_id from sam.sam_exclusions_data)", nativeQuery = true)
    void deleteAllRecords();

    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(sea.alias_name) like upper( CONCAT('%', :name, '%')) limit 2000", nativeQuery =  true)
    List<Long> findDistinctIDByAliasNameIgnoreCaseContaining (@Param("name") String name);

    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where sea.alias_name in (select distinct sea1.alias_name from sam.sam_exclusions_alias sea1 where sea1.sam_exclusion_data_id in (select distinct sea2.sam_exclusion_data_id from sam.sam_exclusions_alias sea2 where upper(sea2.alias_name) like upper( CONCAT('%', :name, '%')))) except select distinct sea3.sam_exclusion_data_id from sam.sam_exclusions_alias sea3 where upper(sea3.alias_name) like upper( CONCAT('%', :name, '%')) limit 2000",
    nativeQuery = true)
    List<Long> findSecondaryDataByName (@Param("name") String name);

    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(sea.alias_name) in ( :aliasList ) except select distinct sed.sam_exclusion_data_id from sam.sam_exclusions_data sed where upper(sed.full_address) like upper( CONCAT('%', :address, '%')) limit 2000", nativeQuery = true)
    List<Long> findSecondaryDataIdsByProvidingAliasList(@Param("aliasList") List<String> aliasList, @Param("address") String address);
}
