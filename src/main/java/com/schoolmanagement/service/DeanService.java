package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.DeanDto;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.DeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanService {// sales department

    private final DeanRepository deanRepository;
    private final AdminService adminService;
    private final DeanDto deanDto;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    // Not: Save() *************************************************
    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {

        //!!! Dublicate kontrolu
        adminService.checkDuplicate(deanRequest.getUsername(),
                deanRequest.getSsn(),
                deanRequest.getPhoneNumber());

        // !!! DTO -POJO donusumu
        Dean dean = createDtoForDean(deanRequest);
        // !!! role ve password bilgileri uygun sekilde setleniyor
        dean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));

        // !!! Db ye kayit
        Dean savedDean = deanRepository.save(dean);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean Saved")
                .httpStatus(HttpStatus.CREATED)
                .object(createDeanResponse(savedDean))
                .build();
        // yardimci metod

    }

    private Dean createDtoForDean(DeanRequest deanRequest){ // createDtoToPOJO

        return deanDto.dtoDean(deanRequest);
    }

    private DeanResponse createDeanResponse(Dean dean){
        return DeanResponse.builder()
                .userId(dean.getId())
                .username(dean.getUsername())
                .name(dean.getName())
                .surname(dean.getSurname())
                .birthDay(dean.getBirthDay())
                .birthPlace(dean.getBirthPlace())
                .phoneNumber(dean.getPhoneNumber())
                .gender(dean.getGender())
                .ssn(dean.getSsn())
                .build();
    }

    // Not :  UpdateById() **********************************************
    public ResponseMessage<DeanResponse> update(DeanRequest newDean, Long deanId) {
        // Donen objeyi DeanController tarafinda setlemistik.

        // @PutMapping tehlikelidir mesela doldurulmasi gereken 10 tane field vardir bunlardan 3 tanesini doldurursunuz
        // digerleri null gelir ve NullPointException alabiliriz. @Valid inteface si ile calisacagimiz icin
        // nullpointexception almamiza imkan yok

        //Sadece istedigimiz fieldlar dolsun digerleri ayni kalsin denirse o zaman @FetchMapping kullanacagiz

        //Method a parametre olarak bi deanId geliyor oncelikle boyle bir id varmi kontrol etmemiz lazim
        Optional<Dean> dean = deanRepository.findById(deanId); //orElseThrow ilede yapilabilir daha guzel olur.
        //findById abstract classi Optional bir parametreli bir deger dondurur bunuda generic olarak <Dean> alan dean
        // adindaki bir Optional yapiya atiyoruz

        //Optional olarak gelecegi icin bos gelme ihtimali var. orElseThrow yapmamanin bir sikintisi, orElseThrow daha clean
        // dean objesi bos olma kontrolu
        if(!dean.isPresent()) { // isEmpty() de kullanilabilir bunu kullanirsa boolean olarak bos oldugu icin true
            // gelecek terslemeyede gerek kalmayacak
            //isPresent() fieldin icini denetler ici dolu ise true getirir.
            //Burada icinin bos sa tru gelmesini istiyoruz, ici bos sa true gelsin demek icin (!) isaretini kullanarak
            // tersleme yapiyoruz. !dean.isPresent()

            //icinin bos olarak true gelmesi drumunda kendimizin yapilandirdigi bir exception firlatacagiz
            //exception package sinin altinda ResorceNotFoundException Class i olusturuyoruz
            //utils degi message classinin icine mesajimizi setliyoruz.
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));

            //Bos olma durumu false ise yani doluysa;
            //Update yapacagimiz icin user zaten kayitli unique olmasi gereken degerlerde kullanicin bir degisiklik
            //yapmadiysa unique olmasi gereken bir datanin diger kullanicilari kontrol ederek girilen datanin unique ligini
            //kontrol eden AdminService Class da olusturdugumuz checkDuplicate methodunun bosu bosuna calismasina gerek yok
            //unique olmasi gereken degerlerde bir degisiklik yapildiysa o zaman bu method calissin demek icin:
        } else if(!CheckParameterUpdateMethod.checkParameter(dean.get(),newDean)) {
            // iki bilgiyi karsilastirmak icin utils packagesinin altinda olusturacagimiz CheckParameterUpdateMethod
            // adli classda, checkParameter adinda bir method yaziyoruz
            // update edilen bilgiler ile orjinel bilgilerin ayni olup olmadigi kontrol edilecek

            // Hangi fieldlarda degisiklik yapilip yapilmayacagini burada parametre olarak veriyoruz
            // asagidaki code blogundan CheckParameterUpdateMethod icinde otomatik olarak checkParameter adinda bir
            // methodu create edecegiz

            //karsilatiracagimiz user id sinin orjinali path da verilen id uzerinden db den gelecek digeri
            //BaseUserRequest den DeanRequest ile gelecek.Kullanici update bilgilerini JWT Token ile DeanRequest olusturacak
            adminService.checkDuplicate(newDean.getUsername(),newDean.getSsn(), newDean.getPhoneNumber());
            // tek parametre degistirildiginde senaryo postmande test edilmeli
        }

        // !!! guncellenen yeni bilgiler ile Dean objesini kaydediyoruz
        Dean updatedDean = createUpdatedDean(newDean,deanId);
        updatedDean.setPassword(passwordEncoder.encode(newDean.getPassword()));
        deanRepository.save(updatedDean);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean Updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(updatedDean))
                .build();

    }

    private Dean createUpdatedDean(DeanRequest deanRequest, Long managerId){

        return Dean.builder()
                .id(managerId)
                .username(deanRequest.getUsername())
                .ssn(deanRequest.getSsn())
                .name(deanRequest.getName())
                .surname(deanRequest.getSurname())
                .birthPlace(deanRequest.getBirthPlace())
                .birthDay(deanRequest.getBirthDay())
                .phoneNumber(deanRequest.getPhoneNumber())
                .gender(deanRequest.getGender())
                .userRole(userRoleService.getUserRole(RoleType.MANAGER))
                .build();

    }

    // Not :  Delete() ****************************************************
    public ResponseMessage<?> deleteDean(Long deanId) {
        Optional<Dean> dean = deanRepository.findById(deanId);
        if(!dean.isPresent()) { // isEmpty() de kullanilabilir

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }
        deanRepository.deleteById(deanId);
        return ResponseMessage.builder()
                .message("Dean Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not :  getById() ************************************************************************
    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {
        Optional<Dean> dean = deanRepository.findById(deanId);
        if(!dean.isPresent()) { // isEmpty() de kullanilabilir

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }
        deanRepository.deleteById(deanId);
        return ResponseMessage.<DeanResponse>builder()
                .message("Dean successfully found")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(dean.get()))
                .build();
    }

    // Not :  getAll() *************************************************************************
    public List<DeanResponse> getAllDean() {
        return deanRepository.findAll()
                .stream()
                .map(this::createDeanResponse)
                .collect(Collectors.toList());
    }

    // Not :  Search() *************************************************************************
    public Page<DeanResponse> search(int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return deanRepository.findAll(pageable).map(this::createDeanResponse);
    }
}