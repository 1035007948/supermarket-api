package com.supermarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.PageResult;
import com.supermarket.common.Result;
import com.supermarket.dto.MemberDTO;
import com.supermarket.dto.PointsOperateDTO;
import com.supermarket.dto.RechargeDTO;
import com.supermarket.service.MemberService;
import com.supermarket.vo.BalanceRecordVO;
import com.supermarket.vo.MemberVO;
import com.supermarket.vo.PointsRecordVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public Result<MemberVO> create(@RequestBody @Valid MemberDTO dto) {
        return Result.success(memberService.createMember(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid MemberDTO dto) {
        dto.setId(id);
        memberService.updateMember(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MemberVO> getById(@PathVariable Long id) {
        return Result.success(memberService.getMemberById(id));
    }

    @GetMapping("/phone/{phone}")
    public Result<MemberVO> getByPhone(@PathVariable String phone) {
        return Result.success(memberService.getMemberByPhone(phone));
    }

    @GetMapping("/page")
    public Result<PageResult<MemberVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String level) {
        Page<MemberVO> result = memberService.getMemberPage(page, size, name, phone, level);
        return Result.success(PageResult.of(result.getTotal(), (long) page, (long) size, result.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<MemberVO>> list() {
        return Result.success(memberService.getMemberList());
    }

    @PostMapping("/recharge")
    public Result<Void> recharge(@RequestBody @Valid RechargeDTO dto) {
        memberService.rechargeBalance(dto.getMemberId(), dto.getAmount(), "MANUAL", null, "后台充值");
        return Result.success();
    }

    @PostMapping("/points/add")
    public Result<Void> addPoints(@RequestBody @Valid PointsOperateDTO dto) {
        memberService.addPoints(dto.getMemberId(), dto.getPoints(), "MANUAL", null, dto.getRemark());
        return Result.success();
    }

    @PostMapping("/points/deduct")
    public Result<Void> deductPoints(@RequestBody @Valid PointsOperateDTO dto) {
        memberService.deductPoints(dto.getMemberId(), dto.getPoints(), "MANUAL", null, dto.getRemark());
        return Result.success();
    }

    @GetMapping("/{memberId}/points-records")
    public Result<PageResult<PointsRecordVO>> getPointsRecords(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PointsRecordVO> result = memberService.getPointsRecordPage(memberId, page, size);
        return Result.success(PageResult.of(result.getTotal(), (long) page, (long) size, result.getRecords()));
    }

    @GetMapping("/{memberId}/balance-records")
    public Result<PageResult<BalanceRecordVO>> getBalanceRecords(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<BalanceRecordVO> result = memberService.getBalanceRecordPage(memberId, page, size);
        return Result.success(PageResult.of(result.getTotal(), (long) page, (long) size, result.getRecords()));
    }
}
