package com.optimagrowth.license.controller.query;

import com.optimagrowth.commonlibrary.core.common.GenericResponse;
import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.service.query.LicenseQueryService;
import com.optimagrowth.license.validation.annotation.LicenseIdPathVariableExists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/{organizationId}/license")
public class LicenseQueryController {

    private final LicenseQueryService licenseQueryService;

    @GetMapping("/{licenseId}")
    public ResponseEntity<GenericResponse<GetLicenseResponse>> retrieveLicense(
            @PathVariable
            @LicenseIdPathVariableExists
            String licenseId,
            @PathVariable
            String organizationId) throws LicenseNotFoundException {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GenericResponse.success(
                        licenseQueryService.retrieveLicense(licenseId, organizationId),
                        translate("success.license.retrieved.successfully")));
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<GetLicenseResponse>>> retrieveAllLicenses(
            @PathVariable
            String organizationId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GenericResponse.success(
                        licenseQueryService.retrieveAllLicenses(organizationId),
                        translate("success.licenses.retrieved.successfully")));
    }
}
