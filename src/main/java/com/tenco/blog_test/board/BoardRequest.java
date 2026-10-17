package com.tenco.blog_test.board;

import com.tenco.blog_test.user.User;
import lombok.Data;

public class BoardRequest {

    @Data
    public static class SaveDto {
        private String title;
        private String content;
        private String username;

        // 검증 메서드
        public void validate() {
            // 사용자명 검증 코드 삭제
            if(title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }

        // 엔티티 변환 메서드
        public Board toEntity(User user) {
            return Board.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user) // 세션에서 가져온 User 객체 설정
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        private String title;
        private String content;

        // 검증 메서드
        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }

}
