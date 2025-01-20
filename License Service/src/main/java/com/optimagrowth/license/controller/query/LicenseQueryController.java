package com.optimagrowth.license.controller.query;

import com.optimagrowth.commonlibrary.core.common.GenericResponse;
import com.optimagrowth.license.dto.query.response.GetLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.service.query.LicenseQueryService;
import com.optimagrowth.license.validation.annotation.LicenseIdPathVariableExists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

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
}
