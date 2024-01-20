package me.lnkkerst.webblogapi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/** A DTO for the {@link me.lnkkerst.webblogapi.domain.Node} entity. */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeDTO implements Serializable {

  private Long id;

  @NotNull private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NodeDTO)) {
      return false;
    }

    NodeDTO nodeDTO = (NodeDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, nodeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "NodeDTO{" + "id=" + getId() + ", name='" + getName() + "'" + "}";
  }
}
