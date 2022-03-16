package de.thkoeln.syp.team17.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "device", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_generator")
    @SequenceGenerator(name = "device_generator", sequenceName = "device_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_group_id", nullable = false)
    private DeviceGroup deviceGroup;

    @Column(name = "identifier", nullable = false, unique = true, length = 100)
    private String identifier;

    @Column(name = "name")
    private String name;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
