package com.optimagrowth.license.dto.command.response;

import com.optimagrowth.license.define.LicenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import javax.annotation.Nullable;

public record UpdateLicenseResponse(
        @NotBlank(message = "{message.id.blank}")
        String id,
        @NotBlank(message = "{message.license.id.blank}")
        String licenseId,
        @NotBlank(message = "{The license description cannot be blank}")
        String description,
        @NotBlank(message = "{message.license.organization.id.blank}")
        String organizationId,
        @NotBlank(message = "{message.license.product.name.blank}")
        String productName,
        @NotBlank(message = "{message.license.type.blank}")
        @Pattern(regexp = "FULL|PARTIAL" ,message = "{message.license.type.not.valid.alert}")
        LicenseType licenseType,
        @Nullable
        @Size(min = 3, max = 50, message = "{message.license.comment.size.alert}")
        String comment
) {
}
