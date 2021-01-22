package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.search.model.SearchQuery;

public final class SearchQueryBuilder {
  private String query;
  private String sort;
  private int from = 0;
  private int size = 10;
  private String scrollId;
  private boolean scroll = false;

  private SearchQueryBuilder() {}

  public static SearchQueryBuilder aSearchQuery() {
    return new SearchQueryBuilder();
  }

  public SearchQueryBuilder query(String query) {
    this.query = query;
    return this;
  }

  public SearchQueryBuilder sort(String sort) {
    this.sort = sort;
    return this;
  }

  public SearchQueryBuilder from(int from) {
    this.from = from;
    return this;
  }

  public SearchQueryBuilder size(int size) {
    this.size = size;
    return this;
  }

  public SearchQueryBuilder scrollId(String scrollId) {
    this.scrollId = scrollId;
    return this;
  }

  public SearchQueryBuilder scroll(boolean scroll) {
    this.scroll = scroll;
    return this;
  }

  public SearchQuery build() {
    SearchQuery searchQuery = new SearchQuery();
    searchQuery.setQuery(query);
    searchQuery.setSort(sort);
    searchQuery.setFrom(from);
    searchQuery.setSize(size);
    searchQuery.setScrollId(scrollId);
    searchQuery.setScroll(scroll);
    return searchQuery;
  }
}
