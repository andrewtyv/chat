package com.lollychat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lollychat.dto.MessageDTO;
import com.lollychat.model.*;
import com.lollychat.repos.FriendRoomRepo;
import com.lollychat.repos.MessageRepo;
import com.lollychat.repos.RoomRepo;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.securingweb.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    FriendRoomRepo roomRepo;
    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private Chatuserrepo chatuserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @MessageMapping("/message")
    public void sendMessage(@RequestBody Map<String, String> data, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String type = data.get("type");
        String content = data.get("content");
        Long roomId = Long.parseLong(data.get("roomId"));

        Room room = roomRepo.findById(roomId).orElseThrow(() -> new Exception("Room not found"));

        String jwtToken = headerAccessor.getFirstNativeHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        String username = jwtUtil.validateToken(jwtToken);

        if (username == null) {
            throw new Exception("User not authenticated");
        }

        if ("string".equals(type)) {
            String escapedContent = HtmlUtils.htmlEscape(content);
            StringMessage stringMessage = new StringMessage(room, chatuserRepo.findByUsername(username), escapedContent);
            messageRepo.save(stringMessage);
            messagingTemplate.convertAndSend("/topic/messages/" + roomId, new MessageDTO(stringMessage.getId(), roomId, username, stringMessage.isRead(), stringMessage.getContent()));
        }
    }

    @PostMapping("/enter")
    public ResponseEntity<List<MessageDTO>> enterRoom(HttpServletRequest request, @RequestBody Map<String, String> data) {
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


    public List<Message> markMessagesAsReadAndGetMessages(Long roomId, Long userId) {
        FriendRoom room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
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