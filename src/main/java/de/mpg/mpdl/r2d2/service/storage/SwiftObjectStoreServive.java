package de.mpg.mpdl.r2d2.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.FileService;

@Service
public class SwiftObjectStoreServive {

	private FileStorageService repository;
	private FileService service;

	@Autowired
	public SwiftObjectStoreServive(FileStorageService repo, FileService svc) {
		this.repository = repo;
		this.service = svc;
	}

	public String initializeUpload(String container, File file, R2D2Principal user)
			throws ValidationException, AuthorizationException, R2d2TechnicalException {

		File file2create = null;
		try {
			repository.createContainer(container);
			file2create = service.create(file, user);
		} catch (Exception e) {
			throw new R2d2TechnicalException(e);
		}
		return file2create.getId().toString();
	}
	
	public File upload(String container, File file, byte[] bytes) {
		repository.uploadFile(container, bytes, file.getId().toString(), file.getFormat());
		return file;
	}
}
