package de.mpg.mpdl.r2d2.aa;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ExceptionFilter extends OncePerRequestFilter {
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch(Exception ex) {
			Map<String, Object> errors = new LinkedHashMap<>();
		    errors.put("time", LocalDateTime.now());
		    errors.put("cause", ex.getClass().getSimpleName());
		    errors.put("message", ex.getMessage());
		    errors.put("uri", request.getRequestURL());
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	        response.setContentType("application/json");
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.getWriter().append(mapper.writeValueAsString(errors));
		}

	}

}
