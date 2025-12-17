package org.spring.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorDto {

    private Integer code;
    private String message;
}