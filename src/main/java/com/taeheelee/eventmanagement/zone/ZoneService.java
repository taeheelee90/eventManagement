package com.taeheelee.eventmanagement.zone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Zone;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

	private final ZoneRepository zoneRepository;
	

	/*public void initZoneData() throws IOException {
		if(zoneRepository.count() == 0) {
			Resource resource = new ClassPathResource("zone_kr.csv");
			List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath()).stream()
				.map(line -> {
					String[] split = line.split(",");
					return Zone.builder().city(split[0]).province(split[1]).build();
				}).collect(Collectors.toList());
			zoneRepository.saveAll(zoneList);
		}
	}*/
	
	@PostConstruct
	public void initZoneData() throws IOException {
	    if (zoneRepository.count() == 0) {
	        Resource resource = new ClassPathResource("zone_kr.csv");

	        InputStream zonesInputStream = resource.getInputStream();
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(zonesInputStream)) ) {
	            List<Zone> zoneList = reader.lines().map(line -> {
	                String[] split = line.split(",");
	                return Zone.builder().city(split[0]).province(split[1]).build();
	            }).collect(Collectors.toList());

	            zoneRepository.saveAll(zoneList);
	        }
	    }
	}
}
