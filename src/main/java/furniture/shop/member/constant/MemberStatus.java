package furniture.shop.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성화"),
    STOP("중지"),
    BAN("정지");

    @Getter
    private final String korean;
}
