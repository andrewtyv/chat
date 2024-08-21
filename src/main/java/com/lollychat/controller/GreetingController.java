package com.lollychat.controller;


import com.lollychat.model.ChatUser;
import com.lollychat.model.Message;
import com.lollychat.repos.Chatuserrepo;
import com.lollychat.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path="/demo")
public class GreetingController {
    @Autowired
    private Chatuserrepo userRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser (@RequestParam String name
            , @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        ChatUser n = new ChatUser();
        n.setUsername(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<ChatUser> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}

