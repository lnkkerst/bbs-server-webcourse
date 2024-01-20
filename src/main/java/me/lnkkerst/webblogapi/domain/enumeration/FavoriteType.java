package me.lnkkerst.webblogapi.domain.enumeration;

/** The FavoriteType enumeration. */
public enum FavoriteType {
  NODE("node"),
  USER("user"),
  POST("post");

  private final String value;

  FavoriteType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
