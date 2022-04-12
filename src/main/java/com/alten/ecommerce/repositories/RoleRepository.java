package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.Role;
import com.alten.ecommerce.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(Roles roleName);

}
