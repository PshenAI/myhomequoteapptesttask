package com.myhomequote.testtask.controller;

import com.myhomequote.testtask.dto.ResultDTO;
import com.myhomequote.testtask.dto.SetInfoRequest;
import com.myhomequote.testtask.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResultController {

    private final ResultService service;

    @PutMapping("/setinfo")
    public ResponseEntity<Void> setInfo(@RequestBody SetInfoRequest request) {
        service.setInfo(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userinfo/{user_id}")
    public ResponseEntity<List<ResultDTO>> getUserInfo(@PathVariable("user_id") int userId) {
        return ResponseEntity.ok(service.getUserInfo(userId));
    }

    @GetMapping("/levelinfo/{level_id}")
    public ResponseEntity<List<ResultDTO>> getLevelInfo(@PathVariable("level_id") int levelId) {
        return ResponseEntity.ok(service.getLevelInfo(levelId));
    }
}
