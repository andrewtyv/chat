package com.lollychat.controller;

import com.lollychat.dto.ApiResponse;
import com.lollychat.dto.ChatUserDTO;
import com.lollychat.model.ChatUser;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.repos.Friendshiprepo;
import com.lollychat.securingweb.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class ChatUserController {
    @Autowired
    private Chatuserrepo chatuserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Friendshiprepo friendshipRepo;


    @GetMapping("/me")
    public ResponseEntity<ChatUserDTO> info(HttpServletRequest request){
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser me = chatuserRepo.findByUsername(username);
        if (me != null && !me.getUsername().isEmpty()) {
            return new ResponseEntity<>( new ChatUserDTO(me.getEmail() , me.getUsername()), HttpStatus.OK);
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/delete")
    public void deleteUserAndFriendships(Long userId) {
        ChatUser user = chatuserRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        friendshipRepo.deleteBySenderOrReceiver(user, user);
        chatuserRepo.delete(user);
    }

}
