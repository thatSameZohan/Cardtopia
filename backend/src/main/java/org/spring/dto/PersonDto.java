package org.spring.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PersonDto {

    private Long id;

    private String login;

    private String password;

    private String authority;

}
