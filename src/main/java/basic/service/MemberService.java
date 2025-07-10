package basic.service;

import basic.dto.MemberDto;
import basic.entity.Member;
import basic.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원가입
    @Transactional
    public Long join(MemberDto memberDto) {
        validateDuplicateMember(memberDto);
        Member member = Member.of(memberDto.getUsername(), memberDto.getPassword());
        memberRepository.save(member);
        return member.getId();
    }

    // 로그인
    public String login(String username, String password, HttpSession session, Model model) {

        Optional<Member> findMember = memberRepository.findByUsername(username);

        if (findMember.isEmpty()) {
            model.addAttribute("loginError", "존재하지 않는 사용자입니다.");
            return "index";
        }

        Member member = findMember.get();
        if (!member.getPassword().equals(password)) {
            model.addAttribute("loginError", "비밀번호가 일치하지 않습니다.");
            return "index";
        }

        session.setAttribute("loginMember", member);
        return "redirect:/home";

    }

    private void validateDuplicateMember(MemberDto memberDto) {
        memberRepository.findByUsername(memberDto.getUsername())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

}
