package com.tenco.blog_test.board;

import com.tenco.blog_test.util.MyDateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Table(name = "board_tb")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String username;

    @CreationTimestamp
    private Timestamp createdAt;

    // 비즈니스 로직을 위한 생성자
    @Builder
    public Board(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
    }

    // 시간 포맷 메서드
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }
    // 자신의 상태값을 변경하는 메서드 추가 (영속성 엔티티를 수정하는 메서드)
    public void update(BoardRequest.UpdateDto updateDto) {
        // 비즈니스 규칙 검증
        updateDto.validate();

        // 영속 상태에 있는 엔티티의 필드값을 변경
        this.title = updateDto.getTitle();
        this.content = updateDto.getContent();
    }
}