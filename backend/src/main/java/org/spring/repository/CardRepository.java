package org.spring.repository;

import org.spring.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    @Query("""
    select distinct c
    from CardEntity c
    left join fetch c.abilities
    """)
    List<CardEntity> findAllWithAbilities();

}
