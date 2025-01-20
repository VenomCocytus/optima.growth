package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends MongoRepository<License, String> {

    boolean existsByLicenseId(String licenseId);
    boolean existsByProductName(String productName);
    Optional<License> findByLicenseIdAndOrganizationId(String licenseId, String organizationId);
    void deleteByLicenseIdAndOrganizationId(String licenseId, String organizationId);
}
