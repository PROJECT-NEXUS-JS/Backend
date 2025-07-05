package com.example.nexus.mypage.dto;

import com.example.nexus.mypage.domain.User;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class Create {
        private String username;
        private String email;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String username;
        private final String email;

        public Response(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }
}