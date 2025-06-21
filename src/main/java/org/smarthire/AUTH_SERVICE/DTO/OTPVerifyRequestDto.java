package org.smarthire.AUTH_SERVICE.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTPVerifyRequestDto {
    private Long userId;
    @JsonProperty("OTP")
    private Long OTP;
}
