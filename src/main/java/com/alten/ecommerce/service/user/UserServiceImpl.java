package com.alten.ecommerce.service.user;

import com.alten.ecommerce.domain.Role;
import com.alten.ecommerce.domain.User;
import com.alten.ecommerce.enums.Roles;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.RoleRepository;
import com.alten.ecommerce.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Utility method: check if user already exists
    public boolean checkUser(User user){
        List<User> users = userRepository.findAll();
        if(users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()) || u.getEmail().equals(user.getEmail()))){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user '{}' to database...", user.getUsername());
        if(checkUser(user)){
            log.error("Error! Username '{} 'or email '{}' already in use", user.getUsername(), user.getEmail());
            throw new CustomException("Error! Username '"+user.getUsername()+"' or email '"+user.getEmail()+"' already in use. Choose others");
        }
        user.setId(0L);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User '{}' saved to database", user.getUsername());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Getting user with id '{}'...", id);
        Optional<User> optionalUser = userRepository.findById(id);
        User user;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }else{
            log.error("Error! User doesn't exists: id '{}' not found in the database", id);
            throw new CustomException("Error! User doesn't exists : id '"+id+"' not found in the database");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        log.info("Getting user by username...");
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }else{
            log.error("Error! User doesn't exists: username '{}' not found in the database", username);
            throw new CustomException("Error! User doesn't exists: username '"+username+"' not found in the database");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Getting user by email...");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }else{
            log.error("Error! User doesn't exists: email '{}' not found in the database", email);
            throw new CustomException("Error! User doesn't exists: email '"+email+"' not found in the database");
        }
        return user;
    }

    @Override
    public Set<User> getUsers() {
        log.info("Getting all users...");
        return userRepository.findAll().stream().collect(Collectors.toSet());
    }

    @Override
    public User updateUser(User user) {
        log.info("Update user '{}' ...", user.getUsername());
        if(checkUser(user)){
            log.error("Error! Username or email already in use: choose others");
            throw new CustomException("Error! Username or email already in use: choose others");
        }else{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.saveAndFlush(user);
            log.info("User updated successfully");
            return user;
        }
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Deleting user with id '{}'...", id);
        userRepository.deleteById(id);
        log.info("User '{}' deleted", id);
    }

    @Override
    public void deleteUserByEmail(String email) {
        log.info("Deleting user with email '{}'...", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
            userRepository.delete(user);
            log.info("User with email '{}' deleted", email);
        }else{
            log.error("Error! User doesn't exists: email '{}' not found in the database", email);
            throw new CustomException("Error! User doesn't exists: email '"+email+"' not found in the database");
        }
    }

    @Override
    public void addRoleToUser(String roleName, String username) {
        Optional<Role> optionalRole = roleRepository.findByRoleName(Roles.valueOf(roleName));
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Role role;
        User user;
        if(optionalRole.isPresent() && optionalUser.isPresent()){
            log.info("Adding role '{}' to user '{}'", roleName, username);
            role = optionalRole.get();
            user = optionalUser.get();
            if(user.getRoles().contains(role)){
                log.error("Error! User '{}' has already '{}' role", username, roleName);
                throw new CustomException("Error! User '"+username+"' has already '"+roleName+"' role");
            }else{
                user.getRoles().add(role);
            }
        }else{
            log.error("Error! User or role do not exist: username '{}' or role '{}' not found in the database", username, roleName);
            throw new CustomException("Error! User or role do not exist: username '"+username+"' or role '"+roleName+"' not found in the database");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).get();
        if(user == null){
            log.error("Error! User doesn't exists: username not found in the database");
            throw new UsernameNotFoundException("Error! Username '"+user.getUsername()+"' not found int the database");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName().name())));
        return  new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
