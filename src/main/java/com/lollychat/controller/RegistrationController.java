package com.lollychat.controller;

import com.lollychat.model.ChatUser;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.securingweb.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.mail.internet.MimeMessage;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private Chatuserrepo chatuserrepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/email_validation")
    public ResponseEntity<String> emailValidation(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = jwtUtil.generateToken(email); // Використання JWT токена
        String confirmationUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/confirm")
                .queryParam("token", token)
                .toUriString();

        String subject = "Validation of e-mail";
        String message = "To validate please follow the link: " + confirmationUrl;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error sending email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Validation email sent successfully", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (chatuserrepo.existsByUsername(username)) {
            return new ResponseEntity<>("User with this username already exists.", HttpStatus.CONFLICT);
        }

        ChatUser user = new ChatUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        chatuserrepo.save(user);

        String token = jwtUtil.generateToken(username); // Генерація JWT токена після реєстрації

        return new ResponseEntity<>("Registration successful. Your token: " + token, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        ChatUser user = chatuserrepo.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(username); // Генерація JWT токена при успішному логіні
            return new ResponseEntity<>("Login successful. Your token: " + token, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        try {
            String email = jwtUtil.validateToken(token); // Валідація JWT токена
            ChatUser user = chatuserrepo.findByEmail(email);
            if (user != null) {
                user.setEnabled(true);
                chatuserrepo.save(user);
                return new ResponseEntity<>("Email confirmed successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }
    }
}
