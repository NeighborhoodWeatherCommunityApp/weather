package org.pknu.weather.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pknu.weather.member.exp.ExpEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpEventRequestDto {

    @NotNull(message = "'expEvent' 필드는 필수입니다.")
    @JsonProperty("expEvent")
    ExpEvent expEvent;
}
