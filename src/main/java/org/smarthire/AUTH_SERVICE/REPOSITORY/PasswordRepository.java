package org.smarthire.AUTH_SERVICE.REPOSITORY;

import org.smarthire.AUTH_SERVICE.MODELS.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password,Long> {

    List<Password> findByUserId(Long userId);
}
