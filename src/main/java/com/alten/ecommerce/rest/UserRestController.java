package com.alten.ecommerce.rest;

import com.alten.ecommerce.domain.User;
import com.alten.ecommerce.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> saveUser(@RequestBody User user){
        return ResponseEntity.ok().body(userService.saveUser(user));
    }

    @PostMapping("/users/addRole/{roleName}/toUser/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addRoleToUser(@PathVariable String roleName, @PathVariable String username){
        userService.addRoleToUser(roleName, username);
        return ResponseEntity.ok().body("Role '"+roleName+"' added to user '"+username+"'");
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }
}
