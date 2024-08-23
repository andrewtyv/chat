package com.lollychat.controller;

import com.lollychat.model.ChatUser;
import com.lollychat.model.Friendship;
import com.lollychat.model.FriendshipStatus;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.repos.Friendshiprepo;
import com.lollychat.securingweb.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendshipController {

    @Autowired
    Friendshiprepo friendshipRepo;

    @Autowired
    Chatuserrepo chatuserRepo;

    @Autowired
    JwtUtil jwtUtil; // Ін'єкція JwtUtil для роботи з токенами

    // Додавання заявки на дружбу
    @PostMapping("/request")
    public ResponseEntity<String> addFriendRequest(HttpServletRequest request, @RequestParam String recivier_name) {
        // Отримання імені відправника з токена
        String sendler_name = jwtUtil.validateToken(extractToken(request));

        if (chatuserRepo.existsByUsername(recivier_name) && chatuserRepo.existsByUsername(sendler_name)) {
            ChatUser sendler = chatuserRepo.findByUsername(sendler_name);
            ChatUser recivier = chatuserRepo.findByUsername(recivier_name);
            friendshipRepo.save(new Friendship(sendler, recivier, FriendshipStatus.PENDING));

            return new ResponseEntity<>("Need to approve", HttpStatus.OK);
        }

        return new ResponseEntity<>("User with username " + recivier_name + " does not exist", HttpStatus.CONFLICT);
    }

    // Отримання всіх запитів на дружбу для користувача
    @GetMapping("/allrequests")
    public ResponseEntity<List<Friendship>> getAllRequests(HttpServletRequest request) {
        // Отримання імені користувача з токена
        String username = jwtUtil.validateToken(extractToken(request));

        if (chatuserRepo.existsByUsername(username)) {
            ChatUser recivier = chatuserRepo.findByUsername(username);
            List<Friendship> requests = friendshipRepo.findByReceiverAndStatus(recivier, FriendshipStatus.PENDING);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Підтвердження заявки на дружбу
    @PostMapping("/approve")
    public ResponseEntity<String> approveRequest(HttpServletRequest request, @RequestParam Long requestId) {
        String username = jwtUtil.validateToken(extractToken(request));
        Friendship friendship = friendshipRepo.findById(requestId).orElse(null);

        if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING &&
                friendship.getReceiver().getUsername().equals(username)) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepo.save(friendship);
            return new ResponseEntity<>("Friendship approved", HttpStatus.OK);
        }

        return new ResponseEntity<>("Request not found or already processed", HttpStatus.NOT_FOUND);
    }

    // Відхилення заявки на дружбу
    @PostMapping("/reject")
    public ResponseEntity<String> rejectRequest(HttpServletRequest request, @RequestParam Long requestId) {
        String username = jwtUtil.validateToken(extractToken(request));
        Friendship friendship = friendshipRepo.findById(requestId).orElse(null);

        if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING &&
                friendship.getReceiver().getUsername().equals(username)) {
            friendship.setStatus(FriendshipStatus.REJECTED);
            friendshipRepo.save(friendship);
            return new ResponseEntity<>("Friendship rejected", HttpStatus.OK);
        }

        return new ResponseEntity<>("Request not found or already processed", HttpStatus.NOT_FOUND);
    }

    // Витяг токена з заголовку запиту
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
