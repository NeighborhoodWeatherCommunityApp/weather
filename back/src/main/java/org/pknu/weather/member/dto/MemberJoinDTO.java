package org.pknu.weather.member.dto;

import lombok.*;
import org.pknu.weather.domain.common.Sensitivity;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinDTO {

    private Sensitivity sensitivity;

    private String nickname;

    private MultipartFile profileImg;

    private String imgName;

    private String imgPath;
}
