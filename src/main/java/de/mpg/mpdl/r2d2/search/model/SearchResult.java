package de.mpg.mpdl.r2d2.search.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

public class SearchResult<T> {

  private int total;

  @JsonProperty("scroll_id")
  private String scrollId;

  private List<SearchRecord<T>> hits;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public String getScrollId() {
    return scrollId;
  }

  public void setScrollId(String scrollId) {
    this.scrollId = scrollId;
  }

  public List<SearchRecord<T>> getHits() {
    return hits;
  }

  public void setHits(List<SearchRecord<T>> hits) {
    this.hits = hits;
  }

}
