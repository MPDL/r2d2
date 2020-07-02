package de.mpg.mpdl.r2d2.aa;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private LocalUserAccountRepository userRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LocalUserAccount applicationUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

    R2D2Principal p = new R2D2Principal(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.getUser().isActive(),
        new ArrayList<GrantedAuthority>());
    p.setUserAccount(applicationUser.getUser());
    return p;//new User(applicationUser.getUsername(), applicationUser.getPassword(), new ArrayList<GrantedAuthority>());
  }
}
