
package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.repo.MessageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    private final MessageRepository repo;

    public MessageController(MessageRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/public")
    public List<Message> getPublic() {
        return repo.findAll().stream().filter(Message::isPublic).toList();
    }

    @GetMapping("/private")
    public List<Message> getPrivate() {
        return repo.findAll();
    }

    @PostMapping("/messages")
    public Message create(@RequestBody Message message) {
        return repo.save(message);
    }
}
