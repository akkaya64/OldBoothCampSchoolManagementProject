package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    public UserRole getUserRole(RoleType roleType) {

        Optional<UserRole> userRole = userRoleRepository.findByERoleEquals(roleType);
        return userRole.orElse(null);
    }

    //Runner Tarafi icin gerekli method
    public List<UserRole> getAllUserRole() {
        // Public Collection<Object> getAllUserRole() Runner Class dan otomatik olusturulan methodun orjinal hali.
        // Runner class dan olusturduk Bunun bir list yapida gelmesini istiyoruz ve UserRole ler gelecegini belirtiyoruz

        return userRoleRepository.findAll(); //Spring DataJPA nin findAll() methodu
        //ODEV: Neden REPO ya gidip code yazmamiza gerek kalmadi.
    }

    //Runner Tarafi icin gerekli method
    public UserRole save(RoleType roleType) {
        // public void save(RoleType roleType)
        // Kaydedilen data nin kendisi Kullaniciya donsun diye bir PO dan gelen  requirement vardi. bunun icin void olan
        // donen degerinin yerien UserRole yaziyoruz

        // Oncelikle userRoleRepository methodunu kullanarak olusturulacak olan role DB de varmi kontrol etmemiz lazim
        if (userRoleRepository.existsByERoleEquals(roleType)) {
            // Spring DataJPA yida kullanaral existsBy methodunu kullanabilirdik burada kendimiz olusturmayi sectik ve
            // bunun bir roleType oparametresi ile gelecegini belirttik. existsByERoleEquals(roleType)) deki By(E)Ro (E)
            // baska yerden turetilmesin diye konuldu
            // Eger boyle bir role type varsa bunun olusturulmamasi lazim.
            throw new ConflictException("This role is already registered");
            // Bu varsa zaten kaydetme ve exxeption firlat deme durumu.
        }

        // Boyle bir role yoksa;
        // UserRole create etmemiz lazim
        UserRole userRole = UserRole.builder().roleType(roleType).build();
        // UserRole git, builder() methodunu cagir, buna bir role bilgisi vermemiz lazim methodun parametresinden gelen
        // roleType gore setle diyoruz roleType(roleType) ve bununda zaten bir tane degeri oldugu icin Id sinide
        // otomatik kendisi generate edecek, sonra bir tane boyle bir obje olustur diyoruz build().
        // Bunuda UserRole Data tipinde bir userRole ata diyoruz

        return userRoleRepository.save(userRole);

    }
}