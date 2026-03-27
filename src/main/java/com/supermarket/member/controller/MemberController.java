package com.supermarket.member.controller;

import com.supermarket.member.dto.ApiResponse;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberLevel;
import com.supermarket.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers() {
        List<Member> members = memberService.findAll();
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable Long id) {
        return memberService.findById(id)
                .map(member -> ResponseEntity.ok(ApiResponse.success(member)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/memberNo/{memberNo}")
    public ResponseEntity<ApiResponse<Member>> getMemberByMemberNo(@PathVariable String memberNo) {
        return memberService.findByMemberNo(memberNo)
                .map(member -> ResponseEntity.ok(ApiResponse.success(member)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<Member>>> getMembersByLevel(@PathVariable String level) {
        try {
            MemberLevel memberLevel = MemberLevel.valueOf(level.toUpperCase());
            List<Member> members = memberService.findByLevel(memberLevel);
            return ResponseEntity.ok(ApiResponse.success(members));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("无效的会员等级"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.findById(id)
                .map(existingMember -> {
                    member.setId(id);
                    Member updatedMember = memberService.updateMember(member);
                    return ResponseEntity.ok(ApiResponse.success("更新成功", updatedMember));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        if (memberService.findById(id).isPresent()) {
            memberService.deleteMember(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        }
        return ResponseEntity.notFound().build();
    }
}
