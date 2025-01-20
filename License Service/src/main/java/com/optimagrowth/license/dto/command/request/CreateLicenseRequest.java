package com.optimagrowth.license.dto.command.request;

import com.optimagrowth.license.define.LicenseType;
import com.optimagrowth.license.validation.annotation.LicenseIdNotAlreadyExists;
import com.optimagrowth.license.validation.annotation.ProductNameNotAlreadyExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLicenseRequest(
        @LicenseIdNotAlreadyExists
        @NotBlank(message = "{message.license.id.blank}")
        @Size(min = 3, max = 50, message = "{message.license.id.size.alert}")
        String licenseId,

        @NotBlank(message = "{The license description cannot be blank}")
        @Size(min = 3, max = 50, message = "{message.license.description.size.alert}")
        String description,

        @ProductNameNotAlreadyExists
        @NotBlank(message = "{message.license.product.name.blank}")
        @Size(min = 3, max = 50, message = "{message.license.product.name.size.alert}")
        String productName,

        @NotNull(message = "{message.license.type.null}")
        LicenseType licenseType
) {
}
