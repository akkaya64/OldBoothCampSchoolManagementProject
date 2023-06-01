package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.concretes.ViceDean;
import com.schoolmanagement.repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DeanRepository deanRepository;
    private final ViceDeanRepository viceDeanRepository;
    private final AdminRepository adminRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByUsernameEquals(username);
        Teacher teacher = teacherRepository.findByUsernameEquals(username);
        Dean dean = deanRepository.findByUsernameEquals(username);
        ViceDean viceDean = viceDeanRepository.findByUsernameEquals(username);

    }
}
