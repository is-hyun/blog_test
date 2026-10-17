package com.tenco.blog_test.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserPersistRepository userPersistRepository;

    // A. 회원가입
    // GET - http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm() {
        // templates/   <<-- 콘텐츠 루트 경로
        return "user/join-form";
    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDto joinDto, Model model) {
        log.info("==== 회원가입 요청 ====");
        log.info("사용자명 : {}", joinDto.getUsername());
        log.info("비밀번호 : {}", joinDto.getPassword());
        log.info("이메일 : {}", joinDto.getEmail());

        try {
            joinDto.validate();

            User existingUser = userPersistRepository
                    .findByUsername(joinDto.getUsername());
            if (existingUser != null) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다");
            }

            // DTO  -> 엔티티 변환
            User user = joinDto.toEntity();

            User userEntity = userPersistRepository.save(user);

            return "redirect:/login";
        } catch (Exception e) {
            log.error("회원가입 실패 : {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "user/join-form";
        }
    }

    // B. 로그인
    // GET - http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    @PostMapping("/login")
    public String login(UserRequest.LoginDto loginDto, HttpSession session, Model model) {
        log.info("==== 로그인 요청 ====");
        log.info("사용자명 : {}", loginDto.getUsername());

        try {
            loginDto.validate();
            User sessionUser = userPersistRepository.findByUsernameAndPassword(loginDto.getUsername(), loginDto.getPassword());

            if (sessionUser == null) {
                // 로그인 실패 - 일치하는 사용자 없음
                throw new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다");
            }

            // ! 객체 인스턴스에서 비밀번호 은닉
            sessionUser.setPassword(null);

            // 세션에 로그인 정보 저장
            session.setAttribute("sessionUser", sessionUser);
            log.info("로그인 성공 | 사용자명 : {}", sessionUser.getUsername());

            return "redirect:/";

        } catch (Exception e) {
            // 로그인 실패 시 에러메시지 반환
            model.addAttribute("errorMessage", e.getMessage());
            return "user/login-form";
        }
    }

    // C. 로그아웃
    // GET - http://localhost:8080/logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("==== 로그아웃 요청 ====");
        // 세션 무효화 처리
        session.invalidate();
        log.info("==== 로그아웃 완료 ====");
        return "redirect:/";
    }

    // D. 회원정보 수정
    // GET - http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {

        // 1. 인증 검사 - 로그인
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        User user = userPersistRepository.findById(sessionUser.getId());
        model.addAttribute("user", user);
        return "user/update-form";
    }

    @PostMapping("/user/update")
    public String update(UserRequest.UpdateDto updateDto, Model model, HttpSession session) {

        // 1. 인증 검사 - 로그인
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            // 2. 권한 검사
            User userEntity = userPersistRepository.findById(sessionUser.getId());
            if (userEntity == null) {
                throw new IllegalArgumentException("사용자 정보를 불러올 수 없습니다");
            }
            // 3. 유효성 검사
            updateDto.validate();

            // 4. 세션 동기화
            User updateUser = userPersistRepository.updatedById(sessionUser.getId(), updateDto);
            updateUser.setPassword(null);

            return "redirect:/";
        } catch (Exception e) {
            log.error("회원 정보 수정 실패 : {}", e.getMessage());
            model.addAttribute("user", userPersistRepository.findById(sessionUser.getId()));
            model.addAttribute("errorMessage", e.getMessage());
            return "user/update-form";
        }
    }
}