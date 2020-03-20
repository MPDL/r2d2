package de.mpg.mpdl.r2d2.model.search;

public class SearchRecord<T> {

  private String id;


  private T source;

  public T getSource() {
    return source;
  }

  public void setSource(T source) {
    this.source = source;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


}
