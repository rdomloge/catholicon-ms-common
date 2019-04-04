package com.domloge.catholicon.ms.common;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CacheControlFilter implements Filter {
	
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	
	@Value("${CACHE_TIME_SECONDS:3600}") // default to 1hr
	private int cacheTimeSeconds;

	private String MAX_AGE = "max-age=";
	
	private static final String HEADER_EXPIRES = "Expires";
	
	@PostConstruct
	public void setMaxAgeStringAfterCacheTimeSet() {
		MAX_AGE = "max-age="+cacheTimeSeconds;
	}
	
	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) sreq;
		HttpServletResponse response = (HttpServletResponse) sresp;
		
		response.setHeader(HEADER_CACHE_CONTROL, MAX_AGE);
		long expiresTimeMillis = System.currentTimeMillis() + (1000 * cacheTimeSeconds);
		response.setHeader(HEADER_EXPIRES, new Date(expiresTimeMillis).toGMTString());
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	public void setCacheTimeSeconds(int cacheTimeSeconds) {
		this.cacheTimeSeconds = cacheTimeSeconds;
	}

	@Override
	public void destroy() {
	}
}
