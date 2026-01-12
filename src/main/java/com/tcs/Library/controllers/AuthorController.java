package com.tcs.Library.controllers;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tcs.Library.dto.AuthorSignUp;
import com.tcs.Library.service.AuthorService;
import lombok.RequiredArgsConstructor;

@RequestMapping("/author")
@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorService authsvc;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthorSignUp dto) {
        return ResponseEntity.ok(authsvc.registerAuthor(dto));
    }

}
