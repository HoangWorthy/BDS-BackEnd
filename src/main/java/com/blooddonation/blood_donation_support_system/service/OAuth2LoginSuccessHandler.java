package com.blooddonation.blood_donation_support_system.service;

        import com.blooddonation.blood_donation_support_system.util.JwtUtil;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.oauth2.core.user.OAuth2User;
        import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
        import org.springframework.stereotype.Component;

        @Component
        public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

            private final JwtUtil jwtUtil;

            @Autowired
            public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
                this.jwtUtil = jwtUtil;
            }

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Authentication authentication) throws java.io.IOException {

                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                String token = jwtUtil.saveOAuth2User(oAuth2User);

                response.setHeader("Set-Cookie", "jwt-token=" + token + "; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600");
                response.sendRedirect("/login");
            }
        }