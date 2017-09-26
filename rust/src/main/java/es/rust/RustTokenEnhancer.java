package es.rust;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class RustTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(
     OAuth2AccessToken accessToken, 
     OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        
        additionalInfo.put("organization", authentication.getName());
        
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}