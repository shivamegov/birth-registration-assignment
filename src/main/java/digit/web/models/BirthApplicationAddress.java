package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * BirthApplicationAddress
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-14T01:36:21.586087Z[Africa/Dakar]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthApplicationAddress {
    @JsonProperty("id")
    @Valid
    private String id = null;

    @JsonProperty("tenantId")
    @NotNull

    @Size(min = 2, max = 64)
    private String tenantId = null;

    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("applicantAddress")
    @Valid
    private Address applicantAddress = null;

    @JsonProperty("registrationId")
    private String registrationId = null;


}
