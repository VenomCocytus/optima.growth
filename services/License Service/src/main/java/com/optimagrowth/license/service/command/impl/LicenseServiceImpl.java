package com.optimagrowth.license.service.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.license.dto.command.request.CreateLicenseRequest;
import com.optimagrowth.license.dto.command.response.CreateLicenseResponse;
import com.optimagrowth.license.dto.command.response.UpdateLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.mapper.LicenseMapper;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.command.LicenseCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseCommandService {

    private final ObjectMapper objectMapper;
    private final LicenseMapper licenseMapper;
    private final LicenseRepository licenseRepository;


    @Override
    public CreateLicenseResponse createLicense(CreateLicenseRequest createLicenseRequest, String organizationId) {

        License licenseToCreate = licenseMapper.mapToLicense(createLicenseRequest);
        licenseToCreate.setOrganizationId(organizationId);

        licenseRepository.save(licenseToCreate);

        return licenseMapper.mapToCreateLicenseResponse(licenseToCreate);
    }

    @Override
    public UpdateLicenseResponse updateLicense(String licenseId, String organizationId, JsonPatch jsonPatch)
            throws LicenseNotFoundException, JsonPatchException, JsonProcessingException {

        License licenseToUpdate = licenseRepository.findByLicenseIdAndOrganizationId(licenseId, organizationId).orElseThrow(
                () -> new LicenseNotFoundException(translate("exception.license.not.found.with.id", licenseId, organizationId))
        );
        License licensePatched = applyPatchToLicense(jsonPatch, licenseToUpdate);
        licenseRepository.save(licensePatched);

        return licenseMapper.mapToUpdateLicenseResponse(licensePatched);
    }

    @Override
    public void deleteLicense(String licenseId, String organizationId) {
        licenseRepository.deleteByLicenseIdAndOrganizationId(licenseId, organizationId);
    }

    private License applyPatchToLicense(JsonPatch jsonPatch, License targetLicense)
            throws JsonPatchException, JsonProcessingException {

        JsonNode jsonPatched =  jsonPatch.apply(objectMapper.convertValue(targetLicense, JsonNode.class));

        return objectMapper.treeToValue(jsonPatched,License.class);
    }
}
