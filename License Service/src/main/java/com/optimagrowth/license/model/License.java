package com.optimagrowth.license.model;

import com.optimagrowth.license.define.LicenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "license")
public class License extends BaseModel{

    @Indexed(unique = true)
    @NotBlank(message = "{message.license.id.blank}")
    @Size(min = 3, max = 50, message = "{message.license.id.size.alert}")
    private String licenseId;

    @NotBlank(message = "{message.license.description.blank}")
    @Size(min = 3, max = 50, message = "{message.license.description.size.alert}")
    private String description;

    @NotBlank(message = "{message.license.organization.id.blank}")
    @Size(min = 3, max = 50, message = "{message.license.organization.id.size.alert}")
    private String organizationId;

    @Indexed(unique = true)
    @NotBlank(message = "{message.license.product.name.blank}")
    @Size(min = 3, max = 50, message = "{message.license.product.name.size.alert}")
    private String productName;

    @NotBlank(message = "{message.license.type.blank}")
    @Pattern(regexp = "FULL|PARTIAL", message = "{message.license.type.not.valid.alert}")
    private LicenseType licenseType;
}
