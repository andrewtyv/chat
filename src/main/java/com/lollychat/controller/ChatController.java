package com.lollychat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lollychat.dto.MessageDTO;
import com.lollychat.model.AudioMessage;
import com.lollychat.model.Message;
import com.lollychat.model.Room;
import com.lollychat.model.StringMessage;
import com.lollychat.repos.MessageRepo;
import com.lollychat.repos.RoomRepo;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.securingweb.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private Chatuserrepo chatuserRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @MessageMapping("/message")
    public void sendMessage(HttpServletRequest request,Map<String, String> data, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String type = data.get("type");
        String content = data.get("content");
        Long roomId = Long.parseLong(data.get("roomId"));

        Room room = roomRepo.findById(roomId).orElseThrow(() -> new Exception("Room not found"));

        headerAccessor.getSessionAttributes().put("roomId", roomId);
        String username = jwtUtil.validateToken(extractToken(request));

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
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
