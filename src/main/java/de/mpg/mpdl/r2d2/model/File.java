package de.mpg.mpdl.r2d2.model;

import javax.persistence.Entity;

@Entity
public class File extends BaseDb {
	
	private String filename;
	
	private String storageLocation;
	
	private String checksum;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
