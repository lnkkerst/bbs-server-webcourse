package me.lnkkerst.webblogapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import me.lnkkerst.webblogapi.domain.enumeration.FavoriteType;

/** A Favorite. */
@Entity
@Table(name = "favorite")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Favorite implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private FavoriteType type;

  @ManyToOne(optional = false)
  @NotNull
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Node node;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnoreProperties(
      value = {"user", "node"},
      allowSetters = true)
  private Post post;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public Favorite id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public FavoriteType getType() {
    return this.type;
  }

  public Favorite type(FavoriteType type) {
    this.setType(type);
    return this;
  }

  public void setType(FavoriteType type) {
    this.type = type;
  }

  public User getOwner() {
    return this.owner;
  }

  public void setOwner(User user) {
    this.owner = user;
  }

  public Favorite owner(User user) {
    this.setOwner(user);
    return this;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Favorite user(User user) {
    this.setUser(user);
    return this;
  }

  public Node getNode() {
    return this.node;
  }

  public void setNode(Node node) {
    this.node = node;
  }

  public Favorite node(Node node) {
    this.setNode(node);
    return this;
  }

  public Post getPost() {
    return this.post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public Favorite post(Post post) {
    this.setPost(post);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Favorite)) {
      return false;
    }
    return getId() != null && getId().equals(((Favorite) o).getId());
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
    return "Favorite{" + "id=" + getId() + ", type='" + getType() + "'" + "}";
  }
}
