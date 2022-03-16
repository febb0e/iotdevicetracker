package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameLike(String username, Pageable pageable);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
