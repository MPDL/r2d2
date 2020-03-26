package de.mpg.mpdl.r2d2.aa;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.InternalUserRepository;
import de.mpg.mpdl.r2d2.db.UserRepository;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private InternalUserRepository userRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LocalUserAccount applicationUser = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));

    return new User(applicationUser.getUsername(), applicationUser.getPassword(), new ArrayList<GrantedAuthority>());
  }
}
