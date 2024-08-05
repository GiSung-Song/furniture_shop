package furniture.shop.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberGender {
    FEMALE("여자"),
    MALE("남자");

    @Getter
    private final String korean;
}