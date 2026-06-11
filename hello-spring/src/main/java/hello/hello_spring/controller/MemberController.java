package hello.hello_spring.controller;

import hello.hello_spring.domain.Member;
import hello.hello_spring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }  // <- 생성자 주입 (보통 많이 함)

    // @Autowired
    private final MemberService memberService; // <- 필드 주입 (중간에 바꿀 수 있는 방법이 없음 [별로임])

//    @Autowired
//    public void setMemberService(MemberService memberService) {
//        this.memberService = memberService;
//    } // setter 주입 (public으로 노출됨 호출되지 않아야 할 메서드가 노출이 됌 다른 개발자가 수정 가능성이 올라감)

    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
