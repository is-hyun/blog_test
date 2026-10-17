package com.tenco.blog_test.board;

import com.tenco.blog_test.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardPersistRepository boardPersistRepository;

    // GET - http://localhost:8080/    ,    http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {

        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    // GET http://localhost:8080/board/3
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board boardEntity = boardPersistRepository.findById(id);

        if (boardEntity == null) {
            // 추후에 404 에러 페이지를 만들어서 처리할 예정
            throw new RuntimeException("게시글을 찾을 수 없습니다 : " + id);
        }
        model.addAttribute("board", boardEntity);
        return "board/detail";
    }

    // 게시글 작성
    // GET - http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveForm(HttpSession session) {
        // 1. 인증 검사 - 비로그인 사용자 접근 불가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        return "board/save-form";
    }

    // POST - http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDto saveDto, HttpSession session) {

        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // 2. 유효성 검사
        try {
            saveDto.validate();
            Board board = saveDto.toEntity(sessionUser);
            Board savedBoard = boardPersistRepository.save(board);
            return "redirect:/" + savedBoard.getId();
        } catch (Exception e) {
            log.error("게시글 등록 실패 : {}", e.getMessage());
            return "board/save-form";
        }
    }

    // 게시글 수정
    // GET http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        Board board = boardPersistRepository.findById(id);
        model.addAttribute("board", board);
        return "board/update-form";
    }

    // POST http://localhost:8080/board/1/update (게시글 수정 기능 요청)
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id,
                         BoardRequest.UpdateDto reqDto) {
        reqDto.validate();
        boardPersistRepository.updateById(id, reqDto);

        return "redirect:/board/" + id;
    }

    // 게시글 삭제
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes rttr) {

        // 1. 인증 검사 - 로그인
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            // 2. 삭제할 게시글 조회
            Board boardEntity = boardPersistRepository.findById(id);

            // 3. 권한 검사 - 사용자명 일치
            if (!boardEntity.isOwner(sessionUser.getId())) {
                throw new RuntimeException("삭제 권한이 없습니다");
            }
            boardPersistRepository.deleteById(id);
            return "redirect:/";
        } catch (Exception e) {
            log.error("게시글 삭제 실패 : {}", e.getMessage());
            // 리다이렉트에 의해 화면이 새로고침될 때
            // 사라지는 에러 메시지 내용을 유지하기 위해 세션에 1회성 저장
            // 화면 새로고침과 동시에 화면에 에러 메시지 출력 후 세션에서 데이터 삭제
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/" + id;
        }
    }

}