package com.taeheelee.eventmanagement.settings.form;

import com.taeheelee.eventmanagement.domain.Zone;

import lombok.Data;

@Data
public class ZoneForm {
	
    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("/"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

  
    public Zone getZone() {
        return Zone.builder()
        		.city(this.getCityName())               
                .province(this.getProvinceName())
                .build();
    }
}
