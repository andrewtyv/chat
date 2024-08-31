package com.lollychat.controller;


import com.lollychat.dto.MessageDTO;
import com.lollychat.dto.RoomDTO;
import com.lollychat.model.*;
import com.lollychat.repos.*;
import com.lollychat.securingweb.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groop")
public class GroopController {
    @Autowired
    private Friendshiprepo friendshipRepo;

    @Autowired
    private Chatuserrepo chatuserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GroopRoomRepo roomRepo;

    @Autowired
    private RoomRequestRepo requestRepo;

    @Autowired
    private MessageRepo messageRepo;


    @PostMapping("/createRoom")
    public ResponseEntity<RoomDTO> create(HttpServletRequest request, Map<String , String > data){
        Long id;
        String createdAt;
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);
        String roomName = data.get("name");

        if (roomName == null || roomName.isEmpty()) {
            return new ResponseEntity<>(new RoomDTO("no room name" , null ,null , null,null), HttpStatus.BAD_REQUEST);
        }

        if (user == null ) {
            return  new ResponseEntity<>(new RoomDTO("no user found" , null , null, null,null ), HttpStatus.NOT_FOUND);
        }

        GroopRoom newRoom  = new GroopRoom(roomName);
        id = newRoom.getId();
        createdAt = newRoom.getCreatedAt().toString();
        newRoom.addUser(user);
        roomRepo.save(newRoom);


        return new ResponseEntity<>(new RoomDTO("created succesfully ", data.get("name"), id,createdAt,null ), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<RoomDTO> add_user(HttpServletRequest request, @RequestBody Map<String,String> data){
        String roomName = data.get("roomName");
        Long id = Long.parseLong(data.get("id"));
        String username = jwtUtil.validateToken(extractToken(request));
        String recivername = data.get("reciverName");
        Room room = roomRepo.findById(id).orElse(null);
        if (room == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        ChatUser receiver = chatuserRepo.findByUsername(recivername);

        if (room.getUsers().contains(receiver)) {
            return new ResponseEntity<>(new RoomDTO("User already in the room", room.getName(), room.getId(), room.getCreatedAt().toString(), null), HttpStatus.CONFLICT);
        }

        if (receiver == null || receiver.getUsername().isEmpty()) {
            return  new ResponseEntity<>(null , HttpStatus.NOT_FOUND);

        }

        RoomRequest roomRequest = requestRepo.findByRoomAndUser(room , receiver);

        if (roomRequest !=null) {
            return  new ResponseEntity<>(null , HttpStatus.CONFLICT);
        }

        RoomRequest newRequest = new RoomRequest(room, receiver, FriendshipStatus.PENDING);
        requestRepo.save(newRequest);

        RoomDTO roomDTO = new RoomDTO("Request sent successfully", room.getName(), room.getId(), room.getCreatedAt().toString(),null);
        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);

    }

    @GetMapping("/allrequests")
    public ResponseEntity<List<RoomDTO>> findallreq(HttpServletRequest request){
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
        List<RoomRequest> requests = requestRepo.findByUserAndStatus(user, FriendshipStatus.PENDING);
        return new ResponseEntity<>(requests.stream()
                .map(f-> new RoomDTO
                                (
                            null,
                            f.getRoom().getName(),
                            f.getId(),
                            f.getCreatedAt().toString(),
                                        null)
                    )
                .collect(Collectors.toList()),
                HttpStatus.OK );
    }

    @GetMapping("/allgroups")
    public ResponseEntity<List<RoomDTO>> findAllGroups(HttpServletRequest request) {
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser currentUser = chatuserRepo.findByUsername(username);
        if (currentUser == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        List<Room> rooms = roomRepo.findByUsers(currentUser);

        List<RoomDTO> roomDTOs = rooms.stream()
                .map(room -> {
                    long unreadCount = messageRepo.countByRoomAndAuthorNotAndRead(room, currentUser, false);

                    return new RoomDTO(
                            null,
                            room.getName(),
                            room.getId(),
                            room.getCreatedAt().toString(),
                            unreadCount
                    );
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(roomDTOs, HttpStatus.OK);
    }

    @PostMapping("/enter")
    public ResponseEntity<List<MessageDTO>> enterRoom(HttpServletRequest request,@RequestBody Map<String, String> data) {
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Long id = Long.parseLong(data.get("id"));
        List<Message> messages = markMessagesAsReadAndGetMessages(id , user.getId());

        List<MessageDTO> messageDTOs = messages.stream()
                .map(m -> new MessageDTO(
                        m.getId(),
                        m.getRoom().getId(),
                        m.getAuthor().getUsername(),
                        m.isRead(),
                        ((StringMessage)m).getContent()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<RoomDTO> acceptRequest(HttpServletRequest request,@RequestBody Map<String, String> data) {
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Long requestId = Long.parseLong(data.get("requestId"));
        RoomRequest roomRequest = requestRepo.findById(requestId).orElse(null);

        if (roomRequest == null || !roomRequest.getUser().equals(user)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roomRequest.setStatus(FriendshipStatus.ACCEPTED);
        requestRepo.save(roomRequest);

        GroopRoom room = (GroopRoom) roomRequest.getRoom();
        room.addUser(user);
        roomRepo.save(room);

        RoomDTO roomDTO = new RoomDTO("Request accepted", room.getName(), room.getId(), room.getCreatedAt().toString(), null);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<RoomDTO> rejectRequest(HttpServletRequest request,@RequestBody Map<String, String> data) {
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Long requestId = Long.parseLong(data.get("requestId"));
        RoomRequest roomRequest = requestRepo.findById(requestId).orElse(null);

        if (roomRequest == null || !roomRequest.getUser().equals(user)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roomRequest.setStatus(FriendshipStatus.REJECTED);
        requestRepo.save(roomRequest);

        RoomDTO roomDTO = new RoomDTO("Request rejected", roomRequest.getRoom().getName(), roomRequest.getRoom().getId(), roomRequest.getCreatedAt().toString(), null);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    public List<Message> markMessagesAsReadAndGetMessages(Long roomId, Long userId) {
        Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        ChatUser user = chatuserRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages = room.getMessages();
        for (Message message : messages) {
            if (!message.getAuthor().equals(user) && !message.isRead()) {
                message.setRead(true);
                messageRepo.save(message);
            }
        }

        return messages;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



}
