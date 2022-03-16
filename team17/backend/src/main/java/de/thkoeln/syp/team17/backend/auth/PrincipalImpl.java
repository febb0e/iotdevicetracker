package de.thkoeln.syp.team17.backend.auth;

import de.thkoeln.syp.team17.backend.entities.User;

import java.io.Serializable;
import java.security.Principal;

public class PrincipalImpl implements Serializable, Principal {

    private String username;

    public PrincipalImpl(User user) {
        username = user.getUsername();
    }

    @Override
    public boolean equals(Object another){
        if (another instanceof PrincipalImpl) {
            return ((PrincipalImpl) another).getName().equals(getName());
        }
        return false;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

}
