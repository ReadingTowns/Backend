package kr.co.readingtown.authentication.oauth;

import kr.co.readingtown.authentication.client.MemberClient;
import kr.co.readingtown.authentication.domain.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberClient memberClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = null;
        String username = null;

        if (provider.equals("KAKAO")) {
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            username = (String) properties.get("nickname");
        } else if (provider.equals("GOOGLE")) {
            providerId = (String) attributes.get("id");
            username = (String) attributes.get("name");
        }

        // member 저장
        memberClient.registerMember(provider, providerId, username);

        return new CustomOAuth2User(
                attributes,
                oAuth2User.getAuthorities(),
                providerId,
                provider
        );
    }
}