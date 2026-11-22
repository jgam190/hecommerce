package com.br.unifor.auth.controller;

import com.br.unifor.auth.model.AppUser;
import com.br.unifor.auth.model.SessionToken;
import com.br.unifor.auth.repository.SessionRepository;
import com.br.unifor.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@RestController
public class UserController {
    private final UserRepository users;
    private final SessionRepository sessions;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public UserController(UserRepository users, SessionRepository sessions, PasswordEncoder encoder) {
        this.users = users;
        this.sessions = sessions;
        this.encoder = encoder;
    }

    record Credentials(String email, String senha) {}
    record Registration(String nome, String email, String senha) {}
    record UserView(String id, String nome, String email, String role) {}
    record LoginView(String token, UserView usuario) {}
    record PromoteRequest(String email) {}

    private UserView view(AppUser user) {
        return new UserView(user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private LoginView createSession(AppUser user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.save(new SessionToken(token, user.getId(), Instant.now().plus(12, ChronoUnit.HOURS)));
        return new LoginView(token, view(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Registration body) {
        String email = normalize(body.email());
        if (body.nome() == null || body.nome().isBlank() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                || body.senha() == null || body.senha().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Informe nome, e-mail válido e senha de pelo menos 8 caracteres."));
        }
        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", "E-mail já cadastrado."));
        }
        AppUser user = users.save(new AppUser(null, body.nome().trim(), email, encoder.encode(body.senha()), "USER"));
        return ResponseEntity.status(HttpStatus.CREATED).body(createSession(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Credentials body) {
        AppUser user = users.findByEmail(normalize(body.email())).orElse(null);
        if (user == null || body.senha() == null || !encoder.matches(body.senha(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "E-mail ou senha inválidos."));
        }
        return ResponseEntity.ok(createSession(user));
    }

    private AppUser authenticated(String header) {
        if (header == null || !header.startsWith("Bearer ")) return null;
        SessionToken session = sessions.findById(header.substring(7)).orElse(null);
        if (session == null || !session.getExpiresAt().isAfter(Instant.now())) return null;
        return users.findById(session.getUserId()).orElse(null);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        AppUser user = authenticated(authorization);
        return user == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(view(user));
    }

    @GetMapping("/permissions/admin")
    public ResponseEntity<Void> checkAdmin(@RequestHeader(value = "Authorization", required = false) String authorization) {
        AppUser user = authenticated(authorization);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return "ADMIN".equals(user.getRole()) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) sessions.deleteById(authorization.substring(7));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/admin/promote")
    public ResponseEntity<?> promote(@RequestHeader(value = "Authorization", required = false) String authorization,
                                     @RequestBody PromoteRequest body) {
        AppUser caller = authenticated(authorization);
        if (caller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"ADMIN".equals(caller.getRole())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        AppUser target = users.findByEmail(normalize(body.email())).orElse(null);
        if (target == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Usuário não encontrado."));
        target.setRole("ADMIN");
        return ResponseEntity.ok(view(users.save(target)));
    }
}
