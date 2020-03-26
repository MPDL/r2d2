package de.mpg.mpdl.r2d2.aa;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * This filter checks and validates the JWT token, if sent in an header
 * 
 * @author haarlae1
 *
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

  public JWTAuthenticationFilter(AuthenticationManager authManager) {
    super(authManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(JWTLoginFilter.HEADER_STRING);

    if (header == null || !header.startsWith(JWTLoginFilter.TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(JWTLoginFilter.HEADER_STRING);
    if (token != null) {
      // parse the token.
      String user = JWT.require(Algorithm.HMAC512(JWTLoginFilter.SECRET.getBytes())).build()
          .verify(token.replace(JWTLoginFilter.TOKEN_PREFIX, "")).getSubject();

      if (user != null) {
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
      }
      return null;
    }
    return null;
  }
}
