package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.List;

public class DatasetVersionMetadata {

  private String title;

  // creator is in BaseDB

  private List<Person> authors = new ArrayList<>();
  
  private String doi;

  private String description;

  private List<String> genres;

  private List<String> keywords;

  private String license;

  private String language;

  private List<Publication> correspondingPapers = new ArrayList<>();

  // doi is in DatasetVersion

  // citeAs gets automatically created/composed

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

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
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

}
