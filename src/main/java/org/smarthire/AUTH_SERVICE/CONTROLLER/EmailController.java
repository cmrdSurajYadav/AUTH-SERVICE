package org.smarthire.AUTH_SERVICE.CONTROLLER;

import lombok.RequiredArgsConstructor;
import org.smarthire.AUTH_SERVICE.SERVICE.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestParam String to,
                                           @RequestParam String subject,
                                           @RequestParam String body) {
        emailService.sendEmail(to, subject, body, true);
        return ResponseEntity.ok("Email sent (or queued)!");
    }
}
