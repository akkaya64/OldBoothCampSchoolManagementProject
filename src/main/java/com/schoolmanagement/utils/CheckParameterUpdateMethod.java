package com.schoolmanagement.utils;

import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod { //updatet edilen bilgiler ile orjinel bilgilerin ayni olup olmadigi kontrol edilecek

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){
        //bu method parametre olarak user bilgilerini (User user) ile DB den alacak yeni girilen degerleride
        // (BaseUserRequest baseUserRequest) classinidan alacak
        // Bu methodu kontrol yapilacak tum class larda calisabilmek icin parametre olarak herhangi bir rolun
        // (Ornek: DeanRequest) request claasini degilde onlarin parent classi olan BaseUserRequest parametre olarak veriyoruz

        // hangi filedlarin kontrol edilecegini burada belirtiyoruz
        //DB deki user a git Ssn degerini al bunu requestten gelen (baseUserRequest.getSsn() deger ile karsilastir ve
        // bir true yada false bir deger dondur. .equalsIgnoreCase boolean bir deger dondurur
        return user.getSsn().equalsIgnoreCase(baseUserRequest.getSsn())
                || user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber())
                || user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername());

        //bunlardan herhangi birisi true dondururse DeanService classindaki update methodundaki checkDuplicate methodu calisacak

    }
}
