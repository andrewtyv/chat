package com.lollychat.controller;

import com.lollychat.model.ChatUser;
import com.lollychat.repos.Chatuserrepo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private Chatuserrepo chatuserrepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/email_validation")
    public ResponseEntity<String> emailValidation(@RequestParam(defaultValue = "andrii.tivodar@gmail.com") String email) {
        String token = UUID.randomUUID().toString();
        System.out.println("here");
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
            helper.setText(message, true); // true for HTML content

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error sending email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Validation email sent successfully", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username,
                                               @RequestParam String email,
                                               @RequestParam String password) {
        if (chatuserrepo.existsByUsername(username)) {
            return new ResponseEntity<>("User with this username already exists.", HttpStatus.CONFLICT);
        }
        System.out.println("here");
        ChatUser user = new ChatUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        // Save user to the database
        chatuserrepo.save(user);

        return new ResponseEntity<>("Registration successful", HttpStatus.OK);
    }
    @PostMapping("/a")
    public ResponseEntity<String> handlePostRequest() {
        // Ваш код тут
        System.out.println("qeq");
        return ResponseEntity.ok("Запит отримано");
    }
}
