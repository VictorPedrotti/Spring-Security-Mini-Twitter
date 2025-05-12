package estudos.java.spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estudos.java.spring.security.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Role findByName(String name);
  
}
