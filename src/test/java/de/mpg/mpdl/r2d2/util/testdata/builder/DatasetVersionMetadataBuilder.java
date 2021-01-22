package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DatasetVersionMetadataBuilder {
  private String title;
  private List<Person> authors = new ArrayList<>();
  private String description;
  private List<String> genres;
  private List<String> keywords;
  // the DOI without the prefix and not as URL
  private String doi;
  private License license;
  private List<Publication> correspondingPapers = new ArrayList<>();
  private String language;
  private Set<DatasetVersionMetadata.StudyType> studyTypes = new HashSet<>();
  private List<Project> funding = new ArrayList<>();
  private Geolocation geolocation;

  private DatasetVersionMetadataBuilder() {}

  public static DatasetVersionMetadataBuilder aDatasetVersionMetadata() {
    return new DatasetVersionMetadataBuilder();
  }

  public DatasetVersionMetadataBuilder title(String title) {
    this.title = title;
    return this;
  }

  public DatasetVersionMetadataBuilder authors(List<Person> authors) {
    this.authors = authors;
    return this;
  }

  public DatasetVersionMetadataBuilder description(String description) {
    this.description = description;
    return this;
  }

  public DatasetVersionMetadataBuilder genres(List<String> genres) {
    this.genres = genres;
    return this;
  }

  public DatasetVersionMetadataBuilder keywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }

  public DatasetVersionMetadataBuilder doi(String doi) {
    this.doi = doi;
    return this;
  }

  public DatasetVersionMetadataBuilder license(License license) {
    this.license = license;
    return this;
  }

  public DatasetVersionMetadataBuilder correspondingPapers(List<Publication> correspondingPapers) {
    this.correspondingPapers = correspondingPapers;
    return this;
  }

  public DatasetVersionMetadataBuilder language(String language) {
    this.language = language;
    return this;
  }

  public DatasetVersionMetadataBuilder studyTypes(Set<DatasetVersionMetadata.StudyType> studyTypes) {
    this.studyTypes = studyTypes;
    return this;
  }

  public DatasetVersionMetadataBuilder funding(List<Project> funding) {
    this.funding = funding;
    return this;
  }

  public DatasetVersionMetadataBuilder geolocation(Geolocation geolocation) {
    this.geolocation = geolocation;
    return this;
  }

  public DatasetVersionMetadata build() {
    DatasetVersionMetadata datasetVersionMetadata = new DatasetVersionMetadata();
    datasetVersionMetadata.setTitle(title);
    datasetVersionMetadata.setAuthors(authors);
    datasetVersionMetadata.setDescription(description);
    datasetVersionMetadata.setGenres(genres);
    datasetVersionMetadata.setKeywords(keywords);
    datasetVersionMetadata.setDoi(doi);
    datasetVersionMetadata.setLicense(license);
    datasetVersionMetadata.setCorrespondingPapers(correspondingPapers);
    datasetVersionMetadata.setLanguage(language);
    datasetVersionMetadata.setStudyTypes(studyTypes);
    datasetVersionMetadata.setFunding(funding);
    datasetVersionMetadata.setGeolocation(geolocation);
    return datasetVersionMetadata;
  }
}
