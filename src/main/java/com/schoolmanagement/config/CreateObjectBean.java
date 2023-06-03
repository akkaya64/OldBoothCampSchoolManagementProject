package com.schoolmanagement.config;

import com.schoolmanagement.payload.dto.ViceDeanDto;
import org.springframework.context.annotation.Configuration;

import com.schoolmanagement.payload.dto.DeanDto;
import org.springframework.context.annotation.Bean;

@Configuration
public class CreateObjectBean {

    @Bean
    public DeanDto deanDTO() {
        return new DeanDto();
    }


    @Bean
    public ViceDeanDto viceDeanDto(){
        return new ViceDeanDto();
    }

}