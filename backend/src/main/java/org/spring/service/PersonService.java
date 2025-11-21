package org.spring.service;

import org.spring.dto.PersonDto;
import org.springframework.http.ResponseEntity;

public interface PersonService {

    PersonDto save(PersonDto dto);

    ResponseEntity<?> login (PersonDto dto);

    ResponseEntity<?> refresh(String refresh);

    ResponseEntity<?> logout(String refresh);
}
