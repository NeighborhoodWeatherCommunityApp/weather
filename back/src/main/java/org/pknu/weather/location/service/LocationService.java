package org.pknu.weather.location.service;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.location.utils.AddressFinder;
import org.pknu.weather.location.utils.SgisLocationUtils;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.location.dto.LocationDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.pknu.weather.location.converter.LocationConverter.toLocation;
import static org.pknu.weather.location.converter.LocationConverter.toLocationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final SgisLocationUtils sgisLocationUtils;
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final AddressFinder addressFinder;

    public LocationDTO saveLocation(String email, double x, double y) {

        log.debug("LocationService - save Location with coordinate service start......");
        LocationDTO locationDTO= new LocationDTO();

        sgisLocationUtils.getAddressInfo(locationDTO, x, y);

        Optional<Location> locationByFullAddress = locationRepository.findLocationByFullAddress(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());

        Location savedLocation = locationByFullAddress.orElseGet(() -> locationRepository.save(toLocation(locationDTO)));

        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.changeLocation(savedLocation);
        memberRepository.save(member);

        log.debug("LocationService - saveLocation method end......");

        return toLocationDTO(savedLocation);
    }

    public LocationDTO saveLocation(LocationDTO locationDTO) {

        Optional<Location> locationByFullAddress = locationRepository.findLocationByFullAddress(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());
        Location savedLocation = null;

        if(locationByFullAddress.isEmpty()){
            sgisLocationUtils.getAddressInfo(locationDTO);
            savedLocation = locationByFullAddress.orElseGet(() -> locationRepository.save(toLocation(locationDTO)));
        }

        if(locationByFullAddress.isPresent())
            savedLocation = locationByFullAddress.get();

        return toLocationDTO(savedLocation);
    }

    public LocationDTO findDefaultLocation(String email) {

        log.debug("LocationService - findDefaultLocation  start......");

        Member member = memberRepository.findMemberByEmail(email).orElseThrow();

        Location location = member.getLocation();

        if (location == null) {
            throw new GeneralException(ErrorStatus._MEMBER_NOT_FOUND_LOCATION);
        }

        log.debug("LocationService - findDefaultLocation  method end......");

        return toLocationDTO(location);

    }

    public List<String> getLocation(String province, String city) {

        if (province == null || province.isEmpty())
            return addressFinder.getLocation();
        else if (city == null || city.isEmpty())
            return addressFinder.getLocation(province);
        else
            return addressFinder.getLocation(province, city);

    }

    /**
     * 해당 defaultLocation은 sgis로 검색이 되지 않은 지역들입니다.
     */
    public void setDefaultLocation() {
        List<LocationDTO> locations = new ArrayList<>();

        LocationDTO locationDTO1 = LocationDTO.builder()
                .longitude(127.185090374889)
                .latitude(37.549023322979)
                .province("경기도")
                .city("하남시")
                .street("미사3동")
                .build();
        LocationDTO locationDTO2 = LocationDTO.builder()
                .longitude(127.073699594)
                .latitude(37.4921731813)
                .province("서울특별시")
                .city("강남구")
                .street("개포3동")
                .build();
        LocationDTO locationDTO3 = LocationDTO.builder()
                .longitude(126.768719735373)
                .latitude(37.7189863489401)
                .province("경기도")
                .city("파주시")
                .street("운정4동")
                .build();
        LocationDTO locationDTO4 = LocationDTO.builder()
                .longitude(126.710801780994)
                .latitude(37.7202712243604)
                .province("경기도")
                .city("파주시")
                .street("운정5동")
                .build();
        LocationDTO locationDTO5 = LocationDTO.builder()
                .longitude(126.720412067404)
                .latitude(37.7127442760022)
                .province("경기도")
                .city("파주시")
                .street("운정6동")
                .build();
        LocationDTO locationDTO6 = LocationDTO.builder()
                .longitude(127.138381834449)
                .latitude(37.1806505336027)
                .province("경기도")
                .city("화성시")
                .street("동탄9동")
                .build();
        LocationDTO locationDTO7 = LocationDTO.builder()
                .longitude(127.228738715704)
                .latitude(37.3660957187075)
                .province("경기도")
                .city("광주시")
                .street("오포1동")
                .build();
        LocationDTO locationDTO8 = LocationDTO.builder()
                .longitude(127.254597637005)
                .latitude(37.3776841055727)
                .province("경기도")
                .city("광주시")
                .street("오포2동")
                .build();

        locations.add(locationDTO1);
        locations.add(locationDTO2);
        locations.add(locationDTO3);
        locations.add(locationDTO4);
        locations.add(locationDTO5);
        locations.add(locationDTO6);
        locations.add(locationDTO7);
        locations.add(locationDTO8);

        for ( LocationDTO location:locations ) {
            locationRepository.save(toLocation(location));
        }

    }
}
