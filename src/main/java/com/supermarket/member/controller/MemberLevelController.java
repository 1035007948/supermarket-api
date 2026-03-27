package com.supermarket.member.controller;

import com.supermarket.member.common.Result;
import com.supermarket.member.entity.MemberLevel;
import com.supermarket.member.service.MemberLevelService;
import com.supermarket.member.vo.MemberLevelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/member-level")
public class MemberLevelController {

    @Autowired
    private MemberLevelService memberLevelService;

    @GetMapping
    public Result<List<MemberLevelVO>> list() {
        List<MemberLevel> levels = memberLevelService.listActiveLevels();
        return Result.success(levels.stream().map(this::convertToVO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<MemberLevelVO> getById(@PathVariable Long id) {
        MemberLevel level = memberLevelService.getById(id);
        if (level == null) {
            return Result.error("会员等级不存在");
        }
        return Result.success(convertToVO(level));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MemberLevelVO> create(@RequestBody MemberLevel memberLevel) {
        memberLevelService.save(memberLevel);
        return Result.success(convertToVO(memberLevel));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MemberLevelVO> update(@PathVariable Long id, @RequestBody MemberLevel memberLevel) {
        memberLevel.setId(id);
        memberLevelService.updateById(memberLevel);
        return Result.success(convertToVO(memberLevelService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        memberLevelService.removeById(id);
        return Result.success();
    }

    private MemberLevelVO convertToVO(MemberLevel level) {
        MemberLevelVO vo = new MemberLevelVO();
        BeanUtils.copyProperties(level, vo);
        return vo;
    }
}
