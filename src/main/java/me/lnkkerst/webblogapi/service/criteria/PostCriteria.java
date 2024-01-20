package me.lnkkerst.webblogapi.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link me.lnkkerst.webblogapi.domain.Post} entity. This class is used in
 * {@link me.lnkkerst.webblogapi.web.rest.PostResource} to receive all the possible filtering
 * options from the Http GET request parameters. For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false} As Spring is
 * unable to properly convert the types, unless specific {@link Filter} class are used, we need to
 * use fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter title;

  private StringFilter content;

  private ZonedDateTimeFilter createdAt;

  private LongFilter userId;

  private LongFilter nodeId;

  private Boolean distinct;

  public PostCriteria() {}

  public PostCriteria(PostCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.title = other.title == null ? null : other.title.copy();
    this.content = other.content == null ? null : other.content.copy();
    this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.nodeId = other.nodeId == null ? null : other.nodeId.copy();
    this.distinct = other.distinct;
  }

  @Override
  public PostCriteria copy() {
    return new PostCriteria(this);
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

  public StringFilter getTitle() {
    return title;
  }

  public StringFilter title() {
    if (title == null) {
      title = new StringFilter();
    }
    return title;
  }

  public void setTitle(StringFilter title) {
    this.title = title;
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

  public LongFilter getNodeId() {
    return nodeId;
  }

  public LongFilter nodeId() {
    if (nodeId == null) {
      nodeId = new LongFilter();
    }
    return nodeId;
  }

  public void setNodeId(LongFilter nodeId) {
    this.nodeId = nodeId;
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
    final PostCriteria that = (PostCriteria) o;
    return (Objects.equals(id, that.id)
        && Objects.equals(title, that.title)
        && Objects.equals(content, that.content)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(userId, that.userId)
        && Objects.equals(nodeId, that.nodeId)
        && Objects.equals(distinct, that.distinct));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, createdAt, userId, nodeId, distinct);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "PostCriteria{"
        + (id != null ? "id=" + id + ", " : "")
        + (title != null ? "title=" + title + ", " : "")
        + (content != null ? "content=" + content + ", " : "")
        + (createdAt != null ? "createdAt=" + createdAt + ", " : "")
        + (userId != null ? "userId=" + userId + ", " : "")
        + (nodeId != null ? "nodeId=" + nodeId + ", " : "")
        + (distinct != null ? "distinct=" + distinct + ", " : "")
        + "}";
  }
}
