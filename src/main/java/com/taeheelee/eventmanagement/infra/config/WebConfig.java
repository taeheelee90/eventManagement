package com.taeheelee.eventmanagement.infra.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.taeheelee.eventmanagement.modules.notification.NotificationInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
	
	private final NotificationInterceptor notificationInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> staticResoucrcePath = Arrays.stream(StaticResourceLocation.values())
											.flatMap(StaticResourceLocation::getPatterns)
											.collect(Collectors.toList());
		staticResoucrcePath.add("/node_modules/**");
		
		registry.addInterceptor(notificationInterceptor).excludePathPatterns(staticResoucrcePath);
	}

}
