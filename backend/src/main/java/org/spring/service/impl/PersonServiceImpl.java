package org.spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.security.JwtUtil;
import org.spring.model.PersonEntity;
import org.spring.model.RefreshToken;
import org.spring.dto.PersonDto;
import org.spring.exc.UserCommonException;
import org.spring.mapper.PersonMapper;
import org.springframework.web.bind.annotation.CookieValue;
import org.spring.repository.PersonRepository;
import org.spring.repository.RefreshTokenRepository;
import org.spring.service.PersonService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepo;
    private final PersonMapper mapper;
    private final RefreshTokenRepository rtRepo;
    private final JwtUtil jwt;
    private final PasswordEncoder encoder=new BCryptPasswordEncoder(10);

    @Override
    public PersonDto save(PersonDto dto) {

        var entity = mapper.toEntity(dto);

        if (personRepo.existsByLogin(entity.getLogin())) {
            throw new UserCommonException( 80801, "user already exist");
        }
        entity.setPassword(encoder.encode(dto.getPassword()));
        entity.setAuthority("ROLE_USER");
        var result = personRepo.save(entity);
        return mapper.toDto(result);
    }

    @Override
    public ResponseEntity<?> login(PersonDto dto) {

        var opt = personRepo.findByLogin(dto.getLogin());

        if (opt.isEmpty() || !encoder.matches(dto.getPassword(), opt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","bad credentials"));
        }

        PersonEntity user = opt.get();

        String access = jwt.createAccessToken(user.getId(), user.getLogin());
        String refresh = jwt.createRefreshToken(user.getId());
        RefreshToken rt = new RefreshToken(user.getId(), refresh, Instant.now().plusMillis(jwt.getRefreshMs()));
        rtRepo.save(rt);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true).secure(true).path("/") // secure=true in production
                .sameSite("Lax").maxAge(jwt.getRefreshMs()/1000)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Map.of("access_token", access, "userId", user.getId()));
    }

    @Override
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refresh) {
        if (refresh == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","no refresh token in cookie"));
        }
        try {
            var parsed = jwt.parse(refresh);
            Long uid = Long.valueOf(parsed.getBody().getSubject());

            var db = rtRepo.findByToken(refresh).orElseThrow();

            if (db.getExpiresAt().isBefore(Instant.now())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","refresh token expired"));
            }

            var user = personRepo.findById(uid).orElseThrow();

            String newAccess = jwt.createAccessToken(user.getId(), user.getLogin());
            String newRefresh = jwt.createRefreshToken(user.getId());
            RefreshToken newRt = new RefreshToken(user.getId(), newRefresh, Instant.now().plusMillis(jwt.getRefreshMs()));
            rtRepo.save(newRt);
            rtRepo.deleteByToken(refresh); // Удаляем старый refresh token

            ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefresh)
                    .httpOnly(true).secure(true).path("/") // secure=true in production
                    .sameSite("Lax").maxAge(jwt.getRefreshMs()/1000)
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Map.of("access_token", newAccess));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","bad refresh token"));
        }
    }

    @Override
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refresh) {
        if (refresh != null) {
            rtRepo.deleteByToken(refresh);
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(true).path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Map.of("ok",true));
    }
}
