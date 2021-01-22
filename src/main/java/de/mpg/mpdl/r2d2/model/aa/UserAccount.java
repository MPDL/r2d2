package de.mpg.mpdl.r2d2.model.aa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.mpg.mpdl.r2d2.model.BaseDb;
import de.mpg.mpdl.r2d2.model.Person;

@Entity
@Table(name = "user_account")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = UserAccount.class,
    resolver = UserAccountIdResolver.class)

public class UserAccount extends BaseDb {

  public enum Role {
    USER,
    ADMIN
  }

  @Column(unique = true)
  private String email;

  boolean active = false;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private Person person;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private List<Role> roles = new ArrayList<Role>();

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person p) {
    this.person = p;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
