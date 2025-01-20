package com.optimagrowth.license.service.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.license.dto.command.request.CreateLicenseRequest;
import com.optimagrowth.license.dto.command.response.CreateLicenseResponse;
import com.optimagrowth.license.dto.command.response.UpdateLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;

public interface LicenseCommandService {

    CreateLicenseResponse createLicense(CreateLicenseRequest createLicenseRequest, String organizationId);
    UpdateLicenseResponse updateLicense(String licenseId, String organizationId, JsonPatch jsonPatch) throws LicenseNotFoundException, JsonPatchException, JsonProcessingException;
    void deleteLicense(String licenseId, String organizationId);
}
