package kr.hhplus.be.server.support.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		String uuid = UUID.randomUUID().toString();
		MDC.put("requestID", uuid);

		long startTime = System.currentTimeMillis();

		try {
			filterChain.doFilter(requestWrapper, responseWrapper);
		} finally {
			long duration = System.currentTimeMillis() - startTime;

			logRequest(requestWrapper);
			logResponse(responseWrapper, duration);
			responseWrapper.copyBodyToResponse();
			MDC.clear();
		}
	}

	private void logRequest(ContentCachingRequestWrapper request) {
		String method = request.getMethod();
		String uri = request.getRequestURI();

		if ("GET".equalsIgnoreCase(method)) {
			log.info("[REQUEST] {} {} params={}", method, uri, extractQueryParams(request));
		} else {
			String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
			log.info("[REQUEST] {} {} body={}", method, uri, requestBody);
		}
	}

	private void logResponse(ContentCachingResponseWrapper response, long duration) {
		String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
		log.info("[RESPONSE] status={} duration={}ms body={}", response.getStatus(), duration, responseBody);
	}

	private String extractQueryParams(HttpServletRequest request) {
		return request.getParameterMap().entrySet().stream()
			.map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
			.collect(Collectors.joining(", ", "{", "}"));
	}
}
