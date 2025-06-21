package org.smarthire.AUTH_SERVICE.DTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePasswordResponse {

    private boolean success;
    private String message;
    private String email;
    private Long userId;
    private int noOfAttampt;
}
