package com.alten.ecommerce.service.user;

import com.alten.ecommerce.domain.User;

import java.util.Set;

public interface UserService {

    User saveUser(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    Set<User> getUsers();
    User updateUser(User user);
    void deleteUserById(Long id);
    void deleteUserByEmail(String email);
    void addRoleToUser(String roleName, String username);

}
