package com.schoolmanagement.service;


import com.schoolmanagement.entity.concretes.ContactMessage;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static com.schoolmanagement.utils.Messages.ALREADY_SEND_A_MESSAGE_TODAY;


@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;


    // Not: save() methodu **********************************************************************
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        // !!! ayni kisi ayni gun icinde sadece 1 defa mesaj gonderebilsin
         boolean isSameMessageWithSameEmailForToday =
                 contactMessageRepository.existsByEmailEqualsAndDateEquals(contactMessageRequest.getEmail(), LocalDate.now());

         if(isSameMessageWithSameEmailForToday) throw new ConflictException(String.format(ALREADY_SEND_A_MESSAGE_TODAY));

         // !!! DTO-POJO donusumu ( odev )
        ContactMessage contactMessage = createObject(contactMessageRequest);
        ContactMessage saveData = contactMessageRepository.save(contactMessage);

        return  ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact Message Created Successfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(saveData))
                .build();
    }

    private ContactMessage createObject(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .date(LocalDate.now())
                .build();
    }

    private ContactMessageResponse createResponse(ContactMessage contactMessage){
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .date(LocalDate.now())
                .build();
    }


    // Not: getAll() methodu **********************************************************************

    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {
       Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

       if (Objects.equals(type,"desc")){
           pageable = PageRequest.of(page,size,Sort.by(sort).descending());
       }

       return contactMessageRepository.findAll(pageable).map(this::createResponse);
    }

    // Not: searchByEmail() methodu **********************************************************************
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {
       Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

       if (Objects.equals(type,"desc")){
           pageable = PageRequest.of(page,size,Sort.by(sort).descending());
       }

       return contactMessageRepository.findByEmailEquals(email, pageable).map(this::createResponse);
    }

    // Not: searchBySubject() methodu **********************************************************************
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }
        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(this::createResponse);
    }

    //repoda elimize pojo geldi  biz onu yukarıdakı method ile response classına çevirdik

}
