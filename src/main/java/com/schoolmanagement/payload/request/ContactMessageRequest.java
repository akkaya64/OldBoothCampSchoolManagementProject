package com.schoolmanagement.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest implements Serializable {

    @NotNull(message = "Please enter name")
    @Size(min=4, max=16, message = "Your name should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .") // bosluk olmayacak ve en az 1 karakter olacak
    private String name ;

    @Email(message = "Please enter valid email")
    @Size(min=5, max=20, message = "Your email should be at least 5 chars")
    @NotNull(message = "Please enter your email")
    private String email ;

    @NotNull(message = "Please enter subject")
    @Size(min=4, max=50, message = "Your subject should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your subject must consist of the characters .")
    private String subject ;

    @NotNull(message = "Please enter message")
    @Size(min=4, max=50, message = "Your message should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .")
    private String message ;
}

/*
    public static void main(String[] args) {

        String num = "120-6";
        operation(num);


    }

    public static void operation(String str) {
        String newNum = str.replaceAll("\\p{Punct}", "!");
        String arr[] = newNum.split("!");

        int idx = arr[0].length();
        String oprtr = str.substring(idx, idx + 1);
        int num1 = Integer.valueOf(arr[0]);
        int num2 = Integer.valueOf(arr[1]);


        switch (oprtr) {
            case "+":
                System.out.println("Result is : " + (num1 + num2));
                break;
            case "-":
                System.out.println("Result is : " + (num1 - num2));
                break;
            case "*":
                System.out.println("Result is : " + (num1 * num2));
                break;
            case "/":
                System.out.println("Result is : " + (num1 / num2));
                break;
        }
    }

 */