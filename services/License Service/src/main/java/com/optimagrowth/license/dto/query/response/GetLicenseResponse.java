package com.optimagrowth.license.dto.query.response;

import com.optimagrowth.license.define.LicenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetLicenseResponse extends RepresentationModel<GetLicenseResponse> {
        @NotBlank(message = "{message.id.blank}")
        private String id;
        @NotBlank(message = "{message.license.id.blank}")
        private String licenseId;
        @NotBlank(message = "{message.license.description.blank}")
        private String description;
        @NotBlank(message = "{message.license.organization.id.blank}")
        private String organizationId;
        @NotBlank(message = "{message.license.product.name.blank}")
        private String productName;
        @NotNull(message = "{message.license.type.null}")
        private LicenseType licenseType;
        private String comment;
}
