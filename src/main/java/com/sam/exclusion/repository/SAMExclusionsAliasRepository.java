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

    /*
     * @Query(value = "SELECT * FROM Users u WHERE u.status = :status and u.name = :name", 
  nativeQuery = true)
User findUserByStatusAndNameNamedParamsNative(
  @Param("status") Integer status, @Param("name") String name);
     */

     //Currently the param is not working
    //select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where (upper(alias_name) like upper('%:name%') limit 50; native=true-- all these ids are your primary data IDs
    @Query(value = "select distinct sam_exclusion_data_id from sam.sam_exclusions_alias sea where upper(sea.alias_name) like upper( CONCAT('%', :name, '%')) limit 50", nativeQuery =  true)
    List<Long> findDistinctIDByAliasNameIgnoreCaseContaining (@Param("name") String name);

    //select distinct sam_exclusion_data_id,alias_name from sam_exclusions_alias sea where alias_name in (':names') ;
    //List<SAMExclusionsAlias> findAliasByNames (@Param("names") List<String> names);

}
