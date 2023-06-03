package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("dean")//Base path dean olacak
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
    //  dean/(devaminda)/update(ile gel)/ ardindan bir part variable gelecek onada userId atiyoruz {userId}

    @PreAuthorize("hasAuthority('ADMIN')")// bu islemi Admin yapabilecek
    // Asagida ResponseMessage turunde bir DeanResponse donecek
    // @RequestBody request in bodysinde bana bir veri gelecek
    // @Valid gelen veriyi validasyondan gecirecek
    // DeanRequest deanRequest, gelecek olan veri DeanRequest Classindan gelecek olan veriler olacak

    //Note: DeanRequest uzerinden BaseUserResponse Classindan extend edildigi icin DeanRequest parent class i olan
    //BaseUserResponse daki user fieldlara atanan kullanici bilgileri getirilebilecek.

    // @PathVariable ile user id yi alacagiz ki updatade edecegimiz userin ilgilerine ulasabilelim: @PathVariable Long userId

    public ResponseMessage<DeanResponse> update(@RequestBody @Valid DeanRequest deanRequest, @PathVariable Long userId) {
        // Artik service classinda gerekli logica islemlerinin yapilabilmesi icin elimizde tum veriler var
        // istedigimiz user a ulacabilmek icin bir mapping- path variable miz var: /dean/update/1.
        // Bize rolu DEAN olan kullanincinin bilgileri DeanRequest ten gelecek.

        // Service Class da logical islemlerin yapilabilmesi icin return a dondurulmesi icin  olusturulacak olan
        // methodun adini ve parametrelerini veriyoruz
        return deanService.update(deanRequest, userId);
    }

    // Not :  Delete() ****************************************************
    @DeleteMapping("/delete/{userId}") // http://localhost:8080/dean/delete/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long userId){

        return deanService.deleteDean(userId);
    }

    // Not :  getById() ************************************************************************
    @GetMapping("/getManagerById/{userId}") // http://localhost:8080/dean/getManagerById/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> getDeanById(@PathVariable Long userId){

        return deanService.getDeanById(userId);

    }

    // Not :  getAll() *************************************************************************

    @GetMapping("/getAll") // http://localhost:8080/dean/getAll
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DeanResponse> getAll(){
        return deanService.getAllDean();
    }


    // Not :  Search() *************************************************************************
    @GetMapping("/search") // http://localhost:8080/dean/search
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<DeanResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type

    ){
        return deanService.search(page,size,sort,type);
    }
}