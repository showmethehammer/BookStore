package com.example.bookstore.login.service;

import com.example.bookstore.configuration.MailComponents;
import com.example.bookstore.login.Dto.*;
import com.example.bookstore.login.entity.Member;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.MemberException;
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
import java.util.Random;
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
    private Long expireTimeMs = 1000 * 60 * 60L;

    /**
     * 회원가입
     * Data 객채의 정보를 활용하여 DataBase에 저장
     * step
     * 1. 아이디 중복 확인.
     * 2. 메일 인증키 생성
     * 3. 비밀번호 암호화
     * 4. DataBase 저장
     * 5. 인증키 E-Mail 송부 Text 생성
     * 6. Email 송부
     * @param member
     * @return
     */
    public boolean join(JoinDto member) {
        // 1. 아이디 중복 확인.
        if (memberRepository.existsByUserName(member.getUserName())) {
            throw new BookUserException(UserErrorCode.USER_ID_EXIST_ERROR);
        }
        // 2. 인증키 생성
        String AuthKey = UUID.randomUUID().toString();
        // 3. 비밀번호 암호화
        String password = encoder.encode(member.getPassword());
        // 4. DataBase 저장
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
                .addressData2(member.getAddressData2())
                .userMoney(member.getUserMoney())
                .build());
        // 5. 인증키 E-Mail 송부 Text 생성
        String subject = "BookStore 가입을 환영합니다.";
        String text = "<p>BookStore 가입을 환영합니다.</p><p> </p>" +
                "<p>본인이 맞으시면 아래 링크를 클릭 하세요.</p>" + "<p> </p><p> </p><p> </p>"
                + "<div><a href='http://localhost:8080/api/member/email/key/" + AuthKey + "/id/" + member.getUserName() + "'>회원가입 완료</a></div>";
        // 6. E-Mail 송부
        mailComponents.sendMail(member.getEmail(), subject, text);
        return true;
    }

    /**
     * UserName과 인증키를 매칭하여 실명인증을 함
     * 확인 사항
     * 1. ID 검색하여 결과값 유/무
     * 2. 인증키와 ID의 일치 확인
     * 3. 회원가입때 메일송부한 시간과 인증요청 시간 비교
     *
     * @param authKey 인증키
     * @param username 비교할 UserName
     * @return
     */
    public ResponseEntity<?> authKey(String authKey, String username) {
        // 회원 ID를 찾는다.
        Optional<Member> opMember = memberRepository.findByUserName(username);
        // 회원 ID가 없으면 알람.
        if (opMember.isEmpty()) {
            return ResponseEntity.badRequest().body("아이디가 없습니다.");
        }
        // 회원 ID가 있으면 Member 객체로 형변환
        Member member = opMember.get();
        // 인증키와 검색한 회원 ID가 일치하지 않으면 알람.
        if (!member.getIdAuthKey().equals(authKey)) {
            return ResponseEntity.badRequest().body("Key가 일치하지 않습니다.");
        }
        // 메일송부 시간과 인증요청한 시간을 비교하여 10분이상 경과했을때 메일을 다시보낸 후 인증시간 초과 알람 송부.
        if (!member.getAuthDate().plusMinutes(10L).isAfter(LocalDateTime.now())) {
            member.setAuthDate(LocalDateTime.now());
            member.setIdAuthKey(UUID.randomUUID().toString());
            memberRepository.save(member);
            String subject = "BookStore 가입을 환영합니다.";
            String text = "<p>BookStore 가입을 환영합니다.</p><p> </p>" +
                    "<p>본인이 맞으시면 아래 링크를 클릭 하세요.</p>" + "<p> </p><p> </p><p> </p>"
                    + "<div><a href='http://localhost:8080/api/member/email/key/" + member.getIdAuthKey() + "/id/" + member.getUserName() + "'>회원가입 완료</a></div>";
            mailComponents.sendMail(member.getEmail(), subject, text);
            return ResponseEntity.badRequest().body("인증시간을 초과하였습니다.\n 제송부 드리오니 다시 인증하세요.");
        }
        //
        member.setIdAuth(true);
        memberRepository.save(member);
        return ResponseEntity.ok().body("인증에 성공하였습니다.");
    }


    /**
     * 로그인 Service
     * 아이디와 Password를 이용하여 로그인 토큰 생성
     * Step
     * 1. ID 검색
     * 2. 비밀번호 확인
     * 3. E-Mail인증 확인
     * 4. 토큰 리턴
     *
     * @param loginDto ID와 비밀번호 가 저장된 객체
     * @return
     */
    public String login(LoginDto loginDto) {
        // 1. ID 검색
        Member member = memberRepository.findByUserName(loginDto.getUserName())
                .orElseThrow(() -> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        // 2. 비밀번호 확인
        if (!encoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        // 3. E-Mail인증 확인
        if (!member.isIdAuth()) {
            throw new BookUserException(UserErrorCode.USER_NOT_AUTH_ERROR);
        }
        // 4. 토큰 리턴
        String token = JwtTokenUtil.createToken(member.getUserName(), this.tokenKey, this.expireTimeMs);
        return token;
    }

    /**
     * ID 검색을 하기위한 메소드
     * 중복하는 ID 가 있는지 확인
     *
     * @param userName 아이디 검색
     * @return
     */
    public boolean idCheckDto(String userName) {
        // 아이디 검색하여 검색이 되면 true
        return this.memberRepository.existsByUserName(userName);
    }
    /**
     * 정보등을 변경하기위해 ID와 비밀벌호를 재검증하기위한 메소드
     * step
     * 1. ID 검색
     * 2. 비밀번호 확인
     * @param userName  ID
     * @param password  비밀번호
     * @return
     */
    public boolean idPasswordCheck(String userName, String password) {
        // 1. ID 검색
        Member member = this.memberRepository.findByUserName(userName).orElseThrow(() -> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        // 2. 비밀번호 확인.
        if (!encoder.matches(password, member.getPassword())) {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        return true;
    }

    /**
     * 검색 및 기타 테스트용도로 만듬.
     * 그외 사용하지 안음.
     * @return
     */
    public List<Member> test() {
        List<Member> memberList = memberRepository.findByUserNameContaining("4ljw");
        if (memberList.size() == 0) {
            throw new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
        return memberList;
    }

    /**
     * 비밀번호 변경
     * 아이디, 비밀번호, 새비밀번호를 확인하여 비밀번호 변경
     * Step
     * 1. 아이디 검색해서 불러옴.
     * 2. 비밀번호 확인
     * 3. 새비밀번호 암호화
     * 4. 새비밀번호 저장
     *
     * @param passwordChange
     */
    public void passwordChange(MemberStatusChangeDto.PasswordChange passwordChange) {
        // 1. 아이디 검색
        Member member = this.memberRepository.findByUserName(passwordChange.getUserName()).orElseThrow(() -> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        // 2. 비밀번호 확인
        if (!encoder.matches(passwordChange.getPassword(), member.getPassword())) {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        // 3. 비밀번호 암호화
        String password = encoder.encode(passwordChange.getPassword());
        // 4. 새비밀번호 저장
        member.setPassword(password);
        memberRepository.save(member);
    }

    /**
     * 회원정보 변경
     * 변경할 정보가 내장된 객체를 확인하여 적용
     * Step
     * 1. ID 검색
     * 2. 비밀번호 확인
     * 3. 정보 변경
     * 4. 저장
     *
     * @param statusChange 회원정보를 변경하기위한 객체가 저장됨.
     */
    public void memberStatusChange(MemberStatusChangeDto statusChange) {
        // 1. ID 검색
        Member member = this.memberRepository
                .findByUserName(statusChange.getUserName())
                .orElseThrow(() -> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));

        if (!encoder.matches(statusChange.getPassword(), member.getPassword())) {
            throw new BookUserException(UserErrorCode.USER_ID_EMAIL_FOUND_ERROR);
        }
        member.setPhone(statusChange.getPhone());
        member.setName(statusChange.getName());
        member.setEmail(statusChange.getEmail());
        member.setAddress1(statusChange.getAddress1());
        member.setAddressData1(statusChange.getAddressData1());
        member.setAddress2(statusChange.getAddress2());
        member.setAddressData2(statusChange.getAddressData2());
        this.memberRepository.save(member);
    }

    /**
     * 아이디 분실로 인한 아이디 Email 요청
     *
     * Step
     * 1. Email을 활용하여 해당하는 ID 확인
     * 2. 검색된 List에서 핸드폰 번호화 회원 이름을 확인하여 일치하면 해당 정보 활용. 없으면 알람.
     * 3. E-Mail로 회원 ID 송부
     *
     * @param idSearch
     */
    public void idSearch(IdSearch idSearch) {
        // 1. Email을 활용하여 해당하는 ID 확인
        List<Member> members = memberRepository.findAllByEmailContaining(idSearch.getEmail());
        Member member = null;
        // 2. 검색된 List에서 핸드폰 번호화 회원 이름을 확인하여 일치하면 해당 정보 활용. 없으면 알람.
        if (members.size() == 1) {
            if (members.get(0).getPhone().equals(idSearch.getPhone())
                    && members.get(0).getName().equals(idSearch.getName())) {
                member = members.get(0);
            }
        } else if (members.size() > 1) {
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getPhone().equals(idSearch.getPhone())
                        && members.get(i).getName().equals(idSearch.getName())) {
                    member = members.get(i);
                }
            }
        }
        if (member == null) {
            throw new BookUserException(UserErrorCode.USER_NOT_DATA_ERROR);
        }

        // 3. E-Mail로 회원 ID 송부
        String subject = "BookStore 요청하신 ID 전송";
        String text = "<p>ID를 Email송부 요청하여 송부드립니다.</p><p> </p>" +
                "<p>본인이 가닌경우 고객센터에 문의하세요.</p>" + "<p> </p><p> </p>" +
                "<p> " + member.getName() + " 님의 ID는  " + member.getUserName() + "  입니다. </p>";
        mailComponents.sendMail(member.getEmail(), subject, text);
    }

    /**
     * 비밀번호 분실시 임시비밀번호 발송.
     * ID, 이름, 전화번호를 활용하여 임시비밀번호 요청
     * 1. 아이디 검색
     * 2. 이름과 핸드폰번호 확인하여 다르면 알람.
     * 3. 임시비밀번호 생성 및 암호화
     * 4. 비밀번호 변경 후 저장
     * 5. E-Mail 전송
     *
     * @param passwordSearch ID, 이름, 전화번호가 담긴 객체
     */
    public void passwordSearch(PasswordSearch passwordSearch) {
        // 1. 아이디 검색
        Member member = this.memberRepository.findByUserName(passwordSearch
                .getUserName()).orElseThrow(() -> new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        // 2. 이름과 행드폰번호 확인하여 다르면 알람.
        if(!member.getName().equals(passwordSearch.getName()) || !member.getPhone().equals(passwordSearch.getPhone())){
            throw new BookUserException(UserErrorCode.USER_NOT_DATA_ERROR);
        }
        // 3. 임시비밀번호 생성 및 암호화
        String passwordSp = "" + (int)(Math.random()*100000000);
        String password = encoder.encode(passwordSp);
        // 4. 비밀번호 변경 후 저장
        member.setPassword(password);
        memberRepository.save(member);
        // 5. E-Mail 전송
        String subject = "BookStore 요청하신 임시 비밀번호 전송";
        String text = "<p>요청하신 임시비밀번호를 Email송부 드립니다.</p><p> </p>" +
                "<p>본인이 가닌경우 고객센터에 문의하세요.</p>" + "<p> </p><p> </p>" +
                "<p> " + member.getName() + " 님의 임시 비밀번호는    " + passwordSp + "    입니다. </p>";
        mailComponents.sendMail(member.getEmail(), subject, text);
    }
}
