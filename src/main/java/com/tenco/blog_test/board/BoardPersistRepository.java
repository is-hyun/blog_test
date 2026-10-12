package com.tenco.blog_test.board;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardPersistRepository {

    private final EntityManager em;

    // 게시글 저장
    @Transactional
    public Board save(Board board) {

        em.persist(board);
        return board;

    }

    // 게시글 전체 목록
    public List<Board> findAll() {

        String jpql = """
                SELECT b FROM Board b ORDER BY b.createdAt DESC
                """;

        return em.createQuery(jpql, Board.class).getResultList();
    }

    // 게시글 상세보기
    // 기본키로 게시글 단건 조회(1차 캐쉬 활용)
    public Board findById(Long id) {
        Board board = em.find(Board.class, id);
        // find() 특징:
        // 1. 기본키로만 조회 가능
        // 2. 1차 캐시에 먼저 찾기 시도
        // 3. 없으면 DB에서 조회 후 1차 캐시에 저장
        // 4. 영속 상태로 만든 후 반환
        return board;
    }

    // JPQL을 사용한 조회 방법
    public Board findByIdWithJPQL(Long id) {
        String jpql = """
                SELECT b FROM Board b WHERE b.id = :id
                """;
        try {
            return em.createQuery(jpql, Board.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return  null;
        }
        // JPQL 단점 :
        // 1. 1차 캐시 우회하여 항상 DB에 접근
        // 2. 코드가 복잡할 수 있음
        // 3. getSingleResult() 예외 처리 필요
    }

    // 게시글 삭제하기 (영속성 컨텍스트를 활용한 안전한 삭제)
    @Transactional
    public void deleteById(Long id) {
        // 1. 먼저 삭제할 엔티티를 영속 상태로 조회 부터 해야 함
        Board boardEntity = em.find(Board.class, id);
        // 2 엔티티 존재 여부 확인 (안전한 삭제)
        if (boardEntity == null) {
            throw new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다");
        }
        // 3. 영속 상태의 엔티티를 삭제 상태 변경
        em.remove(boardEntity);
    }

    // 게시글 수정
    @Transactional
    public void updateById(Long id, BoardRequest.UpdateDto reqDto) {

        // 1. 수정할 엔티티를 먼저 조회 후 영속 상태로 만듬
        Board boardEntity = em.find(Board.class, id);

        // 2. 엔티티 존재 여부 확인
        if (boardEntity == null) {
            throw new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다");
        }

        boardEntity.update(reqDto);
    }

}