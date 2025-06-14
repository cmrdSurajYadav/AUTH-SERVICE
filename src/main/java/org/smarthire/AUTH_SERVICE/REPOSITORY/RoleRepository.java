package org.smarthire.AUTH_SERVICE.REPOSITORY;

import org.smarthire.AUTH_SERVICE.MODELS.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long>, JpaSpecificationExecutor<Role> {



    Role findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}
