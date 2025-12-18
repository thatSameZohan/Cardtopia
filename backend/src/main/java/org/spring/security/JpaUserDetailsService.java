package org.spring.security;

import org.spring.model.PersonEntity;
import org.spring.repository.PersonRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public JpaUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PersonEntity person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(person.getUsername())
                .password(person.getPassword())
                .authorities(person.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!person.isEnabled())
                .build();
    }
}
