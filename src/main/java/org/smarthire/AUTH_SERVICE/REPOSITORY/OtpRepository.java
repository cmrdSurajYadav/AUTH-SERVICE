package org.smarthire.AUTH_SERVICE.REPOSITORY;

import org.smarthire.AUTH_SERVICE.MODELS.OtpDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<OtpDB,Long>, JpaSpecificationExecutor<OtpDB> {

    List<OtpDB> findByUserId(Long userID);
    OtpDB findByUserIdAndOtp(Long userId, Long otp);
}
