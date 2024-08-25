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

    public ApiResponse emailValidation(String email) {

        String token = jwtUtil.generateToken(email);
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
            return new ApiResponse("Error sending email", null);
        }

        return new ApiResponse("Validation email sent successfully", null);
    }

    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (chatuserrepo.existsByUsername(username)) {
            return new ApiResponse("User with this username already exists.", null);
        }

        ApiResponse emailValidationResponse = emailValidation(email);
        if (!emailValidationResponse.getMessage().equals("Validation email sent successfully")) {
            return new ApiResponse("Error sending validation email.", null);
        }

        ChatUser user = new ChatUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);

        chatuserrepo.save(user);

        String token = jwtUtil.generateToken(username);

        return new ApiResponse("Registration successful. Please validate your email.", token);
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        ChatUser user = chatuserrepo.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword()) && user.isEnabled()) {
            String token = jwtUtil.generateToken(username);
            return new ApiResponse("Login successful.", token);
        }
        return new ApiResponse("Invalid credentials", null);
    }

    @GetMapping("/confirm")
    public ApiResponse confirmEmail(@RequestParam String token) {
        try {
            String email = jwtUtil.validateToken(token);
            ChatUser user = chatuserrepo.findByEmail(email);
            if (user != null) {
                user.setEnabled(true);
                chatuserrepo.save(user);
                return new ApiResponse("Email confirmed successfully", null);
            } else {
                return new ApiResponse("User not found", null);
            }
        } catch (Exception e) {
            return new ApiResponse("Invalid or expired token", null);
        }
    }
}
