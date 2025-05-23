package estudos.java.spring.security.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estudos.java.spring.security.dto.LoginRequest;
import estudos.java.spring.security.dto.LoginResponse;
import estudos.java.spring.security.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class TokenController {
  
  private final JwtEncoder jwtEncoder;
  private final UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder;

  public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.jwtEncoder = jwtEncoder;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

    var user = userRepository.findByUsername(loginRequest.username());

    if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
      throw new BadCredentialsException("user or password is invalid");
    }

    var now = Instant.now();
    var expiresIn = 300L;

    var scopes = user.get().getRole()
                .stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.joining(" "));
    
    var claims = JwtClaimsSet.builder()
          .issuer("mybackend")
          .subject(user.get().getUserId().toString())
          .issuedAt(now)
          .expiresAt(now.plusSeconds(expiresIn))
          .claim("scope", scopes)
          .build();

    var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
  }
}
