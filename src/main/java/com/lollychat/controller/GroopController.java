package com.lollychat.controller;


import com.lollychat.dto.RoomDTO;
import com.lollychat.model.ChatUser;
import com.lollychat.model.FriendshipStatus;
import com.lollychat.model.Room;
import com.lollychat.model.RoomRequest;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.repos.Friendshiprepo;
import com.lollychat.repos.RoomRepo;
import com.lollychat.repos.RoomRequestRepo;
import com.lollychat.securingweb.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private RoomRepo roomRepo;

    @Autowired
    private RoomRequestRepo requestRepo;


    @PostMapping("/createRoom")
    public ResponseEntity<RoomDTO> create(HttpServletRequest request, Map<String , String > data){
        Long id;
        String createdAt;
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);
        String roomName = data.get("name");

        if (roomName == null || roomName.isEmpty()) {
            return new ResponseEntity<>(new RoomDTO("no room name" , null ,null , null), HttpStatus.BAD_REQUEST);
        }

        if (user == null ) {
            return  new ResponseEntity<>(new RoomDTO("no user found" , null , null, null ), HttpStatus.NOT_FOUND);
        }

        Room newRoom  = new Room(roomName);
        id = newRoom.getId();
        createdAt = newRoom.getCreatedAt().toString();

        return new ResponseEntity<>(new RoomDTO("created succesfully ", data.get("name"), id,createdAt ), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<RoomDTO> add_user(HttpServletRequest request, Map<String,String> data){
        String roomName = data.get("roomName");
        Long id = Long.parseLong(data.get("id"));
        String username = jwtUtil.validateToken(extractToken(request));
        String recivername = data.get("reciverName");
        Room room = roomRepo.findById(id).orElse(null);
        if (room == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        ChatUser receiver = chatuserRepo.findByUsername(recivername);

        if (receiver == null || receiver.getUsername().isEmpty()) {
            return  new ResponseEntity<>(null , HttpStatus.NOT_FOUND);

        }

        RoomRequest roomRequest = requestRepo.findByRoomAndUser(room , receiver);

        if (roomRequest !=null) {
            return  new ResponseEntity<>(null , HttpStatus.CONFLICT);
        }

        RoomRequest newRequest = new RoomRequest(room, receiver, FriendshipStatus.PENDING);
        requestRepo.save(newRequest);

        RoomDTO roomDTO = new RoomDTO("Request sent successfully", room.getName(), room.getId(), room.getCreatedAt().toString());
        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);

    }

    @GetMapping("/allrequests")
    public ResponseEntity<List<RoomDTO>> findall(HttpServletRequest request){
        String username = jwtUtil.validateToken(extractToken(request));
        ChatUser user = chatuserRepo.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
        List<RoomRequest> requests = requestRepo.findByUserAndStatus(user, FriendshipStatus.PENDING);
        return new ResponseEntity<>(requests.stream()
                .map(f-> new RoomDTO(
                            null,
                            f.getRoom().getName(),
                            f.getId(),
                            f.getCreatedAt().toString())
                    )
                .collect(Collectors.toList()),
                HttpStatus.OK );
    }



    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



}
