package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.r2d2.model.validation.PublishConstraintGroup;
import de.mpg.mpdl.r2d2.model.validation.SaveConstraintGroup;

@JsonInclude(Include.NON_EMPTY)
public class DatasetVersionMetadata {

  @NotBlank(message = "{dataset.metadata.title.blank}", groups = {SaveConstraintGroup.class, PublishConstraintGroup.class})
  private String title;

  // creator is in BaseDateDB
  @NotEmpty(message = "{dataset.metadata.authors.empty}", groups = {PublishConstraintGroup.class})
  private List<@Valid Person> authors = new ArrayList<>();

  private String description;

  private List<String> genres;

  private List<String> keywords;

  // the DOI consisting of: prefix/suffix
  private String doi;

  private License license;

  // creationDate (created) is in BaseDateDb
  // modificationDate (modified) is in BaseDateDb
  // publicationDate (issued) is in DatasetVersion
  // (withdrawn) is modificationDate of a DatasetVersion with state WITHDRAWN

  private List<Publication> correspondingPapers = new ArrayList<>();

  private String language;

  private Set<StudyType> studyTypes = new HashSet<>();

  private List<Project> funding = new ArrayList<>();

  private Geolocation geolocation;

  // 'Cite this Dataset as' gets automatically created/composed

  // state is in DatasetVersion

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Person> getAuthors() {
    return authors;
  }

  public void setAuthors(List<Person> authors) {
    this.authors = authors;
  }

  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getGenres() {
    return genres;
  }

  public void setGenres(List<String> genres) {
    this.genres = genres;
  }

  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  public License getLicense() {
    return license;
  }

  public void setLicense(License license) {
    this.license = license;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public List<Publication> getCorrespondingPapers() {
    return correspondingPapers;
  }

  public void setCorrespondingPapers(List<Publication> correspondingPapers) {
    this.correspondingPapers = correspondingPapers;
  }

  public Set<StudyType> getStudyTypes() {
    return studyTypes;
  }

  public void setStudyTypes(Set<StudyType> studyTypes) {
    this.studyTypes = studyTypes;
  }

  public List<Project> getFunding() {
    return funding;
  }

  public void setFunding(List<Project> funding) {
    this.funding = funding;
  }

  public Geolocation getGeolocation() {
    return geolocation;
  }

  public void setGeolocation(Geolocation geolocation) {
    this.geolocation = geolocation;
  }

  public enum StudyType {
    OBSERVATIONAL,
    EXPERIMENTAL,
    SIMULATION, //simulation/modelling
    SURVEY,
    OTHER
  }

}
