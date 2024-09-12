package furniture.shop.global;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberAuthorizationUtil {

    private final MemberRepository memberRepository;

    public static String getAuthenticationEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        return principal.getUsername();
    }

    public Member getMember() {
        String email = getAuthenticationEmail();

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        return member;
    }
}