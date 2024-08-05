package furniture.shop.member;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.embed.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long joinMember(MemberJoinDto dto) {

        Member findMember = memberRepository.findByEmail(dto.getEmail());

        if (findMember != null) {
            throw new CustomException(CustomExceptionCode.JOIN_DUPLICATE_EXCEPTION);
        }

        Member member = Member.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(new Address(dto.getZipCode(), dto.getCity(), dto.getStreet()))
                .gender(dto.getMemberGender())
                .build();

        return memberRepository.save(member).getId();
    }
}
