package com.sam.exclusion.repository;

import com.sam.exclusion.entity.NyRentStabilizedPropertyAddress;
import com.sam.exclusion.model.PropertyDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NycRcuListingsAddressRepository extends JpaRepository<NyRentStabilizedPropertyAddress, Long> {

    @Modifying
    @Transactional
    @Query(value="DELETE FROM NyRentStabilizedPropertyAddress WHERE borough = ?1")
    void deleteRecordsByBorough(String boroughName);

    List<NyRentStabilizedPropertyAddress> findByBorough(String borough);
    @Query("SELECT new com.sam.exclusion.model.PropertyDetails(p, u, a) " +
            "FROM NyRentStabilizedPropertyAddress a " +
            "LEFT JOIN NyRentStabilizedProperty p ON p.ucbblNumber = a.ucbblNumber " +
            "LEFT JOIN NycStblzdPropertyData u ON a.ucbblNumber = u.ucbblNumber")
    List<PropertyDetails> getAllProperties(Pageable pageable);

    @Query("SELECT new com.sam.exclusion.model.PropertyDetails(p, u, a) " +
            "FROM NyRentStabilizedPropertyAddress a " +
            "LEFT JOIN NyRentStabilizedProperty p ON p.ucbblNumber = a.ucbblNumber " +
            "LEFT JOIN NycStblzdPropertyData u ON a.ucbblNumber = u.ucbblNumber")
    List<PropertyDetails> getAllProperties();

    @Query("SELECT count(a) " +
            "FROM NyRentStabilizedPropertyAddress a " +
            "LEFT JOIN NyRentStabilizedProperty p ON p.ucbblNumber = a.ucbblNumber " +
            "LEFT JOIN NycStblzdPropertyData u ON a.ucbblNumber = u.ucbblNumber")
    long countAllAddresses();

}
