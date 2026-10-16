package com.tenco.blog_test.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URL;
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

            User existingUser = userPersistRepository.findByUsername(joinDto.getUsername());
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
    }


    // GET - http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model) {

        // 뼈대용 임시 데이터
        model.addAttribute("user",
                Map.of("username", "김민수", "email", "abc@naver.com"));
        return "user/update-form";
    }

    // GET - http://localhost:8080/logout
    @GetMapping("/logout")
    public String logout() {
        // templates/   <<-- 콘텐츠 루트 경로
        return "redirect/";
    }
}