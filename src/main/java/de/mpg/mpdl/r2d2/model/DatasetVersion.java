package de.mpg.mpdl.r2d2.model;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import de.mpg.mpdl.r2d2.model.Dataset.State;

@Entity
public class DatasetVersion extends BaseDb {
	

	@Column(nullable = false)
	public int versionNumber = 1;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Dataset.State state = State.PRIVATE;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(nullable = false)
	private Dataset dataset = new Dataset();
	
	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime publicationDate;
	
	private String doi;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private DatasetVersionMetadata metadata = new DatasetVersionMetadata();
	
	@ManyToMany
	private List<File> files;

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Dataset.State getState() {
		return state;
	}

	public void setState(Dataset.State state) {
		this.state = state;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public OffsetDateTime getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(OffsetDateTime publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public DatasetVersionMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(DatasetVersionMetadata metadata) {
		this.metadata = metadata;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
