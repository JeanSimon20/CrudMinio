package com.jsimon.minion.controller;

import com.jsimon.minion.model.Document;
import com.jsimon.minion.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(value = "/document")
public class DocumentController {

    @Autowired
    private DocumentService service;

    @GetMapping(value = "/list")
    public ResponseEntity<Object> getFiles() {
        return ResponseEntity.ok(service.getListObjects());
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Object> upload(@ModelAttribute Document request) {
        return ResponseEntity.ok().body(service.uploadFile(request));
    }

}
