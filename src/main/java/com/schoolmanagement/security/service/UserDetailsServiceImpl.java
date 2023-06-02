package com.schoolmanagement.security.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service // Springframework artik bunu service olarak algiladi
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    // Bu classin security in bir Class i oldugunu bildirmemiz lazim bunun icin springframework dan gelen
    // UserDetailsService interface sinden implement edince Sequrity sunu anladi bu benim security katmaninda
    // kullanacagim servis katmani.

    // Rollerin repositorylerini buraya enjekte edelimki gerekli logic islemleri burada yapabilelim
    // Bu Classi @RequiredArgsConstructor ile annotate etmememize ve final setlememize ragmen CTO hatasi vermemesinin
    // nedeni @AllArgsConstructor Class icindeki butun fieldlardan zaten bir constructor uretiyor.
    // zaten final da final olmayan bir field yok. Dolayisi ile @AllArgsConstructor burada butun hepsini uretiyor.
    // Gizli lombok da kullanarak constructor enjection yapmis olduk .
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;//Advisor teacher zaten bir teacher
    // oldugu icin burada tanimlamamaiza gerek yok
    private final DeanRepository deanRepository;
    private final ViceDeanRepository viceDeanRepository;
    private final AdminRepository adminRepository;
    // GuestUzer i da burada tanimlamaya gerek yok. O herzaman girebildigi icin onun kontrolunu yapmayacagiz
    // ona jwt totem gondermeyecegiz

    // UserDetailsService interface sinden asagidaki loadUserByUsername methodunu implement edip olusmasini sagliyoruz
    // loadUserByUsername User i User detail e cevirecegiz ama zaten daha once bunu yapan UserDetailsImpl class i
    // olusturmustuk Service Classinin entity si degil UserDetailsImpl Security katmaninin Entitysi
    // Security katinda buraya gelen user lari UserDetail lere cevirmek lazim.
    @Override
    @Transactional // eklenecek
    /*
        Bu özel durumda, loadUserByUsername methodu kullanıcının veritabanından bilgilerini yüklemek
        için farklı repository'leri kullanır. Bu repository'lerden her biri ayrı bir veritabanı işlemi
        gerçekleştirir. @Transactional annotasyonu, tüm bu işlemlerin tek bir transaction içinde
        gerçekleştirilmesini sağlar. Yani, eğer herhangi bir veritabanı işlemi başarısız olursa,
        tüm işlemler geri alınır (rollback) ve veritabanı tutarlı bir durumda kalır.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // icinde bulundugumuz methodn parametresinde zaten bir username geliyor ama biz bunun Student mi yoksa
        // Teacher mi yoksa diger user rollerden birisimi oldugunu bilmiyoruz. bunlarin disinda ise exception
        // firlatmamiz gerekir


        // FindByEquals turetilen bir method findBy ile Equals arasina Classin ismini koyarak yeni bir FindByEquals
        // methodu turettik
        Student student = studentRepository.findByUsernameEquals(username);
        // CTE hatasi veriyor cunku studentRepository diye bir method yok  uzerine gelip repository packagesinde bulunan
        // StudentRepository Classinda bir method olusmasini sagliyoruz ve StudentRepository de olusan methodun retun
        // degerinide Student olarak duzeltiyoruz. assagidaki methodlarin hesinide bu sekilde yapiyoruz.
        Teacher teacher = teacherRepository.findByUsernameEquals(username);
        Dean dean = deanRepository.findByUsernameEquals(username);
        ViceDean viceDean = viceDeanRepository.findByUsernameEquals(username);
        Admin admin = adminRepository.findByUsernameEquals(username);

        //yukaridaki statement ler Optional yapida da calistigi icin null gelmis olabilir. Bunun handle edilmesi lazim.
        //UserDetailsImpl Classin da User i UserDetail bicimine ceviren constructor u yazmistik
        if(student != null) { //student null degilse
            return new UserDetailsImpl(student.getId(), //Useri UserDetail e ceviriyoruz. UserDetailsImpl git
                                                        // studentin Id sini getir
                    student.getUsername(), // username sini getir.
                    student.getName(),
                    false, // studentin gelmesi icinisAdvisorun false olmasi lazim
                    student.getPassword(),
                    student.getUserRole().getRoleType().name() // getUserRole() diyemiyoruz.
                    // enum typin String ismini almamz lazim
            );
        } else if(teacher!=null){
            return new UserDetailsImpl(teacher.getId(),
                    teacher.getUsername(),
                    teacher.getName(),
                    teacher.getIsAdvisor(),// eger Advisor sa onu getir degilse teacher i getir.
                    teacher.getPassword(),
                    teacher.getUserRole().getRoleType().name());
        } else if(dean != null) {
            return new UserDetailsImpl(dean.getId(),
                    dean.getUsername(),
                    dean.getName(),
                    false,
                    dean.getPassword(),
                    dean.getUserRole().getRoleType().name());

        }else if(viceDean != null) {
            return new UserDetailsImpl(viceDean.getId(),
                    viceDean.getUsername(),
                    viceDean.getName(),
                    false,
                    viceDean.getPassword(),
                    viceDean.getUserRole().getRoleType().name());
        }else if(admin != null) {
            return new UserDetailsImpl(admin.getId(),
                    admin.getUsername(),
                    admin.getName(),
                    false,
                    admin.getPassword(),
                    admin.getUserRole().getRoleType().name());
        }
        //iflere girmezse return yapacagi birsey olmasi lazim yoksa CTE hatasi veriri bunun icin asagida if lere
        // girmezse diye exception donduruyoruz
        throw new UsernameNotFoundException("User '" + username + "' not found"); //Springframework security in kendi Exception
    }
}