package com.ssafy.global.oauth2.handler;

import com.ssafy.domain.users.dto.JwtDto;
import com.ssafy.domain.users.service.RefreshTokenService;
import com.ssafy.global.oauth2.util.CustomOAuth2User;
import com.ssafy.global.oauth2.util.RedirectUriStorage;
import com.ssafy.global.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RedirectUriStorage redirectUriStorage;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");

        // 저장해둔 CustomOAuth2User 추출
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 토큰 생성
        JwtDto jwtDto = jwtUtil.generateJwtDto(oAuth2User.getUserId());

        // redis에 refreshToken 저장
        refreshTokenService.addRefreshToken(Integer.valueOf(oAuth2User.getUserId()), jwtDto.getRefreshToken());

        // 응답객체 타입 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 헤더에 토큰 담아 전송
        jwtUtil.setAccessAndRefreshToken(response, jwtDto);

        String act = jwtDto.getType()+" "+jwtDto.getAccessToken();
        String rft = jwtDto.getRefreshToken();
        String SOCIAL_SUCCESS_URL = "/login-success";
        response.sendRedirect(redirectUriStorage.getRedirectUri() + SOCIAL_SUCCESS_URL + "/" + act + "/" + rft);
    }
}
