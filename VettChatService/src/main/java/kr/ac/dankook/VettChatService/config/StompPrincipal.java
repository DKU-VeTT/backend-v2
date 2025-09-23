package kr.ac.dankook.VettChatService.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

@AllArgsConstructor
@Getter
public class StompPrincipal implements Principal {

    private final String key;
    private final String role;

    @Override
    public String getName() {
        return this.key;
    }
}
