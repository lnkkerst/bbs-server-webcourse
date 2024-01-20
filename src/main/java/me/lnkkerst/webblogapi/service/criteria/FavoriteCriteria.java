package me.lnkkerst.webblogapi.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import me.lnkkerst.webblogapi.domain.enumeration.FavoriteType;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link me.lnkkerst.webblogapi.domain.Favorite} entity. This class is used
 * in {@link me.lnkkerst.webblogapi.web.rest.FavoriteResource} to receive all the possible filtering
 * options from the Http GET request parameters. For example the following could be a valid request:
 * {@code /favorites?id.greaterThan=5&attr1.contains=something&attr2.specified=false} As Spring is
 * unable to properly convert the types, unless specific {@link Filter} class are used, we need to
 * use fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FavoriteCriteria implements Serializable, Criteria {

  /** Class for filtering FavoriteType */
  public static class FavoriteTypeFilter extends Filter<FavoriteType> {

    public FavoriteTypeFilter() {}

    public FavoriteTypeFilter(FavoriteTypeFilter filter) {
      super(filter);
    }

    @Override
    public FavoriteTypeFilter copy() {
      return new FavoriteTypeFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private FavoriteTypeFilter type;

  private LongFilter ownerId;

  private LongFilter userId;

  private LongFilter nodeId;

  private LongFilter postId;

  private Boolean distinct;

  public FavoriteCriteria() {}

  public FavoriteCriteria(FavoriteCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.type = other.type == null ? null : other.type.copy();
    this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.nodeId = other.nodeId == null ? null : other.nodeId.copy();
    this.postId = other.postId == null ? null : other.postId.copy();
    this.distinct = other.distinct;
  }

  @Override
  public FavoriteCriteria copy() {
    return new FavoriteCriteria(this);
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

  public FavoriteTypeFilter getType() {
    return type;
  }

  public FavoriteTypeFilter type() {
    if (type == null) {
      type = new FavoriteTypeFilter();
    }
    return type;
  }

  public void setType(FavoriteTypeFilter type) {
    this.type = type;
  }

  public LongFilter getOwnerId() {
    return ownerId;
  }

  public LongFilter ownerId() {
    if (ownerId == null) {
      ownerId = new LongFilter();
    }
    return ownerId;
  }

  public void setOwnerId(LongFilter ownerId) {
    this.ownerId = ownerId;
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
    final FavoriteCriteria that = (FavoriteCriteria) o;
    return (Objects.equals(id, that.id)
        && Objects.equals(type, that.type)
        && Objects.equals(ownerId, that.ownerId)
        && Objects.equals(userId, that.userId)
        && Objects.equals(nodeId, that.nodeId)
        && Objects.equals(postId, that.postId)
        && Objects.equals(distinct, that.distinct));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, ownerId, userId, nodeId, postId, distinct);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "FavoriteCriteria{"
        + (id != null ? "id=" + id + ", " : "")
        + (type != null ? "type=" + type + ", " : "")
        + (ownerId != null ? "ownerId=" + ownerId + ", " : "")
        + (userId != null ? "userId=" + userId + ", " : "")
        + (nodeId != null ? "nodeId=" + nodeId + ", " : "")
        + (postId != null ? "postId=" + postId + ", " : "")
        + (distinct != null ? "distinct=" + distinct + ", " : "")
        + "}";
  }
}
