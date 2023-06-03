package com.schoolmanagement.service;

import com.schoolmanagement.controller.ViceDeanController;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViceDeanService {
    private final ViceDeanController viceDeanController;

    public ResponseMessage<ViceDeanResponse> save(ViceDeanRequest viceDeanRequest) {
    }
}
