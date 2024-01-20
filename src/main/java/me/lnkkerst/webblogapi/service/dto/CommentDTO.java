package me.lnkkerst.webblogapi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/** A DTO for the {@link me.lnkkerst.webblogapi.domain.Comment} entity. */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommentDTO implements Serializable {

  private Long id;

  @NotNull private String content;

  @NotNull private ZonedDateTime createdAt;

  private CommentDTO reply;

  private PostDTO post;

  private UserDTO user;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public CommentDTO getReply() {
    return reply;
  }

  public void setReply(CommentDTO reply) {
    this.reply = reply;
  }

  public PostDTO getPost() {
    return post;
  }

  public void setPost(PostDTO post) {
    this.post = post;
  }

  public UserDTO getUser() {
    return user;
  }

  public void setUser(UserDTO user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CommentDTO)) {
      return false;
    }

    CommentDTO commentDTO = (CommentDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, commentDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "CommentDTO{"
        + "id="
        + getId()
        + ", content='"
        + getContent()
        + "'"
        + ", createdAt='"
        + getCreatedAt()
        + "'"
        + ", reply="
        + getReply()
        + ", post="
        + getPost()
        + ", user="
        + getUser()
        + "}";
  }
}
