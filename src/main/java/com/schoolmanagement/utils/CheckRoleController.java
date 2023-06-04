package com.schoolmanagement.utils;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.UserRoleRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CheckRoleController<E>  {

//    private final UserRoleRepository userRoleRepository;
//
//    private Optional<UserRole> checkDeanExists(In roleId) {
//        Optional<UserRole> role = userRoleRepository.findById(roleId);
//        if (!role.isPresent()) {
//            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
//        }
//        return role;
 //   }
}
