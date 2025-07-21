package dto;

import java.util.List;


public class JwtResponse {
    @dto.Data

        private String token;
        private String type = "Bearer";
        private String id;
        private String username;
        private String email;
        private boolean profileCompleted;
        private List<String> roles;

        public JwtResponse(String token, String id, String username, String email, boolean profileCompleted, List<String> roles) {
            this.token = token;
            this.id = id;
            this.username = username;
            this.email = email;
            this.profileCompleted = profileCompleted;
            this.roles = roles;
        }
    }