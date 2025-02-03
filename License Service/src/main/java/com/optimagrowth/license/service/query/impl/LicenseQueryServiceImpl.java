package com.optimagrowth.license.service.query.impl;

import com.optimagrowth.license.controller.command.LicenseCommandController;
import com.optimagrowth.license.controller.query.LicenseQueryController;
import com.optimagrowth.license.dto.command.request.CreateLicenseRequest;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class LicenseQueryServiceImpl implements LicenseQueryService {

    private final LicenseMapper licenseMapper;
    private final LicenseRepository licenseRepository;

    public GetLicenseResponse retrieveLicense(String licenseId, String organizationId) throws LicenseNotFoundException {

        GetLicenseResponse licenseResponse = licenseRepository
                .findByLicenseIdAndOrganizationId(licenseId, organizationId)
                .map(licenseMapper::mapToGetLicenseResponse)
                .orElseThrow(
                        () -> new LicenseNotFoundException(translate(
                                "exception.license.not.found.with.id", licenseId, organizationId)));

        addLinks(licenseResponse, licenseId, organizationId);

        return licenseResponse;
    }

    public List<GetLicenseResponse> retrieveAllLicenses(String organizationId) {

        return licenseRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(licenseMapper::mapToGetLicenseResponse)
                .collect(Collectors.toList());
    }

    private void addLinks(GetLicenseResponse response, String licenseId, String organizationId) {
        response.add(
                linkTo(methodOn(LicenseQueryController.class).retrieveLicense(organizationId, licenseId)).withSelfRel(),
                linkTo(methodOn(LicenseCommandController.class).createLicense(
                        new CreateLicenseRequest(licenseId, response.getDescription(), response.getProductName(), response.getLicenseType()),
                        organizationId)).withRel(translate("link.create.license")),
                // linkTo(methodOn(LicenseCommandController.class).updateLicense(licenseId, organizationId, new JsonPatch())).withRel(translate("link.update.license"),
                linkTo(methodOn(LicenseCommandController.class).deleteLicense(licenseId, organizationId)).withRel(translate("link.delete.license")));
    }
}
