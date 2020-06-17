package de.mpg.mpdl.r2d2.registration;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;

@Entity
public class RegistrationConfirmationToken {

  private static final int EXPIRATION_TIME = 60 * 24;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String token;

  @OneToOne(targetEntity = LocalUserAccount.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
  private LocalUserAccount user;

  private Date expirationDate;

  public RegistrationConfirmationToken() {
    super();
  }

  public RegistrationConfirmationToken(final String token) {
    super();

    this.token = token;
    this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
  }

  public RegistrationConfirmationToken(final String token, final LocalUserAccount user) {
    super();

    this.token = token;
    this.user = user;
    this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
  }

  public UUID getId() {
    return id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public LocalUserAccount getUser() {
    return user;
  }

  public void setUser(final LocalUserAccount user) {
    this.user = user;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(final Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  private Date calculateExpirationDate(final int expirationTimeInMinutes) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(new Date().getTime());
    cal.add(Calendar.MINUTE, expirationTimeInMinutes);
    return new Date(cal.getTime().getTime());
  }

  public void updateToken(final String token) {
    this.token = token;
    this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RegistrationConfirmationToken other = (RegistrationConfirmationToken) obj;
    if (expirationDate == null) {
      if (other.expirationDate != null) {
        return false;
      }
    } else if (!expirationDate.equals(other.expirationDate)) {
      return false;
    }
    if (token == null) {
      if (other.token != null) {
        return false;
      }
    } else if (!token.equals(other.token)) {
      return false;
    }
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Token [String=").append(token).append("]").append("[Expires").append(expirationDate).append("]");
    return builder.toString();
  }

}
