package com.schoolmanagement.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    // Pom a bagimliliklarimiz ekliyoruz.
    // Bu Class i User lari UserDetails seklinde security katmanina tanitmak icin olusturduk. SpringSecurity
    // UserDetails leri securtiye tanitmak istenirse UserDetail interface den implement edilmesini ister.
    // UserDetails interface sinden glen tum methodlari implement ettik.

    //User Details ile gerekli fieldlari olusturuyoruz.
    private Long id;

    private String username;

    private String name;

    private Boolean isAdvisor;
    //Teacher Rolleri de gelecek

    @JsonIgnore// kullaniciya gitmemesi icin
    private String password;

    private Collection<? extends GrantedAuthority> authorities;//Rolleri tanimlamak icin Collection Type'nda generic
    // bir yapi olusturuyoruz <? extends GrantedAuthority> den turuyen  herhangi bir authorities.

    // Bu fieldlarin constructor larini olusturuyoruz User Type larin icinde authorities diye bir tur olmadigi icin
    // Collection<? extends GrantedAuthority> authorities fieldinin constructor unu olusturmuyoruz
    public UserDetailsImpl(Long id, String username, String name, Boolean isAdvisor, String password, String role) {
        // yukarida yazdigimiz "String role" parametresi uzerinden kullanicinin gelen rolunu GrantedAuthority turune cevirebilecegiz
        this.id = id;
        this.username = username;
        this.name = name;
        this.isAdvisor = isAdvisor;// Userlardan birisi Teacher olabilir, Teacher in AdviserTeacher olabilme
        // ozelligi var. AdviserTeacher diye ayrica bir kullanici yok. bu gelen Teacher in advisor ozelliginin olup
        // olmadigini denetliyor
        this.password = password;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //tek bir role olusturacagimizi hesapliyoruz ancak ilerde birden fazla role sahip olabilir. bu nedenle
        // List yapida bir grantedAuthorities olusturuyoruz
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        //BU satirla role bilgimi GrantedAuthority turune cevirmis olduk
        this.authorities = grantedAuthorities;

        // Parametreli constructor da role nin disindaki butun datalari bu constructor a gonderdigimiz user objesi
        // uzerinden alacagiz bunun haricinde role bilgisini GrantedAuthority data type na cevirmis olduk. boylelikle
        // burada User bilgimi UserDetail turune cevirmis oluyoruz

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //return null;
        return authorities; // null degil authorities dondurecegiz
    }

    @Override
    public String getPassword() {
        //return null;
        return password; //nul degil password dondurecegiz
    }

    @Override
    public String getUsername() {
        //return null;
        return username; // null degil username dondurecegiz
    }

    //Assagidaki methodlari kullanmayacagiz return larini true ya setliyoruz.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Bu method UserDetails verecek, her objeninde Equals() methodu var iki tane UserDetails i getirdigi zaman equals()
    // methodunun calisma seklini davranis seklini degistirwebiliyoruz yani baska bir tabirle  @Override edebiliyoruz.
    // Alttaki method bu classin bir methodu. bu  calassin bir instancesi tarafondan cagrilabilecek bir method Bu classin
    // instancesi tarafindan equals methiodu tarafindan cagrildigi zaman oparametre olarak bir obje almasin lazim. iste
    // bu instance paremetre oalarak aldigi obje ile ayni objemi yani kendisini kiyaslamis oluyoruz. kendisini de
    // gonderildigi zaman olabiliyor

    public boolean equals(Object o) {
        //parametresi object turunde olacak
        // normalde iki obje kiyaslanacaksa once fieldlarina bakariz iste bu davranisi degistirmek istiyorsak bunu
        // istedigimiz sekilde degis=tirebiliyoruz
        if(this == o) //this = o. Obje kendisi ile karsilastiriliyor. karsilastirdigimiz obje kendisi ise
            return true ; // return true diyoruz
        if( o== null || getClass() != o.getClass())
            //iki objenin farkli olabilmesi icin ya parametreden gelen o Objesinin null olmasi yada bu objenin
            // UserDetailsdenImpl den gelen getClass() (Class in kendisini getiriyor) methodu ile objenin o.getClass
            // methodu ayni objeleri getirmiyorsa bunlar farklidir diyoruz
            return false; // false donduruyoruz

        //User larin Idleri uzerinden kiyaslama yapilacak
        UserDetailsImpl user = (UserDetailsImpl) o;// objeyi user a ata
        //
        return Objects.equals(id, user.id); // id ile kiyaslama, Bu Class dan gelen id ile yukarida user a atadigimniz
        // id yi karsilastiriyoruz
    }
}