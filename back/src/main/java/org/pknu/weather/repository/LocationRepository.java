package org.pknu.weather.repository;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.pknu.weather.domain.Location;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    default Location safeFindById(Long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._LOCATION_NOT_FOUND));
    }

    @Query("select loc from Location loc where loc.province = :province and loc.city = :city and loc.street = :street")
    Optional<Location> findLocationByFullAddress(String province, String city, String street);
}
