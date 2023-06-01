package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    @Query("select r from UserRole r where r.roleType = ?1")
    Optional<UserRole> findByERoleEquals(RoleType roleType);

    @Query("select (count(r)>0 from UserRole r where r.roleType = ?1")//bir JP Query yaziyoruz roleType tablosunda
        // gelen role sayisi neyse onu kontrol ediyoruz. count(r) roleType icin allies r harfini kullandik roleType i
        // saydiriyoruz, count(r) >0 buyukse getir UserRole r den, r.roleType dekine ata, neyi  asagida yazdigimiz
        // existsByERoleEquals(RoleType roleType); daki roleType("?1")'i
    boolean existsByERoleEquals(RoleType roleType);
}