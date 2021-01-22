package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiTitle;

public final class DoiTitleBuilder {
  private String title;

  private DoiTitleBuilder() {}

  public static DoiTitleBuilder aDoiTitle() {
    return new DoiTitleBuilder();
  }

  public DoiTitleBuilder title(String title) {
    this.title = title;
    return this;
  }

  public DoiTitle build() {
    DoiTitle doiTitle = new DoiTitle();
    doiTitle.setTitle(title);
    return doiTitle;
  }
}
