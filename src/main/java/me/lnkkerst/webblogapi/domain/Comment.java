package me.lnkkerst.webblogapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/** A Comment. */
@Entity
@Table(name = "comment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Comment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(max = 1024)
  @Column(name = "content", length = 1024, nullable = false)
  private String content;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @ManyToOne(optional = false)
  @NotNull
  @JsonIgnoreProperties(
      value = {"user", "node"},
      allowSetters = true)
  private Post post;

  @ManyToOne(optional = false)
  @NotNull
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnoreProperties(
      value = {"post", "user", "reply"},
      allowSetters = true)
  private Comment reply;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public Comment id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return this.content;
  }

  public Comment content(String content) {
    this.setContent(content);
    return this;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ZonedDateTime getCreatedAt() {
    return this.createdAt;
  }

  public Comment createdAt(ZonedDateTime createdAt) {
    this.setCreatedAt(createdAt);
    return this;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Post getPost() {
    return this.post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public Comment post(Post post) {
    this.setPost(post);
    return this;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Comment user(User user) {
    this.setUser(user);
    return this;
  }

  public Comment getReply() {
    return this.reply;
  }

  public void setReply(Comment comment) {
    this.reply = comment;
  }

  public Comment reply(Comment comment) {
    this.setReply(comment);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Comment)) {
      return false;
    }
    return getId() != null && getId().equals(((Comment) o).getId());
  }

  @Override
  public int hashCode() {
    // see
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "Comment{"
        + "id="
        + getId()
        + ", content='"
        + getContent()
        + "'"
        + ", createdAt='"
        + getCreatedAt()
        + "'"
        + "}";
  }
}
