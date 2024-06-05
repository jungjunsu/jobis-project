package com.ssafy.global.oauth2.service;

import com.ssafy.domain.users.entity.SocialType;
import com.ssafy.domain.users.entity.Users;
import com.ssafy.domain.users.exception.DuplicatedEmailException;
import com.ssafy.domain.users.service.UserAuthService;
import com.ssafy.domain.users.service.UserService;
import com.ssafy.global.oauth2.userinfo.OAuth2UserInfo;
import com.ssafy.global.oauth2.util.CustomOAuth2User;
import com.ssafy.global.oauth2.util.OAuthAttributes;
import com.ssafy.global.response.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserAuthService userAuthService;
    private final UserService userService;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException, RestApiException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        log.info("{} :: 소셜 제공 API >> {}", socialType, attributes);

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        Users createdUser = getUser(extractAttributes, socialType);

        return new CustomOAuth2User(
                // 권한 없으니 null 반환
                null,
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getId().toString(),
                createdUser.getEmail()
        );
    }

    private SocialType getSocialType(String registrationId) {
        if(NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        if(KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    private Users getUser(OAuthAttributes attributes, SocialType socialType) {
        OAuth2UserInfo oauth2UserInfo = attributes.getOauth2UserInfo();
        String userEmail = oauth2UserInfo.getEmail();

        // 이미 가입된 이메일이 있는지 확인
        boolean alreadyExistUser = userAuthService.checkEmail(userEmail);

        // 이미 가입된 이메일이 있는 경우
        if (!alreadyExistUser) {
            // 이미 가입된 이메일과 소셜 타입을 이용하여 해당 사용자 정보 조회
            Users findUser = userAuthService.findUserByEmailAndSocialType(socialType, oauth2UserInfo.getId());

            if (findUser != null) {
                return findUser;
            } else {
                // 해당 이메일로 가입된 사용자 정보가 없는 경우 예외 처리
                throw new OAuth2AuthenticationException(new OAuth2Error("already_registered"), "이미 가입된 이메일입니다.");
            }
        } else {
            // 가입되지 않은 사용자인 경우 새로운 사용자로 등록
            return saveUser(attributes, socialType);
        }
    }


    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private Users saveUser(OAuthAttributes attributes, SocialType socialType) {
        Users createdUser = Users.createSocialUser(socialType, attributes.getOauth2UserInfo());
        log.info("생성된 유저 저장>> "+ createdUser);
        return userService.socialRegister(createdUser);
    }
}
