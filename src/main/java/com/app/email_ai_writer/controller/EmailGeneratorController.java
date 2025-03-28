package com.app.email_ai_writer.controller;

import com.app.email_ai_writer.entity.EmailRequest;
import com.app.email_ai_writer.service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    private final  EmailGeneratorService emailGeneratorService;
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailReq){
        String response=emailGeneratorService.generateEmailReply(emailReq);
        return ResponseEntity.ok(response);
    }
}
