package de.mpg.mpdl.r2d2.model.aa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import de.mpg.mpdl.r2d2.model.BaseDb;

@Entity
@Table(name = "users")
public class User extends BaseDb {
	
	public enum Role {
		USER, ADMIN
	}
	
	@Id
	private UUID id;
	
	@Column(unique = true)
	private String email;
	
	private String name;
	
	@Type(type = "string-array")
    @Column(columnDefinition = "text[]")
	private List<Role> roles = new ArrayList<Role>();

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	

}
