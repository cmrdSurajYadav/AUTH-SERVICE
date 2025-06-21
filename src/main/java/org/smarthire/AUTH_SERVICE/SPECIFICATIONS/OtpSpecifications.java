package org.smarthire.AUTH_SERVICE.SPECIFICATIONS;


import org.smarthire.AUTH_SERVICE.MODELS.OtpDB;
import org.springframework.data.jpa.domain.Specification;

public class OtpSpecifications {

    public static Specification<OtpDB> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<OtpDB> isExpired(Boolean expired) {
        return (root, query, cb) ->
                cb.equal(root.get("expired"), expired);
    }

    public static Specification<OtpDB> otpEquals(String otp) {
        return (root, query, cb) ->
                cb.equal(root.get("otp"), otp);
    }

    // Add more filters as needed
}
