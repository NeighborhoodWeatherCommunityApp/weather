package org.pknu.weather.location.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<Location, Long>, LocationCustomRepository {

    default Location safeFindById(Long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._LOCATION_NOT_FOUND));
    }

    @Query(
            value = "SELECT * FROM location WHERE province = :province AND city = :city AND street = :street LIMIT 1",
            nativeQuery = true
    )
    Optional<Location> findLocationByFullAddress(String province, String city, String street);

    List<Location> findByIdIn(Collection<Long> ids);

}
