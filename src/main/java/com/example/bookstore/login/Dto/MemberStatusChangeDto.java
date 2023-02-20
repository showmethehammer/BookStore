package com.example.bookstore.login.Dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MemberStatusChangeDto {
    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String userName;
    @NotBlank(message = "이름은 필수 항목 입니다.")
    private String name;
    @Email(message = "Email 형식에 맞게 입력하세요.")
    @NotBlank(message = "Email은 필수 항목 입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 항목 입니다.")
    private String Password;
    @NotBlank(message = "전화번호는 필수 항목 입니다.")
    private String phone;
    @NotBlank(message = "주소는 필수 입력항목입니다.")
    private String address1;
    private String addressData1;
    private String address2;
    private String addressData2;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PasswordChange{
        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String userName;
        @NotBlank(message = "비밀번호는 필수 입력 항목 입니다.")
        private String Password;
        @NotBlank(message = "변경 비밀번호는 필수 입력 항목 입니다.")
        private String passwordChange;
    }
}
