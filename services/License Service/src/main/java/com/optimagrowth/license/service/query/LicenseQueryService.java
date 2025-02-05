package com.optimagrowth.license.service.query;

import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;

import java.util.List;

public interface LicenseQueryService {

    GetLicenseResponse retrieveLicense(String licenseId, String organizationId) throws LicenseNotFoundException;
    List<GetLicenseResponse> retrieveAllLicenses(String organizationId);
}
