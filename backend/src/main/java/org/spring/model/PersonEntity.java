package org.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "persons")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_roles", joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "role")
    private Set<String> roles;

    private boolean enabled = true;

    private Instant createdAt = Instant.now();
}
