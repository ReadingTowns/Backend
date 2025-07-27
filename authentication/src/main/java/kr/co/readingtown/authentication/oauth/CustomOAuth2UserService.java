package kr.co.readingtown.authentication.oauth;

import kr.co.readingtown.authentication.domain.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = null;
        if (provider.equals("kakao"))
            providerId = String.valueOf(attributes.get("id"));

        // [TODO] : google일때 .get("sub")으로 providerId 저장해주기

        return new CustomOAuth2User(
                attributes,
                oAuth2User.getAuthorities(),
                providerId,
                provider.toUpperCase()
        );
    }
}