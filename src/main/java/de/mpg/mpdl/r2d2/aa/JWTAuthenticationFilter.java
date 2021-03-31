package de.mpg.mpdl.r2d2.aa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

/**
 * This filter checks and validates the JWT token, if sent in an header
 * 
 * @author haarlae1
 *
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

  private static Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

  private UserAccountRepository userAccountRepository;

  public JWTAuthenticationFilter(AuthenticationManager authManager, UserAccountRepository uar) {
    super(authManager);
    this.userAccountRepository = uar;
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

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws ServletException {
    String token = request.getHeader(JWTLoginFilter.HEADER_STRING);
    String reviewToken = request.getParameter(JWTLoginFilter.REVIEW_TOKEN_PARAM_NAME);

    R2D2Principal principal = null;

    if (token != null) {
      // parse the token.
      String userId = JWT.require(Algorithm.HMAC512(JWTLoginFilter.SECRET.getBytes())).build()
          .verify(token.replace(JWTLoginFilter.TOKEN_PREFIX, "")).getClaim("user_id").asString();

      if (userId != null) {
        Optional<UserAccount> oua = userAccountRepository.findById(UUID.fromString(userId));
        if (oua.isEmpty()) {
          logger.info("Cannot authenticate token with user id " + userId + ". User not found.");
        } else {
          UserAccount ua = oua.get();
          principal = new R2D2Principal(ua.getEmail(), "", new ArrayList<>());
          principal.setUserAccount(ua);
          // return new UsernamePasswordAuthenticationToken(p, null, new ArrayList<>());

        }

      }

    }

    if (reviewToken != null && !reviewToken.isBlank()) {
      if (principal != null) {
        principal.setReviewToken(reviewToken);
      } else {
        principal = new R2D2Principal("anonymous_reviewer", "", new ArrayList<>());
        principal.setReviewToken(reviewToken);
      }

    }


    if (principal != null) {
      return new UsernamePasswordAuthenticationToken(principal, null, principal.getUserAccount() != null
          ? principal.getUserAccount().getGrants().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
              .collect(Collectors.toList())
          : new ArrayList<>());
    } else {
      return null;
    }



  }
}
