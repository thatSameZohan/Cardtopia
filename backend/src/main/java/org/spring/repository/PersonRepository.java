package org.spring.repository;

import org.spring.model.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    Optional<PersonEntity> findByLogin(String login);

    boolean existsByLogin(String login);
}
