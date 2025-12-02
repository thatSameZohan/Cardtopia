package org.spring.repository;

import jakarta.transaction.Transactional;
import org.spring.model.PersonEntity;
import org.spring.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByPerson(PersonEntity person);
}