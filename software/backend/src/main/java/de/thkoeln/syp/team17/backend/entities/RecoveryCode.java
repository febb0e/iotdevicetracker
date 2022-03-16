package de.thkoeln.syp.team17.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "recovery_code", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class RecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recovery_code_generator")
    @SequenceGenerator(name = "recovery_code_generator", sequenceName = "recovery_code_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", nullable = false)
    private String code;


}
