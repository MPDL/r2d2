package de.mpg.mpdl.rdrepo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Dataset {
	
	@Id
	private Long id;
	
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
