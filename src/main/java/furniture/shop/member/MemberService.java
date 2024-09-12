package furniture.shop.member;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberInfoDto;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.dto.MemberUpdateDto;
import furniture.shop.global.embed.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberAuthorizationUtil memberAuthorizationUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long joinMember(MemberJoinDto dto) {
        Member findMember = memberRepository.findByEmail(dto.getEmail());

        if (findMember != null) {
            throw new CustomException(CustomExceptionCode.JOIN_DUPLICATE_EXCEPTION);
        }

        Member member = Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(new Address(dto.getZipCode(), dto.getCity(), dto.getStreet()))
                .gender(MemberGender.valueOf(dto.getMemberGender()))
                .build();

        return memberRepository.save(member).getId();
    }

    @Transactional
    public MemberInfoDto getMemberInfo() {
        Member member = memberAuthorizationUtil.getMember();

        MemberInfoDto memberInfoDto = entityToDto(member);

        return memberInfoDto;
    }

    @Transactional
    public MemberInfoDto updateMember(MemberUpdateDto memberUpdateDto) {
        Member member = memberAuthorizationUtil.getMember();

        if (StringUtils.hasText(memberUpdateDto.getPassword())) {
            member.changePassword(passwordEncoder.encode(memberUpdateDto.getPassword()));
        }

        if (StringUtils.hasText(memberUpdateDto.getZipCode())) {
            if (StringUtils.hasText(memberUpdateDto.getCity())) {
                if (StringUtils.hasText(memberUpdateDto.getStreet())) {
                    Address address = new Address(memberUpdateDto.getZipCode(), memberUpdateDto.getCity(), memberUpdateDto.getStreet());

                    member.changeAddress(address);
                }
            }
        }

        MemberInfoDto memberInfoDto = entityToDto(member);

        return memberInfoDto;
    }

    private MemberInfoDto entityToDto(Member member) {
        MemberInfoDto memberInfoDto = new MemberInfoDto();

        memberInfoDto.setUsername(member.getUsername());
        memberInfoDto.setPhone(member.getPhone());
        memberInfoDto.setEmail(member.getEmail());
        memberInfoDto.setZipCode(member.getAddress().getZipCode());
        memberInfoDto.setCity(member.getAddress().getCity());
        memberInfoDto.setStreet(member.getAddress().getStreet());
        memberInfoDto.setMemberGender(member.getGender());
        memberInfoDto.setMileage(member.getMileage());
        memberInfoDto.setRegisterDate(member.getRegisterDate());
        memberInfoDto.setUpdateDate(member.getUpdateDate());

        return memberInfoDto;
    }
}
