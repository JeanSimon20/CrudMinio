package com.jsimon.minion.controller;

import com.jsimon.minion.model.Document;
import com.jsimon.minion.service.DocumentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

@Slf4j
@RestController
@RequestMapping(value = "/document")
public class DocumentController {

    @Autowired
    private DocumentServiceImpl service;

    @GetMapping()
    public ResponseEntity<Object> getFiles() {
        return ResponseEntity.ok(service.getListObjects());
    }

    @PostMapping()
    public ResponseEntity<Object> upload(@ModelAttribute Document request) {
        return ResponseEntity.ok().body(service.uploadFile(request));
    }

    @GetMapping(value = "/**")
    public ResponseEntity<Object> getFile(HttpServletRequest request) throws IOException {
        String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        String filename = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(IOUtils.toByteArray(service.getObject(filename)));
    }

}
