package kr.ac.dankook.VettAuthService.service;

import io.micrometer.context.ContextSnapshotFactory;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.entity.outbox.Outbox;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEvent;
import kr.ac.dankook.VettAuthService.entity.outbox.OutboxEventType;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxService outboxService;
    private final OutboxRepository outboxRepository;
    private final ContextSnapshotFactory snapshotFactory;

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
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Member member) {
        // Outbox 저장
        String userKey = EncryptionUtil.encrypt(member.getId());

        Outbox outbox = outboxService.makeMemberOutbox(OutboxEventType.USER_DELETED, userKey);
        outboxRepository.save(outbox);

        member.convertToDeletedMember();
        memberRepository.save(member);
        // Outbox 이벤트 발행
        eventPublisher.publishEvent(new OutboxEvent(outbox, snapshotFactory.captureAll()));
    }
}
