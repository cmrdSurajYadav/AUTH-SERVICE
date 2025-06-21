package org.smarthire.AUTH_SERVICE.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OtpResponseDTO {

    private Boolean otpMatch;
    private String message;
}
