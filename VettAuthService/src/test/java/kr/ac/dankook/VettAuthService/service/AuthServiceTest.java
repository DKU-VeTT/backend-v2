package kr.ac.dankook.VettAuthService.service;

import kr.ac.dankook.VettAuthService.dto.request.SignupRequest;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<Member> memberCaptor;

    @Test
    @DisplayName("회원가입 성공")
    void signupProcess(){
        SignupRequest request = new SignupRequest("테스트유저",
                "test@naver.com","test1234","testPassword123!");

        given(memberRepository.existsByUserId(request.getUserId())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        authService.signup(request);
        verify(memberRepository).save(memberCaptor.capture());

        Member saved = memberCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo("test@naver.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getName()).isEqualTo("테스트유저");
        assertThat(saved.getUserId()).isEqualTo("test1234");
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    void duplicatedIdSignupProcess(){
        SignupRequest request = new SignupRequest("테스트유저",
                "test@naver.com","test1234","testPassword123!");
        given(memberRepository.existsByUserId(request.getUserId())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_ID);
    }

}
