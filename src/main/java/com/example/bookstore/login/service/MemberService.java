package com.example.bookstore.login.service;

import com.example.bookstore.configuration.MailComponents;
import com.example.bookstore.login.Dto.JoinDto;
import com.example.bookstore.login.Dto.LoginDto;
import com.example.bookstore.login.entity.Member;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.UserErrorCode;
import com.example.bookstore.login.repository.MemberRepository;
import com.example.bookstore.login.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String tokenKey;
    private Long expireTimeMs = 1000*60*60L;

    public boolean join(JoinDto member){
        if(memberRepository.existsByUserName(member.getUserName())){
            throw new BookUserException(UserErrorCode.USER_ID_EXIST_ERROR);
        }
        String AuthKey = UUID.randomUUID().toString();
        String password = encoder.encode(member.getPassword());

        memberRepository.save(Member.builder()
                .userName(member.getUserName())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .password(password)
                .authDate(LocalDateTime.now())
                .idAuth(false)
                .idAuthKey(AuthKey)
                .authDate(LocalDateTime.now())
                .address1(member.getAddress1())
                .addressData1(member.getAddressData1())
                .address2(member.getAddress2())
                .addressData2(member.getAddressData2()).build()
        );
        String subject = "BookStore ????????? ???????????????.";
        String text = "<p>BookStore ????????? ???????????????.</p><p> </p>" +
                "<p>????????? ???????????? ?????? ????????? ?????? ?????????.</p>" + "<p> </p><p> </p><p> </p>"
                + "<div><a href='http://localhost:8080/api/member/email/key/" + AuthKey + "/id/" + member.getUserName() + "'>???????????? ??????</a></div>";
        mailComponents.sendMail(member.getEmail(), subject, text);

        return true;
    }

    public ResponseEntity<?> authKey(String authKey, String username) {
        Optional<Member> opMember = memberRepository.findByUserName(username);
        if(opMember.isEmpty()){
            return ResponseEntity.badRequest().body("???????????? ????????????.");
        }
        Member member = opMember.get();
        if(!member.getIdAuthKey().equals(authKey)){
            return ResponseEntity.badRequest().body("Key??? ???????????? ????????????.");
        }
        if(!member.getAuthDate().plusMinutes(10L).isAfter(LocalDateTime.now())){
            member.setAuthDate(LocalDateTime.now());
            member.setIdAuthKey(UUID.randomUUID().toString());
            memberRepository.save(member);
            String subject = "BookStore ????????? ???????????????.";
            String text = "<p>BookStore ????????? ???????????????.</p><p> </p>" +
                    "<p>????????? ???????????? ?????? ????????? ?????? ?????????.</p>" + "<p> </p><p> </p><p> </p>"
                    + "<div><a href='http://localhost:8080/api/member/email/key/" + member.getIdAuthKey() + "/id/" + member.getUserName() + "'>???????????? ??????</a></div>";
            mailComponents.sendMail(member.getEmail(), subject, text);
            return ResponseEntity.badRequest().body("??????????????? ?????????????????????.\n ????????? ???????????? ?????? ???????????????.");
        }
        member.setIdAuth(true);
        memberRepository.save(member);
        return ResponseEntity.ok().body("????????? ?????????????????????.");
    }

    public String login(LoginDto loginDto) {
        Member member = memberRepository.findByUserName(loginDto.getUserName())
                .orElseThrow(()-> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        if(!encoder.matches(loginDto.getPassword(), member.getPassword()))
        {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        if(!member.isIdAuth()){
            throw new BookUserException(UserErrorCode.USER_NOT_AUTH_ERROR);
        }
        String token = JwtTokenUtil.createToken(member.getUserName(),this.tokenKey,this.expireTimeMs);
        return token;
    }

    public boolean idCheckDto(String userName) {
        return this.memberRepository.existsByUserName(userName);
    }
    public boolean idPasswordCheck(String userName,String password){
        Member member = this.memberRepository.findByUserName(userName).orElseThrow(()-> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        if(!encoder.matches(password, member.getPassword()))
        {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        return true;
    }


    public List<Member> test() {
        List<Member> memberList = memberRepository.findByUserNameContaining("4ljw");
        if(memberList.size() == 0){
           throw new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
        return memberList;
    }
}
