package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("dean")
public class DeanController {

    private final DeanService deanService;

    // Not: Save() *************************************************
    @PostMapping("/save") // http://localhost:8080/dean/save
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> save(@RequestBody @Valid DeanRequest deanRequest) {

        return deanService.save(deanRequest);

    }

    // Not :  UpdateById() **********************************************
    @PutMapping("/update/{userId}") // http://localhost:8080/dean/update/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> update(@RequestBody @Valid DeanRequest deanRequest,
                                                @PathVariable Long userId) {
        return deanService.update(deanRequest, userId);
    }
}