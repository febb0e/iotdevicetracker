package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.RecoveryCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecoveryCodeRepository extends CrudRepository<RecoveryCode, Long> {

    void deleteByUserId(long userId);

    Optional<RecoveryCode> findByUserIdAndCode(long userId, String code);

}
