package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import com.schoolmanagement.service.ViceDeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ViceDeanController {

    //Injection Area
    private final ViceDeanService viceDeanService;

    // Not :  Save() *************************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping("/save") // http://localhost:8080/vicedean/save
    public ResponseMessage<ViceDeanResponse> save(@RequestBody @Valid ViceDeanRequest viceDeanRequest) {

        return viceDeanService.save(viceDeanRequest);

    }

    // Not :  UpdateById() ********************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/update/{userId}") // http://localhost:8080/vicedean/update/1
    public ResponseMessage<ViceDeanResponse> update(@RequestBody @Valid ViceDeanRequest viceDeanRequest
            ,@PathVariable Long userId){
        return viceDeanService.update(viceDeanRequest, userId);
    }
}

