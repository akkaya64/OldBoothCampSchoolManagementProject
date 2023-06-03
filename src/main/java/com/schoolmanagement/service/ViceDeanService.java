package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.ViceDean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.ViceDeanDto;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import com.schoolmanagement.repository.ViceDeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViceDeanService {

    private final ViceDeanRepository viceDeanRepository;
    private final AdminService adminService;
    private final ViceDeanDto viceDeanDto;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    // Not :  Save() *************************************************************************
    public ResponseMessage<ViceDeanResponse> save(ViceDeanRequest viceDeanRequest) {

        adminService.checkDuplicate(viceDeanRequest.getUsername(), viceDeanRequest.getSsn(), viceDeanRequest.getPhoneNumber());
        ViceDean viceDean = createPojoFromDTO(viceDeanRequest);
        // Roll ve password encode islemleri
        viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));
        viceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));
        viceDeanRepository.save(viceDean);
        // response nesnesi olusturulacak
        return ResponseMessage.<ViceDeanResponse>builder()
                .message("Vice Dean Saved")
                .httpStatus(HttpStatus.CREATED)
                .object(createViceDeanResponse(viceDean))
                .build();

    }

    private ViceDean createPojoFromDTO(ViceDeanRequest viceDeanRequest){

        return viceDeanDto.dtoViceBean(viceDeanRequest);

    }

    private ViceDeanResponse createViceDeanResponse(ViceDean viceDean) {

        return ViceDeanResponse.builder()
                .userId(viceDean.getId())
                .username(viceDean.getUsername())
                .name(viceDean.getName())
                .surname(viceDean.getSurname())
                .birthPlace(viceDean.getBirthPlace())
                .birthDay(viceDean.getBirthDay())
                .phoneNumber(viceDean.getPhoneNumber())
                .ssn(viceDean.getSsn())
                .gender(viceDean.getGender())
                .build();
    }

    // Not :  UpdateById() ********************************************************************
    public ResponseMessage<ViceDeanResponse> update(ViceDeanRequest newViceDean, Long managerId) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(!viceDean.isPresent()) {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }else if(!CheckParameterUpdateMethod.checkParameter(viceDean.get(), newViceDean )) {
            adminService.checkDuplicate(newViceDean.getUsername(), newViceDean.getSsn(), newViceDean.getPhoneNumber());
        }

        ViceDean updatedViceDean = createUpdatedViceDean(newViceDean, managerId);
        updatedViceDean.setPassword(passwordEncoder.encode(newViceDean.getPassword()));
        updatedViceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));

        viceDeanRepository.save(updatedViceDean);

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("Vice Dean Updated")
                .httpStatus(HttpStatus.CREATED)
                .object(createViceDeanResponse(updatedViceDean))
                .build();

    }

    private ViceDean createUpdatedViceDean(ViceDeanRequest viceDeanRequest, Long managerId) {

        return ViceDean.builder()
                .id(managerId)
                .username(viceDeanRequest.getUsername())
                .ssn(viceDeanRequest.getSsn())
                .name(viceDeanRequest.getName())
                .surname(viceDeanRequest.getSurname())
                .birthPlace(viceDeanRequest.getBirthPlace())
                .birthDay(viceDeanRequest.getBirthDay())
                .phoneNumber(viceDeanRequest.getPhoneNumber())
                .gender(viceDeanRequest.getGender())
                .build();
    }




}