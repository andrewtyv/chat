package com.lollychat.controller;

import com.lollychat.dto.ApiResponseWrapper;
import com.lollychat.dto.FriendListhandler;
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

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
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

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @PostMapping("/request")
    public ResponseEntity<ApiResponseWrapper<String>> addFriendRequest(HttpServletRequest request, @RequestBody Map<String, String> receiverName) {
        String senderName = jwtUtil.validateToken(extractToken(request));

        if (senderName.equals(receiverName.get("username"))) {
            return new ResponseEntity<>(new ApiResponseWrapper<>("U cannot be friends with yourself", null), HttpStatus.CONFLICT);
        }
        else if (!friendshipRepo.findBySenderAndReceiver(chatuserRepo.findByUsername(senderName),chatuserRepo.findByUsername(receiverName.get("username"))).isEmpty()){
            return new ResponseEntity<>(new ApiResponseWrapper<>("Request already created", null), HttpStatus.CONFLICT);
        }

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
                    .map(f ->
                            new FriendshipDTO.Builder()
                                    .id(f.getId())
                                    .sender(f.getSender().getUsername())
                                    .createdAt(f.getCreatedAt())
                                    .build())
                    .collect(Collectors.toList());//ето було тяжко
            return ResponseEntity.ok(new ApiResponseWrapper<>( "Requests found", response));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>( "No requests found", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponseWrapper<String>> approveRequest(HttpServletRequest request, @RequestBody Map<String, String> ID) {
        Long requestId = Long.parseLong(ID.get("requestId"));
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
    public ResponseEntity<ApiResponseWrapper<String>> rejectRequest(HttpServletRequest request, @RequestBody Map<String, String> ID) {
        Long requestId = Long.parseLong(ID.get("requestId"));
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
    public ResponseEntity<ApiResponseWrapper<List<FriendListhandler>>> getAllFriends(HttpServletRequest request){
        String username = jwtUtil.validateToken(extractToken(request));

        if (chatuserRepo.existsByUsername(username)) {
            ChatUser currentUser = chatuserRepo.findByUsername(username);
            List<Friendship> friendList = new LinkedList<>();

            friendList.addAll(friendshipRepo.findByReceiverAndStatus(currentUser, FriendshipStatus.ACCEPTED));

            friendList.addAll(friendshipRepo.findBySenderAndStatus(currentUser, FriendshipStatus.ACCEPTED));

            List<Friendship> uniqueFriends = friendList.stream()
                    .distinct()
                    .collect(Collectors.toCollection(LinkedList::new));

            List<FriendListhandler> response = uniqueFriends.stream()
                    .map(f -> {
                        String friendName = f.getSender().getUsername().equals(username)
                                ? f.getReceiver().getUsername()
                                : f.getSender().getUsername();
                        return new FriendListhandler.Builder()
                                .friendName(friendName)
                                .createdAt(f.getCreatedAt())
                                .status(f.getStatus().name())
                                .build();
                    })
                    .collect(Collectors.toCollection(LinkedList::new));

            return ResponseEntity.ok(new ApiResponseWrapper<>("Friends found", response));
        }

        return new ResponseEntity<>(new ApiResponseWrapper<>("No friends found", null), HttpStatus.NOT_FOUND);
    }


}
