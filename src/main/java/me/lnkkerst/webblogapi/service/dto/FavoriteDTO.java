package me.lnkkerst.webblogapi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import me.lnkkerst.webblogapi.domain.enumeration.FavoriteType;

/** A DTO for the {@link me.lnkkerst.webblogapi.domain.Favorite} entity. */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FavoriteDTO implements Serializable {

  private Long id;

  private FavoriteType type;

  private UserDTO owner;

  private UserDTO user;

  private NodeDTO node;

  private PostDTO post;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public FavoriteType getType() {
    return type;
  }

  public void setType(FavoriteType type) {
    this.type = type;
  }

  public UserDTO getOwner() {
    return owner;
  }

  public void setOwner(UserDTO owner) {
    this.owner = owner;
  }

  public UserDTO getUser() {
    return user;
  }

  public void setUser(UserDTO user) {
    this.user = user;
  }

  public NodeDTO getNode() {
    return node;
  }

  public void setNode(NodeDTO node) {
    this.node = node;
  }

  public PostDTO getPost() {
    return post;
  }

  public void setPost(PostDTO post) {
    this.post = post;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FavoriteDTO)) {
      return false;
    }

    FavoriteDTO favoriteDTO = (FavoriteDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, favoriteDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "FavoriteDTO{"
        + "id="
        + getId()
        + ", type='"
        + getType()
        + "'"
        + ", owner="
        + getOwner()
        + ", user="
        + getUser()
        + ", node="
        + getNode()
        + ", post="
        + getPost()
        + "}";
  }
}
