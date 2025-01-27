package com.optimagrowth.license.service.query.impl;

import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.mapper.LicenseMapper;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.query.LicenseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;
@Service
@RequiredArgsConstructor
public class LicenseQueryServiceImpl implements LicenseQueryService {

    private final LicenseMapper licenseMapper;
    private final LicenseRepository licenseRepository;

    public GetLicenseResponse retrieveLicense(String licenseId, String organizationId) throws LicenseNotFoundException {

        return licenseRepository
                .findByLicenseIdAndOrganizationId(licenseId, organizationId)
                .map(licenseMapper::mapToGetLicenseResponse)
                .orElseThrow(
                        () -> new LicenseNotFoundException(translate(
                                "exception.license.not.found.with.id", licenseId, organizationId)));
    }

    public List<GetLicenseResponse> retrieveAllLicenses(String organizationId) {

        return licenseRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(licenseMapper::mapToGetLicenseResponse)
                .collect(Collectors.toList());
    }
}
