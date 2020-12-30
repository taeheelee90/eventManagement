package com.taeheelee.eventmanagement.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("eventmanagementapplication")
public class AppProperties {
	
	private String host;

}
