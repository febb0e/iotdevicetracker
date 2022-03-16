package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.TOTPAuthenticator;
import org.springframework.data.repository.CrudRepository;

public interface TotpAuthenticatorRepository extends CrudRepository<TOTPAuthenticator, Long> {

}
