package me.lnkkerst.webblogapi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/** A DTO for the {@link me.lnkkerst.webblogapi.domain.Post} entity. */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostDTO implements Serializable {

  private Long id;

  @NotNull private String title;

  @NotNull private String content;

  @NotNull private ZonedDateTime createdAt;

  private UserDTO user;

  private NodeDTO node;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PostDTO)) {
      return false;
    }

    PostDTO postDTO = (PostDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, postDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "PostDTO{"
        + "id="
        + getId()
        + ", title='"
        + getTitle()
        + "'"
        + ", content='"
        + getContent()
        + "'"
        + ", createdAt='"
        + getCreatedAt()
        + "'"
        + ", user="
        + getUser()
        + ", node="
        + getNode()
        + "}";
  }
}
