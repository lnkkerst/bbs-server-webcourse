package me.lnkkerst.webblogapi.service.mapper;

import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.service.dto.NodeDTO;
import me.lnkkerst.webblogapi.service.dto.PostDTO;
import me.lnkkerst.webblogapi.service.dto.UserDTO;
import org.mapstruct.*;

/** Mapper for the entity {@link Post} and its DTO {@link PostDTO}. */
@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PostDTO, Post> {
  @Mapping(target = "user", source = "user", qualifiedByName = "userId")
  @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
  PostDTO toDto(Post s);

  @Named("userId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  UserDTO toDtoUserId(User user);

  @Named("nodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  NodeDTO toDtoNodeId(Node node);
}
