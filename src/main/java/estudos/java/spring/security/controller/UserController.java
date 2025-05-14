package estudos.java.spring.security.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import estudos.java.spring.security.dto.CreateUserDto;
import estudos.java.spring.security.entities.Role;
import estudos.java.spring.security.entities.User;
import estudos.java.spring.security.repository.RoleRepository;
import estudos.java.spring.security.repository.UserRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/auth")
public class UserController {
  
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private BCryptPasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository, RoleRepository roleRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody CreateUserDto createUserDto) {

    var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

    var userFromDb = userRepository.findByUsername(createUserDto.username());
    if (userFromDb.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    var user = new User();
    user.setUsername(createUserDto.username());
    user.setPassword(passwordEncoder.encode(createUserDto.password()));
    user.setRole(Set.of(basicRole));

    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<User>> listUsers() {
    var users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  
}
