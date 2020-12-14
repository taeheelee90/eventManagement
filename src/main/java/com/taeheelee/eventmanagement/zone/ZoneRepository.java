package com.taeheelee.eventmanagement.zone;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taeheelee.eventmanagement.domain.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

	Zone findByCityAndProvince(String cityName, String provinceName);

}
