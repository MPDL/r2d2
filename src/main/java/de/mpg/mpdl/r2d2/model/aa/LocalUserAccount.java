package de.mpg.mpdl.r2d2.model.aa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class LocalUserAccount {


  @Id
  @GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingUUID",
      strategy = "de.mpg.mpdl.r2d2.db.UseExistingIdOtherwiseGenerateUsingUUID")
  @GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingUUID")
  private UUID id;

  @Column(unique = true)
  private String username;


  @OneToOne
  @JoinColumn(unique = true)
  private UserAccount user;

  String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserAccount getUser() {
    return user;
  }


  public void setUser(UserAccount user) {
    this.user = user;
  }



  public UUID getUserId() {
    return id;
  }

  public void setUserId(UUID id) {
    this.id = id;
  }


}
