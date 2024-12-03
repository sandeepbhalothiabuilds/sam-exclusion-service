package com.nyc.properties.repository;

import com.nyc.properties.entity.NyRentStabilizedProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NycRcuListingsRepository extends JpaRepository<NyRentStabilizedProperty, Long> {

    @Modifying
    @Transactional
    @Query(value="DELETE FROM NyRentStabilizedProperty WHERE borough = ?1")
    void deleteRecordsByBorough(String boroughName);

    List<NyRentStabilizedProperty> findByBorough(String borough);
}
