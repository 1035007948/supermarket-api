package com.supermarket.member.controller;

import com.supermarket.member.common.Result;
import com.supermarket.member.dto.PointsOperateDTO;
import com.supermarket.member.entity.PointsRecord;
import com.supermarket.member.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/records/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<List<PointsRecord>> getMemberRecords(@PathVariable Long memberId) {
        return Result.success(pointsService.getMemberPointsRecords(memberId));
    }

    @PostMapping("/operate")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PointsRecord> operatePoints(@Valid @RequestBody PointsOperateDTO operateDTO) {
        return Result.success(pointsService.operatePoints(operateDTO));
    }

    @PostMapping("/earn")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> earnPoints(@RequestParam Long memberId,
                                   @RequestParam Integer points,
                                   @RequestParam String source,
                                   @RequestParam(required = false) String orderNo,
                                   @RequestParam(required = false) String description) {
        pointsService.earnPoints(memberId, points, source, orderNo, description);
        return Result.success();
    }

    @PostMapping("/deduct")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deductPoints(@RequestParam Long memberId,
                                     @RequestParam Integer points,
                                     @RequestParam String source,
                                     @RequestParam(required = false) String orderNo,
                                     @RequestParam(required = false) String description) {
        pointsService.deductPoints(memberId, points, source, orderNo, description);
        return Result.success();
    }
}
