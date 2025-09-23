package kr.ac.dankook.VettAuthService.service;

import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.entity.Outbox;
import kr.ac.dankook.VettAuthService.entity.OutboxEvent;
import kr.ac.dankook.VettAuthService.entity.OutboxEventType;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberOutboxService memberOutboxService;
    private final OutboxRepository outboxRepository;

    public Member getCurrentMember(String userId){
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void updatePassword(Member member, String newPassword){
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Transactional
    public boolean editMemberPassword(Member member, String originalPassword, String newPassword){
        if (!passwordEncoder.matches(originalPassword, member.getPassword())) return false;
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        member.updatePassword(encodedNewPassword);
        memberRepository.save(member);
        return true;
    }

    @Transactional
    public void editMemberInfo(Member member,String name,String email){
        member.updateMemberInfo(name,email);
        Outbox outbox = memberOutboxService.makeOutbox(member, OutboxEventType.USER_MODIFIED);
        outboxRepository.save(outbox);
        memberRepository.save(member);
        eventPublisher.publishEvent(new OutboxEvent(outbox));
    }

    @Transactional
    public void deleteMember(Member member) {
        Outbox outbox = memberOutboxService.makeOutbox(member, OutboxEventType.USER_DELETED);
        outboxRepository.save(outbox);
        member.convertToDeletedMember();
        memberRepository.save(member);
        eventPublisher.publishEvent(new OutboxEvent(outbox));
    }
}
