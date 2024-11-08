package org.example.inl.Messages.controller;

import org.example.inl.Security.JWT.JwTUtil;
import org.example.inl.Messages.model.Messages;
import org.example.inl.Messages.model.MessagesDTO;
import org.example.inl.Messages.service.messageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class messageController {

    @Autowired
    private messageService service;
    @Autowired
    private JwTUtil jwTUtil;
    @Autowired
    private org.example.inl.users.service.userService userService;


//    @GetMapping("/user/{userId}")
//    public List<Messages> getTransactionByUserId(@PathVariable Long userId) throws Exception {
//        return Transactionservice.getTransactionsByUserId(userId);
//    }

    @PostMapping("/add")
    public ResponseEntity<?> getMessage(@RequestBody MessagesDTO messagesDTO,
                                             @RequestHeader("Authorization") String token) throws Exception {

        try{
            String username = jwTUtil.extractedUsername(token.substring(7));
            Messages messages = service.addMessage(messagesDTO, username);
            return ResponseEntity.ok(messages);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }

    }

    @GetMapping("/user")
    public ResponseEntity<List<Messages>> getMessagesByUserId(@RequestHeader("Authorization") String token) throws Exception {

        String username = jwTUtil.extractedUsername(token.substring(7));

        Long userId = service.getUserIdFromUsername(username);

        List<Messages> userMessages = service.getMessageByUserId(userId);
        return ResponseEntity.ok(userMessages);
    }




}
