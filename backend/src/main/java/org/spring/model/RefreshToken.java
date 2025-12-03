package org.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
@Table(name = "refresh_tokens")
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    private boolean revoked = false;

    private Instant createdAt = Instant.now();

}