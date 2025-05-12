package estudos.java.spring.security.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import estudos.java.spring.security.entities.Role;
import estudos.java.spring.security.entities.User;
import estudos.java.spring.security.repository.RoleRepository;
import estudos.java.spring.security.repository.UserRepository;
import jakarta.transaction.Transactional;

@Configuration
public class AdminUserConfig implements CommandLineRunner{
  
  private RoleRepository roleRepository;
  private UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder;

  public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {

    var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
    
    userRepository.findByUsername("admin").ifPresentOrElse(
        user -> {
            System.out.println("admin already exists");
        },
        () -> {
            var user = new User(); 
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Set.of(roleAdmin)); 
            userRepository.save(user);
        }
    );
  }
  
}
