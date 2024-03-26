package digit.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-14T01:36:21.586087Z[Africa/Dakar]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
        @JsonProperty("tenantId")

                private String tenantId = null;

        @JsonProperty("latitude")

                private Double latitude = null;

        @JsonProperty("longitude")

                private Double longitude = null;

        @JsonProperty("addressId")

                private String addressId = null;

        @JsonProperty("addressNumber")

                private String addressNumber = null;

        @JsonProperty("addressLine1")

                private String addressLine1 = null;

        @JsonProperty("addressLine2")

                private String addressLine2 = null;

        @JsonProperty("landmark")

                private String landmark = null;

        @JsonProperty("city")

                private String city = null;

        @JsonProperty("pincode")

                private String pincode = null;

        @JsonProperty("detail")

                private String detail = null;

        @JsonProperty("id")

        private String id = null;

        @JsonProperty("doorNo")

        private String doorNo = null;

        @JsonProperty("buildingName")

        private String buildingName = null;

        @JsonProperty("type")

        private String type = null;

        @JsonProperty("street")

        private String street = null;

        @JsonProperty("registrationId")
        private String registrationId = null;
}
