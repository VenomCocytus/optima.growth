package com.optimagrowth.license.mapper;

import com.optimagrowth.license.dto.command.request.CreateLicenseRequest;
import com.optimagrowth.license.dto.command.response.CreateLicenseResponse;
import com.optimagrowth.license.dto.command.response.UpdateLicenseResponse;
import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.model.License;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    GetLicenseResponse mapToGetLicenseResponse(License License);
    CreateLicenseResponse mapToCreateLicenseResponse(License license);
    UpdateLicenseResponse mapToUpdateLicenseResponse(License license);
    License mapToLicense(CreateLicenseRequest createLicenseRequest);
}
