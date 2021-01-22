package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Publication;

import java.net.URL;

public final class PublicationBuilder {
  private String title;
  //Identifier = URL
  private URL url;

  private PublicationBuilder() {}

  public static PublicationBuilder aPublication() {
    return new PublicationBuilder();
  }

  public PublicationBuilder title(String title) {
    this.title = title;
    return this;
  }

  public PublicationBuilder url(URL url) {
    this.url = url;
    return this;
  }

  public Publication build() {
    Publication publication = new Publication();
    publication.setTitle(title);
    publication.setUrl(url);
    return publication;
  }
}
