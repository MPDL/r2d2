package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.search.model.SearchRecord;
import de.mpg.mpdl.r2d2.search.model.SearchResult;

import java.util.List;

public final class SearchResultBuilder<T> {
  private int total;
  private String scrollId;
  private List<SearchRecord<T>> hits;

  private SearchResultBuilder() {}

  public static SearchResultBuilder aSearchResult() {
    return new SearchResultBuilder();
  }

  public SearchResultBuilder total(int total) {
    this.total = total;
    return this;
  }

  public SearchResultBuilder scrollId(String scrollId) {
    this.scrollId = scrollId;
    return this;
  }

  public SearchResultBuilder hits(List<SearchRecord<T>> hits) {
    this.hits = hits;
    return this;
  }

  public SearchResult build() {
    SearchResult searchResult = new SearchResult();
    searchResult.setTotal(total);
    searchResult.setScrollId(scrollId);
    searchResult.setHits(hits);
    return searchResult;
  }
}
