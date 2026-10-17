package com.tenco.blog_test.user;

import lombok.Data;

public class UserRequest {

    @Data
    public static class JoinDto {
        private String username;
        private String password;
        private String email;

        // 검증 메서드
        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자명은 필수 입력 사항입니다");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수 입력 사항입니다");
            }
            // 이메일 형식 검증
            if (!email.contains("@")) {
                throw new IllegalArgumentException("잘못된 이메일 형식입니다");
            }
        }

        // 엔티티 변환 메서드
        public User toEntity() {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .build();
        }
    }

    // B. 로그인 DTO
    @Data
    public static class LoginDto {
        private String username;
        private String password;

        // 검증 메서드
        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자명은 필수 입력 사항입니다");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다");
            }
        }
    }

    // C. 회원정보 수정 DTO
    @Data
    public static class UpdateDto {
        private String password;

        // 검증 메서드
        public void validate() {
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다");
            }
            if (password.length() < 4) {
                throw new IllegalArgumentException("비밀번호는 최소 4자 이상이어야 합니다");
            }
        }
    }
}
