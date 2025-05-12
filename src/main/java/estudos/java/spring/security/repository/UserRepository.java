package estudos.java.spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estudos.java.spring.security.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
}
