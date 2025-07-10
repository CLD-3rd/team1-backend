package basic.controller;

import basic.dto.MemberDto;
import basic.entity.Member;
import basic.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    // 회원가입 처리
    @GetMapping("/member/signup")
    public String signUp(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "createMemberDto";
    }

    @PostMapping("/member/signup")
    public String signUp(@ModelAttribute MemberDto memberDto, Model model) {
        try {
            memberService.join(memberDto);
            return "redirect:/";
        } catch (IllegalStateException e) {
            model.addAttribute("signupError", e.getMessage());
            return "index";
        }
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        return memberService.login(username, password, session, model);
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) {
            return "redirect:/";
        }
        model.addAttribute("member", member);
        return "home";
    }
}
