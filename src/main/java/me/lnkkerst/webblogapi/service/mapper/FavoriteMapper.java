package me.lnkkerst.webblogapi.service.mapper;

import me.lnkkerst.webblogapi.domain.Favorite;
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.service.dto.FavoriteDTO;
import me.lnkkerst.webblogapi.service.dto.NodeDTO;
import me.lnkkerst.webblogapi.service.dto.PostDTO;
import me.lnkkerst.webblogapi.service.dto.UserDTO;
import org.mapstruct.*;

/** Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}. */
@Mapper(componentModel = "spring")
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
  @Mapping(target = "owner", source = "owner", qualifiedByName = "userId")
  @Mapping(target = "user", source = "user", qualifiedByName = "userId")
  @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
  @Mapping(target = "post", source = "post", qualifiedByName = "postId")
  FavoriteDTO toDto(Favorite s);

  @Named("userId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  UserDTO toDtoUserId(User user);

  @Named("nodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  NodeDTO toDtoNodeId(Node node);

  @Named("postId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PostDTO toDtoPostId(Post post);
}
