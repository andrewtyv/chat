package com.lollychat.controller;

import com.lollychat.dto.ApiResponseWrapper;
import com.lollychat.dto.FriendshipDTO;
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

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friend")
public class FriendshipController {

    @Autowired
    private Friendshiprepo friendshipRepo;

    @Autowired
    private Chatuserrepo chatuserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/request")
    public ResponseEntity<ApiResponseWrapper<String>> addFriendRequest(HttpServletRequest request, @RequestBody Map<String, String> receiverName) {
        String senderName = jwtUtil.validateToken(extractToken(request));
        if (chatuserRepo.existsByUsername(receiverName.get("username")) && chatuserRepo.existsByUsername(senderName)) {
            ChatUser sender = chatuserRepo.findByUsername(senderName);
            ChatUser receiver = chatuserRepo.findByUsername(receiverName.get("username"));
            friendshipRepo.save(new Friendship(sender, receiver, FriendshipStatus.PENDING));

            return ResponseEntity.ok(new ApiResponseWrapper<>( "Need to approve", null));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "User with username " + receiverName.get("username") + " does not exist", null), HttpStatus.CONFLICT);
    }

    @GetMapping("/allrequests")
    public ResponseEntity<ApiResponseWrapper<List<FriendshipDTO>>> getAllRequests(HttpServletRequest request) {
        String username = jwtUtil.validateToken(extractToken(request));

        if (chatuserRepo.existsByUsername(username)) {
            ChatUser receiver = chatuserRepo.findByUsername(username);
            List<Friendship> requests = friendshipRepo.findByReceiverAndStatus(receiver, FriendshipStatus.PENDING);
            List<FriendshipDTO> response = requests.stream()
                    .map(f -> new FriendshipDTO(f.getId(), f.getSender().getUsername(), f.getReceiver().getUsername(), f.getStatus().name()))
                    .collect(Collectors.toList());//ето було тяжко
            return ResponseEntity.ok(new ApiResponseWrapper<>( "Requests found", response));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "No requests found", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponseWrapper<String>> approveRequest(HttpServletRequest request, @RequestParam Long requestId) {
        String username = jwtUtil.validateToken(extractToken(request));
        Friendship friendship = friendshipRepo.findById(requestId).orElse(null);

        if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING &&
                friendship.getReceiver().getUsername().equals(username)) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepo.save(friendship);
            return ResponseEntity.ok(new ApiResponseWrapper<>( "Friendship approved", null));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "Request not found or already processed", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponseWrapper<String>> rejectRequest(HttpServletRequest request, @RequestParam Long requestId) {
        String username = jwtUtil.validateToken(extractToken(request));
        Friendship friendship = friendshipRepo.findById(requestId).orElse(null);

        if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING &&
                friendship.getReceiver().getUsername().equals(username)) {
            friendship.setStatus(FriendshipStatus.REJECTED);
            friendshipRepo.save(friendship);
            return ResponseEntity.ok(new ApiResponseWrapper<>( "Friendship rejected", null));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "Request not found or already processed", null), HttpStatus.NOT_FOUND);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponseWrapper<String>> testJackson() {
        ApiResponseWrapper<String> response = new ApiResponseWrapper<>("Test successful", null);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/allfriends")
    public ResponseEntity<ApiResponseWrapper<List<FriendshipDTO>>> getAllFriends(HttpServletRequest request){
        String username = jwtUtil.validateToken(extractToken(request));

        if (chatuserRepo.existsByUsername(username)) {
            ChatUser receiver = chatuserRepo.findByUsername(username);
            List<Friendship> requests = friendshipRepo.findByReceiverAndStatus(receiver, FriendshipStatus.ACCEPTED);
            List<FriendshipDTO> response = requests.stream()
                    .map(f -> new FriendshipDTO(f.getId(), f.getSender().getUsername(), f.getReceiver().getUsername(), f.getStatus().name()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponseWrapper<>( "Requests found", response));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "No requests found", null), HttpStatus.NOT_FOUND);
    }

}
