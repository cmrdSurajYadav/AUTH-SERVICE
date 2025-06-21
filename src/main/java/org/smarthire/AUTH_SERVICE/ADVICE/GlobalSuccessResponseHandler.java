package org.smarthire.AUTH_SERVICE.ADVICE;

import org.smarthire.AUTH_SERVICE.DTO.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URI;

@ControllerAdvice
public class GlobalSuccessResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        URI requestPath = request.getURI();

        if (!requestPath.getRawPath().equals("/v3/api-docs")) {
            if (body instanceof String || body instanceof ApiResponse<?>) {
                return body;
            }
            return new ApiResponse<>(body);
        }
        return body;
    }

}
