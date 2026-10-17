package com.tenco.blog_test.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserPersistRepository {

    private final EntityManager em;

    // A. 회원 가입
    @Transactional
    public User save(User user) {
        em.persist(user); // 영속 상태 전환
        return user;
    }

    // A-2. 사용자명 중복 체크 메서드
    public User findByUsername(String username) {
        String jpql = """
                SELECT u FROM User u WHERE u.username = :username
                """;

        try {
            return em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            // 사용자를 찾을 수 없는 경우
            return null;
        }
    }

    // B. 로그인용 회원 확인 메서드
    public User findByUsernameAndPassword(String username, String password) {
        try {
            String jpql = """
                    SELECT u FROM User u WHERE u.username = :username AND u.password = :password
                    """;
            Query query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            // 일치하는 사용자 없음 OR 에러 발생 >> 로그인 실패(null)
            return null;
        }
    }

    // C. 회원 정보 수정
    @Transactional
    public User updatedById(Long id, UserRequest.UpdateDto updateDto) {

        // 수정할 User 엔티티를 영속 상태로 조회
        User userEntity = findById(id);

        userEntity.update(updateDto.getPassword());
        return userEntity;
    }

    // C-2. 수정할 회원 정보 조회 메서드
    public User findById(Long id) {

        // User 엔티티를 영속 상태로 조회
        User user = em.find(User.class, id);
        if (user == null) {
            throw new RuntimeException("사용자 정보를 불러올 수 없습니다");
        }
        return user;
    }

}
