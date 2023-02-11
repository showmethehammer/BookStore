package com.example.bookstore.login.utils;

import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.MemberException;
import com.example.bookstore.login.exception.UserErrorCode;
import com.example.bookstore.login.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println(authorization);
        if(authorization == null){
            log.error("authorization 이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        // 꺼내기
        String token = authorization;
        // 유효 날짜 확인
        if(JwtTokenUtil.isExpired(token,this.secretKey)){
            log.error("토큰 기간이 만료되었습니다.");
            filterChain.doFilter(request,response);
            return;
        }

        String userName = JwtTokenUtil.getUserName(token,this.secretKey);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
