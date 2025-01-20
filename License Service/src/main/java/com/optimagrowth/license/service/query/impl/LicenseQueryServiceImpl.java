package com.optimagrowth.license.service.query.impl;

import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.mapper.LicenseMapper;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.query.LicenseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;
@Service
@RequiredArgsConstructor
public class LicenseQueryServiceImpl implements LicenseQueryService {

    private final LicenseMapper licenseMapper;
    private final LicenseRepository licenseRepository;

    public GetLicenseResponse retrieveLicense(String licenseId, String organizationId) throws LicenseNotFoundException {

        License licenseToRetrieve = licenseRepository.findByLicenseIdAndOrganizationId(licenseId, organizationId).orElseThrow(
                () -> new LicenseNotFoundException(translate("exception.license.not.found.with.id", licenseId, organizationId))
        );

        return licenseMapper.mapToGetLicenseResponse(licenseToRetrieve);
    }
}
