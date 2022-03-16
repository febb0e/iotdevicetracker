package de.thkoeln.syp.team17.backend.entities;

import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "verificationToken")
    private String verificationToken;

    @Enumerated
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    public UserRole getEffectiveRole() {
        if (totpAuthenticator == null || !totpAuthenticator.isVerified()) {
            return UserRole.USER;
        }
        return role;
    }

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<DeviceGroupUser> deviceGroupUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<RecoveryCode> recoveryCodes = new ArrayList<>();

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private TOTPAuthenticator totpAuthenticator;

}
