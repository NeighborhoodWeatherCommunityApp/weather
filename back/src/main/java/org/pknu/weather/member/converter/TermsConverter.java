package org.pknu.weather.member.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.entity.MemberTerms;
import org.pknu.weather.member.entity.Terms;
import org.pknu.weather.member.dto.TermsDto;

import java.util.ArrayList;
import java.util.List;

public class TermsConverter {

    // List<Terms>와 Member 객체를 받아서, 각 약관에 대한 동의 상태를 MemberTerms로 변환
    public static List<MemberTerms> toMemberTermsList(Member member, TermsDto agreed, List<Terms> termsList) {
        List<MemberTerms> memberTermsList = new ArrayList<>();

        // 약관 목록을 순회하며 MemberTerms 객체를 생성
        for (Terms terms : termsList) {
            // 약관에 대한 동의 여부를 조건에 맞게 설정
            Boolean isAgreed = getAgreementStatus(terms, agreed);

            // MemberTerms 객체 생성 후 리스트에 추가
            memberTermsList.add(toMemberTerms(member, terms, isAgreed));
        }

        return memberTermsList;
    }

    // 약관에 대한 동의 여부를 반환하는 로직 (조건에 맞게 동의 상태를 결정)
    private static Boolean getAgreementStatus(Terms terms, TermsDto agreed) {
        // 예시: TermsDto.Agreed가 각 약관에 대한 동의 여부를 나타내는 필드일 경우
        switch (terms.getTermsType()) {
            case SERVICE_TERMS:
                return agreed.getIsServiceTermsAgreed();
            case PRIVACY_POLICY:
                return agreed.getIsPrivacyPolicyAgreed();
            case LOCATION_SERVICE_TERMS:
                return agreed.getIsLocationServiceTermsAgreed();
            case PUSH_NOTIFICATION:
                return agreed.getIsPushNotificationAgreed();
            default:
                return false;  // 기본값 false
        }
    }

    // MemberTerms 객체 생성
    private static MemberTerms toMemberTerms(Member member, Terms terms, Boolean agreed) {
        return MemberTerms.builder()
                .member(member)
                .terms(terms)
                .agreed(agreed)
                .build();
    }
}
