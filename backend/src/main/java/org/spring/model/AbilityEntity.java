package org.spring.model;

import jakarta.persistence.*;
import lombok.*;
import org.spring.enums.AbilityType;

@NoArgsConstructor
@AllArgsConstructor
@Entity @Builder
@Getter @Setter
@Table(name = "abilities")
public class AbilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @Enumerated(EnumType.STRING)
    private AbilityType type;

    private Integer value;
    private String condition;
}
