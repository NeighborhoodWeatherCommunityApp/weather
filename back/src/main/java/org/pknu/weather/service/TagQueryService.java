package org.pknu.weather.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.common.utils.TagUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.dto.TagSelectedOrNotDto;
import org.pknu.weather.dto.TotalWeatherDto;
import org.pknu.weather.dto.WeatherResponse.ExtraWeatherInfo;
import org.pknu.weather.dto.converter.TagResponseConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.TagRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagQueryService {
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final WeatherRepository weatherRepository;
    private final WeatherService weatherService;
    private final EnumTagMapper enumTagMapper;

    /**
     * 간단 날씨 보기 기능에서 태그 정보를 불러온다.
     *
     * @param email
     * @return
     */
    public List<TagDto.SimpleTag> getMostSelectedTags(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();

        List<TagQueryResult> tagQueryResultList = tagRepository.rankingTags(location);
        List<EnumTag> tempAndHumidList = new ArrayList<>();
        List<String> result = new ArrayList<>();

        for (int i = 0; i < tagQueryResultList.size(); i++) {
            EnumTag tag = tagQueryResultList.get(i).getTag();
            if (TagUtils.isTempTagOrHumdiTag(tag)) {
                tempAndHumidList.add(tag);
            } else if (TagUtils.isDustTag(tag)) {
                result.add("미세먼지 " + tag.toText());
            } else {
                result.add(tag.toText());
            }
        }

        String text = TagUtils.temperatureAndHumidityTag2TemperatureTag(tempAndHumidList);
        result.add(0, text);
        result.remove(result.size() - 1);

        return result.stream()
                .map(TagResponseConverter::toSimpleTag)
                .toList();

    }

    public Map<String, List<TagSelectedOrNotDto>> getSelectedOrNotTags(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();
        Weather weather = weatherRepository.findByLocationClosePresentationTime(location);
        ExtraWeatherInfo extraWeatherInfo = weatherService.extraWeatherInfo(member.getEmail(), location.getId());
        TotalWeatherDto totalWeatherDto = new TotalWeatherDto(weather, extraWeatherInfo);
        Map<String, List<TagSelectedOrNotDto>> map = new HashMap<>();

        enumTagMapper.getAll().forEach((key, enumTag) -> {
            TagSelectedOrNotDto tagSelectedOrNotDto = TagResponseConverter.toTagSelectedOrNotDto(enumTag,
                    totalWeatherDto);

            String tagName = enumTag.getTagName();

            if (!map.containsKey(tagName)) {
                map.put(tagName, new ArrayList<>());
            }

            map.get(tagName).add(tagSelectedOrNotDto);
        });

        map.forEach((s, dtoList) -> {
            dtoList.sort(Comparator.comparingInt(TagSelectedOrNotDto::getCode));
        });

        return map;
    }
}
