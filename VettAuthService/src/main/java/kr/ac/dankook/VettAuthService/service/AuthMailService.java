package kr.ac.dankook.VettAuthService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.ac.dankook.VettAuthService.dto.request.MailRequest;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthMailService {

    @Value("{spring.mail.username}")
    private String adminMailAddress;
    private final JavaMailSender mailSender;


    @Async
    public void sendMail(MailRequest mailRequest){

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");

            helper.setTo(mailRequest.getEmail());
            helper.setSubject(mailRequest.getTitle());
            helper.setText(mailRequest.getContent(),true);
            helper.setFrom(adminMailAddress);
            helper.setReplyTo(adminMailAddress);

            mailSender.send(mimeMessage);
            log.info("[{}, class={}, method={}, mailTo={}]",
                    LogMessage.SUCCESS_SEND_MAIL, className, methodName,  mailRequest.getEmail());

        }catch (MessagingException e){
            throw new CustomException(ErrorCode.CERTIFICATE_SEND_MAIL_ERROR,className,methodName,e.getMessage());
        }
    }

}
