package de.mpg.mpdl.r2d2.service.storage;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.impl.GenericServiceDbImpl;

@Service
public class FileServiceImpl extends GenericServiceDbImpl<File> implements FileService {

  private FileRepository fileRepository;
  private FileDaoEs fileDao;

  @Autowired
  public FileServiceImpl(FileRepository repository, FileDaoEs dao) {
    this.fileRepository = repository;
    this.fileDao = dao;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File create(File object, R2D2Principal user) throws R2d2TechnicalException, ValidationException, AuthorizationException {

    File file = createFile(object, user.getUserAccount());
    /*
     * TODO
     */
    // checkAa("create", user, file);
    try {
      file = fileRepository.save(file);
      fileDao.createImmediately(file.getId().toString(), file);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File update(File object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException {

    File file2update = get(object.getId(), user);
    checkEqualModificationDate(object.getModificationDate(), file2update.getModificationDate());

    file2update = createFile(object, user.getUserAccount());
    try {
      file2update = fileRepository.save(file2update);
      fileDao.createImmediately(file2update.getId().toString(), file2update);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file2update;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    File file = get(id, user);
    checkEqualModificationDate(lastModificationDate, file.getModificationDate());
    try {
      fileRepository.deleteById(id);
      fileDao.delete(id.toString());
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  @Override
  @Transactional(readOnly = true)
  public File get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {

    File file = fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
    /*
     * TODO
     */
    // checkAa("get", user, file);

    return file;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub

  }

  @Override
  protected GenericDaoEs<File> getIndexDao() {
    return fileDao;
  }

  private File createFile(File file, UserAccount user) {

    File theFile = new File();
    theFile.setCreator(user);
    theFile.setModifier(user);
    theFile.setState(file.getState());
    theFile.setChecksum(file.getChecksum());
    theFile.setTotalParts(file.getTotalParts());
    theFile.setCompletedParts(file.getCompletedParts());
    theFile.setFilename(file.getFilename());
    theFile.setFormat(file.getFormat());
    theFile.setSize(file.getSize());
    theFile.setStorageLocation(file.getStorageLocation());
    return theFile;

  }

}
