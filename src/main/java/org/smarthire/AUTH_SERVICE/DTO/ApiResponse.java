package org.smarthire.AUTH_SERVICE.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class ApiResponse<T> {
    private boolean success;
    private Integer status;
    private String timeStamp;
    private T data;
    private ErrorResponse errorResponse;

    public ApiResponse(){
        this.timeStamp = LocalDateTime.now().toString();
    }

    public ApiResponse(T data){
        this.data = data;
        this.success=true;
        this.status=200;
        this.timeStamp = LocalDateTime.now().toString();
    }
    public ApiResponse(ErrorResponse errorResponse){
        this.status = errorResponse.getStatus();
        this.success= false;
        this.timeStamp = LocalDateTime.now().toString();
        this.errorResponse = errorResponse;
    }
}
