package org.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity @Builder
@Getter @Setter
@Table(name = "core_set")
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_name")
    private String set;          // Core Set

    private Integer qty;

    private String name;

    private String type;         // Ship / Base

    private String faction;      // Blob / Trade Federation ...

    private Integer cost;

    private Integer defense;

    private String role; // Trade Deck / Explorer Pile

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AbilityEntity> abilities; // заменяет старое поле text
}

