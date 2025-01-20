package com.optimagrowth.license.controller.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.commonlibrary.core.common.GenericResponse;
import com.optimagrowth.license.dto.command.request.CreateLicenseRequest;
import com.optimagrowth.license.dto.command.response.CreateLicenseResponse;
import com.optimagrowth.license.dto.command.response.UpdateLicenseResponse;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import com.optimagrowth.license.service.command.LicenseCommandService;
import com.optimagrowth.license.validation.annotation.LicenseIdPathVariableExists;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/{organizationId}/license")
public class LicenseCommandController {

    private final LicenseCommandService licenseCommandService;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<CreateLicenseResponse>> createLicense(
            @Valid
            @RequestBody
            CreateLicenseRequest createLicenseRequest,
            @PathVariable
            String organizationId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GenericResponse.success(
                        licenseCommandService.createLicense(createLicenseRequest, organizationId),
                        translate("success.license.created.successfully")
                ));
    }

    @PatchMapping("/{licenseId}")
    public ResponseEntity<GenericResponse<UpdateLicenseResponse>> updateLicense(
            @PathVariable
            @LicenseIdPathVariableExists
            String licenseId,
            @PathVariable
            String organizationId,
            JsonPatch jsonPatch)
            throws JsonPatchException, LicenseNotFoundException, JsonProcessingException {

        return ResponseEntity
                .ok(GenericResponse.success(
                        licenseCommandService.updateLicense(licenseId, organizationId, jsonPatch),
                        translate("success.license.updated.successfully")
                ));
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<GenericResponse<?>> deleteLicense(
            @PathVariable
            @LicenseIdPathVariableExists
            String licenseId,
            @PathVariable
            String organizationId) {

        licenseCommandService.deleteLicense(licenseId, organizationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(GenericResponse.success(
                        translate("success.license.deleted.successfully")
                ));
    }
}
