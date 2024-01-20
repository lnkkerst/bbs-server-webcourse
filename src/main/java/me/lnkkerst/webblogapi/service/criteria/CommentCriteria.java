package me.lnkkerst.webblogapi.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link me.lnkkerst.webblogapi.domain.Comment} entity. This class is used
 * in {@link me.lnkkerst.webblogapi.web.rest.CommentResource} to receive all the possible filtering
 * options from the Http GET request parameters. For example the following could be a valid request:
 * {@code /comments?id.greaterThan=5&attr1.contains=something&attr2.specified=false} As Spring is
 * unable to properly convert the types, unless specific {@link Filter} class are used, we need to
 * use fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommentCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter content;

  private ZonedDateTimeFilter createdAt;

  private LongFilter postId;

  private LongFilter userId;

  private LongFilter replyId;

  private Boolean distinct;

  public CommentCriteria() {}

  public CommentCriteria(CommentCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.content = other.content == null ? null : other.content.copy();
    this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
    this.postId = other.postId == null ? null : other.postId.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.replyId = other.replyId == null ? null : other.replyId.copy();
    this.distinct = other.distinct;
  }

  @Override
  public CommentCriteria copy() {
    return new CommentCriteria(this);
  }

  public LongFilter getId() {
    return id;
  }

  public LongFilter id() {
    if (id == null) {
      id = new LongFilter();
    }
    return id;
  }

  public void setId(LongFilter id) {
    this.id = id;
  }

  public StringFilter getContent() {
    return content;
  }

  public StringFilter content() {
    if (content == null) {
      content = new StringFilter();
    }
    return content;
  }

  public void setContent(StringFilter content) {
    this.content = content;
  }

  public ZonedDateTimeFilter getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTimeFilter createdAt() {
    if (createdAt == null) {
      createdAt = new ZonedDateTimeFilter();
    }
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTimeFilter createdAt) {
    this.createdAt = createdAt;
  }

  public LongFilter getPostId() {
    return postId;
  }

  public LongFilter postId() {
    if (postId == null) {
      postId = new LongFilter();
    }
    return postId;
  }

  public void setPostId(LongFilter postId) {
    this.postId = postId;
  }

  public LongFilter getUserId() {
    return userId;
  }

  public LongFilter userId() {
    if (userId == null) {
      userId = new LongFilter();
    }
    return userId;
  }

  public void setUserId(LongFilter userId) {
    this.userId = userId;
  }

  public LongFilter getReplyId() {
    return replyId;
  }

  public LongFilter replyId() {
    if (replyId == null) {
      replyId = new LongFilter();
    }
    return replyId;
  }

  public void setReplyId(LongFilter replyId) {
    this.replyId = replyId;
  }

  public Boolean getDistinct() {
    return distinct;
  }

  public void setDistinct(Boolean distinct) {
    this.distinct = distinct;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CommentCriteria that = (CommentCriteria) o;
    return (Objects.equals(id, that.id)
        && Objects.equals(content, that.content)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(postId, that.postId)
        && Objects.equals(userId, that.userId)
        && Objects.equals(replyId, that.replyId)
        && Objects.equals(distinct, that.distinct));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content, createdAt, postId, userId, replyId, distinct);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "CommentCriteria{"
        + (id != null ? "id=" + id + ", " : "")
        + (content != null ? "content=" + content + ", " : "")
        + (createdAt != null ? "createdAt=" + createdAt + ", " : "")
        + (postId != null ? "postId=" + postId + ", " : "")
        + (userId != null ? "userId=" + userId + ", " : "")
        + (replyId != null ? "replyId=" + replyId + ", " : "")
        + (distinct != null ? "distinct=" + distinct + ", " : "")
        + "}";
  }
}
