package com.optimagrowth.license.service.query;

import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;

public interface LicenseQueryService {

    GetLicenseResponse retrieveLicense(String licenseId, String OrganizationId) throws LicenseNotFoundException;
}
