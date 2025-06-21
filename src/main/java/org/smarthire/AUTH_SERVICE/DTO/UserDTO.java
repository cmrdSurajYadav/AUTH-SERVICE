package org.smarthire.AUTH_SERVICE.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smarthire.AUTH_SERVICE.MODELS.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private Boolean enable;
    private Role role; // Or you can create RoleDTO if needed
    private JwtAuthenticationResponse jwtAuthenticationResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedTime;
}
