package com.supermarket.api.service;

import com.supermarket.api.dto.*;
import com.supermarket.api.entity.Member;
import com.supermarket.api.entity.Transaction;
import com.supermarket.api.repository.MemberRepository;
import com.supermarket.api.repository.TransactionRepository;
import com.supermarket.api.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 会员服务类
 */
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 生成唯一会员卡号
     */
    private String generateCardNumber() {
        String prefix = "VIP";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return prefix + timestamp + random;
    }

    /**
     * 生成交易单号
     */
    private String generateTransactionNo() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 会员注册
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Member> register(MemberRegisterDTO registerDTO) {
        // 检查手机号是否已注册
        if (memberRepository.existsByPhone(registerDTO.getPhone())) {
            return ApiResponse.error("该手机号已注册");
        }

        // 创建会员
        Member member = new Member();
        member.setName(registerDTO.getName());
        member.setPhone(registerDTO.getPhone());
        member.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        member.setCardNumber(generateCardNumber());
        member.setUpdateTime(LocalDateTime.now());

        memberRepository.save(member);

        return ApiResponse.success("注册成功", member);
    }

    /**
     * 会员登录
     */
    public ApiResponse<Map<String, Object>> login(MemberLoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getPhone(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getPhone());

            // 获取会员信息
            Member member = memberRepository.findByPhone(loginDTO.getPhone()).orElse(null);

            // 生成token，携带会员信息
            Map<String, Object> claims = new HashMap<>();
            if (member != null) {
                claims.put("memberId", member.getId());
                claims.put("name", member.getName());
                claims.put("level", member.getLevel().name());
            }

            String token = jwtTokenUtil.generateToken(userDetails, claims);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("member", member);

            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            return ApiResponse.error(401, "手机号或密码错误");
        }
    }

    /**
     * 根据ID查询会员
     */
    public ApiResponse<Member> getMemberById(Long id) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        return memberOpt.map(member -> ApiResponse.success(member))
                .orElseGet(() -> ApiResponse.error(404, "会员不存在"));
    }

    /**
     * 根据手机号查询会员
     */
    public ApiResponse<Member> getMemberByPhone(String phone) {
        Optional<Member> memberOpt = memberRepository.findByPhone(phone);
        return memberOpt.map(member -> ApiResponse.success(member))
                .orElseGet(() -> ApiResponse.error(404, "会员不存在"));
    }

    /**
     * 分页查询会员列表
     */
    public ApiResponse<Page<Member>> getMemberList(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return ApiResponse.success(memberPage);
    }

    /**
     * 更新会员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Member> updateMember(Long id, Member member) {
        Optional<Member> existingMemberOpt = memberRepository.findById(id);
        if (!existingMemberOpt.isPresent()) {
            return ApiResponse.error(404, "会员不存在");
        }

        Member existingMember = existingMemberOpt.get();
        existingMember.setName(member.getName());
        existingMember.setPhone(member.getPhone());
        existingMember.setStatus(member.getStatus());
        existingMember.setUpdateTime(LocalDateTime.now());

        memberRepository.save(existingMember);
        return ApiResponse.success("更新成功", existingMember);
    }

    /**
     * 删除会员
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            return ApiResponse.error(404, "会员不存在");
        }
        memberRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 储值充值
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Transaction> recharge(RechargeDTO rechargeDTO) {
        Optional<Member> memberOpt = memberRepository.findById(rechargeDTO.getMemberId());
        if (!memberOpt.isPresent()) {
            return ApiResponse.error(404, "会员不存在");
        }

        Member member = memberOpt.get();
        double amount = rechargeDTO.getAmount().doubleValue();

        // 更新会员储值余额
        member.recharge(amount);
        member.setUpdateTime(LocalDateTime.now());
        memberRepository.save(member);

        // 创建交易记录
        Transaction transaction = new Transaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setMemberId(member.getId());
        transaction.setType(Transaction.TransactionType.RECHARGE.name());
        transaction.setAmount(amount);
        transaction.setBalanceChange(amount);
        transaction.setPointsAfter(member.getPoints());
        transaction.setBalanceAfter(member.getStoredBalance());
        transaction.setRemark(rechargeDTO.getRemark());

        transactionRepository.save(transaction);

        return ApiResponse.success("充值成功", transaction);
    }

    /**
     * 消费结算
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Map<String, Object>> consumption(ConsumptionDTO consumptionDTO) {
        Optional<Member> memberOpt = memberRepository.findById(consumptionDTO.getMemberId());
        if (!memberOpt.isPresent()) {
            return ApiResponse.error(404, "会员不存在");
        }

        Member member = memberOpt.get();
        double originalAmount = consumptionDTO.getAmount().doubleValue();
        Map<String, Object> result = new HashMap<>();

        // 1. 计算会员折扣
        double discountedAmount = member.calculateDiscountedAmount(originalAmount);
        result.put("originalAmount", originalAmount);
        result.put("discountedAmount", discountedAmount);
        result.put("discountAmount", originalAmount - discountedAmount);
        result.put("discountRate", member.getLevel().getDiscountRate());

        // 2. 处理积分抵扣
        int usePoints = consumptionDTO.getUsePoints();
        double pointsDeductionAmount = 0.0;
        if (usePoints > 0) {
            // 假设100积分抵扣1元
            pointsDeductionAmount = usePoints / 100.0;
            if (pointsDeductionAmount > discountedAmount) {
                pointsDeductionAmount = discountedAmount;
                usePoints = (int) (discountedAmount * 100);
            }

            if (!member.usePoints(usePoints)) {
                return ApiResponse.error("积分不足");
            }
            discountedAmount -= pointsDeductionAmount;
        }
        result.put("usePoints", usePoints);
        result.put("pointsDeductionAmount", pointsDeductionAmount);
        result.put("finalAmount", discountedAmount);

        // 3. 处理支付（这里简化处理，只记录交易）
        double paymentAmount = discountedAmount;
        if (consumptionDTO.getUseStoredBalance()) {
            if (!member.useStoredBalance(paymentAmount)) {
                return ApiResponse.error("储值余额不足");
            }
        } else {
            // 现金支付，只累计消费金额
            member.setTotalConsumption(member.getTotalConsumption() + paymentAmount);
        }

        // 4. 计算并赠送积分
        int earnPoints = member.calculatePoints(paymentAmount);
        member.addPoints(earnPoints);
        result.put("earnPoints", earnPoints);
        result.put("currentPoints", member.getPoints());
        result.put("currentBalance", member.getStoredBalance());

        member.setUpdateTime(LocalDateTime.now());
        memberRepository.save(member);

        // 创建消费交易记录
        Transaction transaction = new Transaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setMemberId(member.getId());
        transaction.setType(Transaction.TransactionType.CONSUMPTION.name());
        transaction.setAmount(originalAmount);
        transaction.setPointsChange(earnPoints - usePoints);
        transaction.setBalanceChange(consumptionDTO.getUseStoredBalance() ? -paymentAmount : 0.0);
        transaction.setPointsAfter(member.getPoints());
        transaction.setBalanceAfter(member.getStoredBalance());
        transaction.setRemark(consumptionDTO.getRemark() + " 折扣优惠:" + (originalAmount - paymentAmount - pointsDeductionAmount) + "元");

        transactionRepository.save(transaction);

        return ApiResponse.success("消费结算成功", result);
    }

    /**
     * 查询会员交易记录（分页）
     */
    public ApiResponse<Page<Transaction>> getMemberTransactions(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            return ApiResponse.error(404, "会员不存在");
        }
        Page<Transaction> transactionPage = transactionRepository.findByMemberIdOrderByCreateTimeDesc(memberId, pageable);
        return ApiResponse.success(transactionPage);
    }

    /**
     * 获取会员统计信息
     */
    public ApiResponse<Map<String, Object>> getMemberStats(Long memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            return ApiResponse.error(404, "会员不存在");
        }

        Member member = memberOpt.get();
        Map<String, Object> stats = new HashMap<>();
        stats.put("memberId", member.getId());
        stats.put("name", member.getName());
        stats.put("level", member.getLevel().name());
        stats.put("levelDescription", member.getLevel().getDescription());
        stats.put("points", member.getPoints());
        stats.put("storedBalance", member.getStoredBalance());
        stats.put("totalConsumption", member.getTotalConsumption());
        stats.put("discountRate", member.getLevel().getDiscountRate());
        stats.put("pointMultiplier", member.getLevel().getPointMultiplier());

        // 计算距离下一级所需累计充值金额
        double nextLevelRecharge = 0.0;
        String nextLevelName = "";
        switch (member.getLevel()) {
            case REGULAR:
                nextLevelRecharge = 1000 - member.getTotalRecharge();
                nextLevelName = "银卡会员";
                break;
            case SILVER:
                nextLevelRecharge = 5000 - member.getTotalRecharge();
                nextLevelName = "金卡会员";
                break;
            case GOLD:
                nextLevelRecharge = 0.0;
                nextLevelName = "已达最高等级";
                break;
        }
        stats.put("totalRecharge", member.getTotalRecharge());
        stats.put("nextLevelName", nextLevelName);
        stats.put("nextLevelNeedRecharge", Math.max(0.0, nextLevelRecharge));

        return ApiResponse.success(stats);
    }
}
