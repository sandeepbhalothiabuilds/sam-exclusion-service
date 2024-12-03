package com.sam.exclusion.service.impl;

import com.sam.exclusion.entity.NyRentStabilizedProperty;
import com.sam.exclusion.entity.NyRentStabilizedPropertyAddress;
import com.sam.exclusion.entity.NycStblzdPropertyData;
import com.sam.exclusion.model.PropertyDetails;
import com.sam.exclusion.repository.NycRcuListingsAddressRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

@Service
public class NYRentStbLzdPropertyService {

    @Autowired
    private NycRcuListingsAddressRepository addrRepository;

    @PersistenceContext
    EntityManager entityManager;


    public List<PropertyDetails> getPropertyDetails(int offset) {
        Pageable pagable = PageRequest.of(offset, offset+50);
        return addrRepository.getAllProperties(pagable);
    }

    public List<PropertyDetails> getPropertyDetails() {
        return addrRepository.getAllProperties();
    }

    public Long getPropertyDetailsCount() {
        return addrRepository.countAllAddresses();
    }

    public Long countByCriteria(String zipcode, String borough , String buildingNumber, String street, String stateSuffix){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<NyRentStabilizedPropertyAddress> addressRoot = query.from(NyRentStabilizedPropertyAddress.class);
        List<Predicate> predicates = new ArrayList<>();
        // Join with custom condition: ucbblNumber = ucbbl
        if (zipcode != null) {
            Predicate zipcodeCondition = criteriaBuilder.like(addressRoot.get("zip"), "%"+zipcode+"%");
            //query.where(zipcodeCondition);
            predicates.add(zipcodeCondition);
        }
        if (borough != null) {
            Predicate boroughCondition = criteriaBuilder.equal(addressRoot.get("borough"), borough);
            // query.where(boroughCondition);
            predicates.add(boroughCondition);
        }
        if (buildingNumber != null) {
            Predicate buildingNumberCondition = criteriaBuilder.equal(addressRoot.get("buildingNumber"), buildingNumber);
            //query.where(buildingNumberCondition);
            predicates.add(buildingNumberCondition);
        }
        if (street != null) {
            Predicate streetCondition = criteriaBuilder.equal(addressRoot.get("street"), street);
            predicates.add(streetCondition);
            // query.where(streetCondition);
        }
        if (stateSuffix != null) {
            Predicate stateSuffixCondition = criteriaBuilder.equal(addressRoot.get("stateSuffix"), stateSuffix);
            //query.where(stateSuffixCondition);
            predicates.add(stateSuffixCondition);
        }
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        query.select(criteriaBuilder.count(addressRoot));
        return entityManager.createQuery(query).getSingleResult();
    }
    public List<PropertyDetails> findAllByCriteria(String zipcode, String borough , String buildingNumber, String street, String stateSuffix, int offset ){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PropertyDetails> query = criteriaBuilder.createQuery(PropertyDetails.class);
        Root<NyRentStabilizedPropertyAddress> addressRoot = query.from(NyRentStabilizedPropertyAddress.class);
        Root<NyRentStabilizedProperty> dataRoot = query.from(NyRentStabilizedProperty.class);
        Root<NycStblzdPropertyData> unitsRoot = query.from(NycStblzdPropertyData.class);

        List<Predicate> predicates = new ArrayList<>();
        Predicate joinCondition1 = criteriaBuilder.equal(addressRoot.get("ucbblNumber"), dataRoot.get("ucbblNumber"));
        predicates.add(joinCondition1);
        //query.where(joinCondition1);
        // Join with custom condition: ucbblNumber = ucbbl
        Predicate joinCondition = criteriaBuilder.equal(addressRoot.get("ucbblNumber"), unitsRoot.get("ucbblNumber"));
        //query.where(joinCondition);
        predicates.add(joinCondition);

        if (zipcode != null) {
            Predicate zipcodeCondition = criteriaBuilder.like(addressRoot.get("zip"), "%"+zipcode+"%");
            //query.where(zipcodeCondition);
            predicates.add(zipcodeCondition);
        }
        if (borough != null) {
            Predicate boroughCondition = criteriaBuilder.equal(addressRoot.get("borough"), borough);
           // query.where(boroughCondition);
            predicates.add(boroughCondition);
        }
        if (buildingNumber != null) {
            Predicate buildingNumberCondition = criteriaBuilder.equal(addressRoot.get("buildingNumber"), buildingNumber);
            //query.where(buildingNumberCondition);
            predicates.add(buildingNumberCondition);
        }
        if (street != null) {
            Predicate streetCondition = criteriaBuilder.equal(addressRoot.get("street"), street);
            predicates.add(streetCondition);
           // query.where(streetCondition);
        }
        if (stateSuffix != null) {
            Predicate stateSuffixCondition = criteriaBuilder.equal(addressRoot.get("stateSuffix"), stateSuffix);
            //query.where(stateSuffixCondition);
            predicates.add(stateSuffixCondition);
        }
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        query.select(criteriaBuilder.construct(
                PropertyDetails.class,
                dataRoot,
                unitsRoot,
                addressRoot

        )).orderBy(criteriaBuilder.asc(addressRoot.get("addressId")));
        return entityManager.createQuery(query).setFirstResult(offset) // offset
                .setMaxResults(50) // limit
                .getResultList();
    }

}
