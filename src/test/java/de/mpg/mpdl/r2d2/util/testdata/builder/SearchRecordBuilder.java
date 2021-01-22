package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.search.model.SearchRecord;

public final class SearchRecordBuilder<T> {
  private String id;
  private T source;

  private SearchRecordBuilder() {}

  public static SearchRecordBuilder aSearchRecord() {
    return new SearchRecordBuilder();
  }

  public SearchRecordBuilder id(String id) {
    this.id = id;
    return this;
  }

  public SearchRecordBuilder source(T source) {
    this.source = source;
    return this;
  }

  public SearchRecord build() {
    SearchRecord searchRecord = new SearchRecord();
    searchRecord.setId(id);
    searchRecord.setSource(source);
    return searchRecord;
  }
}
