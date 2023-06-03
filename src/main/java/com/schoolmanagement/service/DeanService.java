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
            // CheckParameterUpdateMethod methodunda karsilastirilan degerler true verirse bu if in icine girer DB deki
            // datalar ile requestten gelen datalar esitse bu if in icine girer bunu onlemek icin (!) isareti ile
            // parametreyi tersliyoruz. parametrede verilen datalar farklisya if in icine girsin ve unique ligi kontrol
            // etsin farkliysa if in icine hic girmesin.

            // parametreleri karsilastirmak icin olusturdigumuz classin methodunu tersleyerek cagiriyoruz
            // !CheckParameterUpdateMethod.checkParameter. yukarida pathin id vererek cagirdigimiz Optional type daki
            // dean variable ndan bir dean' nin datalarini get() methodu ile parametre olarak veriyoruz dean.get().
            // karsilastirilacak ikinci parametre olarak da method da parametre olarak verilen DeanRequest den gelen
            // newDean parametresini veriyoruz

            // iki bilgiyi karsilastirmak icin utils packagesinin altinda olusturacagimiz CheckParameterUpdateMethod
            // adli classda, checkParameter adinda bir method yaziyoruz
            // update edilen bilgiler ile orjinel bilgilerin ayni olup olmadigi kontrol edilecek

            // Hangi fieldlarda degisiklik yapilip yapilmayacagini burada parametre olarak veriyoruz
            // asagidaki code blogundan CheckParameterUpdateMethod icinde otomatik olarak checkParameter adinda bir
            // methodu create edecegiz

            // karsilatiracagimiz user id sinin orjinali path da verilen id uzerinden db den gelecek digeri
            // BaseUserRequest den DeanRequest ile gelecek.Kullanici update bilgilerini JWT Token ile DeanRequest olusturacak
            adminService.checkDuplicate(newDean.getUsername(),newDean.getSsn(), newDean.getPhoneNumber());
            // adminService deki checkDuplicate methoduna newDean den gelen .getUsername(), .getSsn(), .getPhoneNumber()
            // parametrelerini veriyoruz. artik Duplicate yapmis oluyoruz

            // tek parametre degistirildiginde senaryo postmande test edilmeli
        }

        // !!! guncellenen yeni bilgiler ile newDean uzerinden Dean objesini kaydediyoruz
        // DTO da newDean araciligi ile gelen bilgiler ile bir Dean objesi olusturulmasi gerekir ama newDean dan gelen
        // DTO yu POJO ya cevirip bilgileri DB ye bu sekilde gonderebiliriz

        // Password u kontrol edemedigimiz icin eskisi de olsa yeniside olsa update islemlerinde  password encode
        // edilmek zorunda

        //DTO yu POJO ya cevirmek icin yardimci bir method olustiruyoruz


        // Assagida olusturdugumuz Guncellenen yeni bilgiler ile bir Dean objesi ureten method a Dean uretebilmesi icin
        // parametre olarak bir dean id si ve ve newDean datalarini veriyoruz. Method bir Dean donduruyor bu guncellenmis
        // Dean i updatedDean olarak adlandiriyoruuz
        Dean updatedDean = createUpdatedDean(newDean,deanId);

        // updated edilen Dean e password da vermemiz lazim ayni zamanda password un encode edilmesi lazim bu iki
        // islemide burada yapiyoruz. DeanService class indan gelen passwordEncoder methoduna spring in encode methodu kullanarak
        // encode edecegi parametreyi newDean.getPassword() veriyoruz. updatedDean e newDean dan password u encode ederek setliyoruz.
        updatedDean.setPassword(passwordEncoder.encode(newDean.getPassword()));
        deanRepository.save(updatedDean);// DB deki DeanRepository de update edilen Dean i deanRepository nin save
        // methodu ile persist etmis oluyoruz

        return ResponseMessage.<DeanResponse>builder()// methodumuz artik duzgun calisiyor kullaniciya yapilan islem ile ilgili
                // bir bilgi dondurmemiz lazim
                .message("Dean Updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(updatedDean))// Update edilen Dean objenin kendisini save methodunda
                // Json formata cevirip bir response olusturmustuk. save methodu icindeki createDeanResponse methodunu
                // kullanarak updateDeandan bir DeanResponse olusturuyoruz
                .build();

    }

    //DTO yu POJO ya cevirmek icin yardimci bir method olustiruyoruz
    //Guncellenen yeni bilgiler ile bir Dean objesi ureten method olusturuyoruz
    private Dean createUpdatedDean(DeanRequest deanRequest, Long managerId){//parametre olarak DeanRequest in kendisini alacak
        // Burada yeni bir Dean objesi olusturmuyoruz bu nedenle update edilecek objeyi create edebilmek icin bir id ye
        // ihtiyacimiz olacak. bu Id de update methoduna parametre olarak verdigimiz id olacak ismi onemli degil bu nedenle
        // karistirmamak icin data turunu(Long) verdikten sonra id ismini managerId olarak veriyoruz. yani id si bu parametresi
        // DeanRequest den gelen Dean demis oluyoruz.

        //Bir Dean objesi dondurecegiz.
        return Dean.builder()
                .id(managerId)
                .username(deanRequest.getUsername())// username e DeanRequest den gelen Username 'i ver
                .ssn(deanRequest.getSsn())
                .name(deanRequest.getName())
                .surname(deanRequest.getSurname())
                .birthPlace(deanRequest.getBirthPlace())
                .birthDay(deanRequest.getBirthDay())
                .phoneNumber(deanRequest.getPhoneNumber())
                .gender(deanRequest.getGender())
                .userRole(userRoleService.getUserRole(RoleType.MANAGER))
                // Role bilgisi DeanRequest de olmadigi icin burada kendimiz setliyoruz. Su anda bir Dean objesinin
                // icerisindeyiz ve Deanin enum type MANAGER oldugunu biliyoruz. userRoleService kati uzerinden role type
                // ulasabiliriz
                .build();// bu sekilden bana bir obje olustur ve gonder

    }

    // Not :  Delete() ****************************************************
    public ResponseMessage<?> deleteDean(Long deanId) {

        //Silinmesi istenen Dean DB de varmi kontol ediliyor
        Optional<Dean> dean = deanRepository.findById(deanId);

        if(!dean.isPresent()) { // isEmpty() de kullanilabilir
            //ici dolu degilse exceptionu firlat
            // if in icine girediyse bu dean DB de yok demektir DeanResponse ici bos gelecek kullaniciya bir exception firlatilir.
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }

       // Dean eger if in icine girmediyse Bu dean DB de var demektir ve asagidaki syntax bu dean i siler
        deanRepository.deleteById(deanId);

        //Artik kullaniciya bilgilendirme mesaji gonderebiliriz. ama artik silindigi icin dean bilgilerini kullaniciya
        // gondremeyiz bu nedenle asagida deanResponse setlemiyoruz
        return ResponseMessage.builder()
                .message("Dean Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not :  getById() ************************************************************************
    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {
        Optional<Dean> dean = deanRepository.findById(deanId);

        //getDeanById  methodu zaten bir Response turunde  ResponseMessage donuyor  ResponseMessage<DeanResponse>
        //ama donen response bos sa asagidaki if bos olup olmamasini kontrol edecek  ve eger gelen deanresponse bos ise
        //kullaniciya boyle bir User olmadigi mesajini gonderecek

        if(!dean.isPresent()) { // isEmpty() de kullanilabilir

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }

        deanRepository.deleteById(deanId);

        //Frontend tarafindan gelen istek vardi bu nedenle asagidaki ResponseMessage kullaniciya donduruyoruz.
        return ResponseMessage.<DeanResponse>builder()
                .message("Dean successfully found")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(dean.get()))// dean ini fieldlarini da gonderiyoruz
                .build();
    }

    // Not :  getAll() *************************************************************************
    public List<DeanResponse> getAllDean() {

        //Burada findAll generic bir List donduruyor dolayisi ile Dean lari getirme islemini stream API si ile yapabiliriz
        return deanRepository.findAll()
                .stream()
                //stream ile dean akisi olusuyor bu akisi duzenlememiz lazim
                .map(this::createDeanResponse)
                // deanRepository dan gelen dean lari DeanResponse cevirmemiz lazim, akisi degistirme methodu map()
                // methodu ile buradan gelen Class a (this; deanRepository) ::createDeanResponse a deanResponse
                // cevirmsi icin parametre olarak ver diyoruz
                .collect(Collectors.toList());//ve deanlari List olarak topla diyoruz
    }

    // Not :  Search() *************************************************************************
    public Page<DeanResponse> search(int page, int size, String sort, String type) {

        //Pageable yapiyi data.domain den import ediyoruz
        //PageRequest data.domainden import ediyoruz.
        //PageRequest in .of methoduna icinde bulundugumuz methodun parametresinden gelen data
        //(page,size, Sort.by(sort).ascending()) parametrelerini veriyoruz
        //sort lama parametresini Sort.by methodu ile ascending() olarak veriyoruz
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        //gelen data POJO map() methodunu kullanarak bunu DTO ya ceviriyoruz.
        return deanRepository.findAll(pageable).map(this::createDeanResponse);
    }
}