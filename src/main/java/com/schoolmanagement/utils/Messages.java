package com.schoolmanagement.utils;

public class Messages {

    public static final String ALREADY_SEND_A_MESSAGE_TODAY = "Error : You already sent a message with this e-mail address today" ;
    public static final String ALREADY_REGISTER_MESSAGE_USERNAME = "Error : User with username %s already register" ;
    public static final String ALREADY_REGISTER_MESSAGE_SSN = "Error : User with ssn %s already register" ;
    public static final String ALREADY_REGISTER_MESSAGE_PHONE_NUMBER = "Error : User with phone number %s already register" ;
    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You dont have any permission to change this value" ;
    // delete yerine  change tabirini kullandik cunku bu mesaji baska response lerde de kullanabiliriz mesela update methodlarinda
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found" ;
    //Message yi exception uzerinden de degil bu sekilde de handle edebiliriz. exception yok ama yinede Error messageyi yaziyoruz
    //Baska yerde de kullanabiliriz bi nedenle id bilgisini yazmiyoruz.
    public static final String NOT_FOUND_USER2_MESSAGE = "Error: User not found with id %s" ;

}