package de.mpg.mpdl.r2d2.exceptions;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class RestErrorMessage extends DefaultErrorAttributes {
	
	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		final Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
		final Map<String, Object> restErrorAttributes = new LinkedHashMap<>();
		restErrorAttributes.put("status", errorAttributes.getOrDefault("status", "500"));
		restErrorAttributes.put("error", errorAttributes.getOrDefault("error", "n/a"));
		restErrorAttributes.put("message", errorAttributes.getOrDefault("message", "n/a"));
		restErrorAttributes.put("path", errorAttributes.getOrDefault("path", "n/a"));
		restErrorAttributes.put("time", errorAttributes.getOrDefault("timestamp", "n/a"));

		return Map.of("errors", new Map[] {restErrorAttributes});
	}

}
