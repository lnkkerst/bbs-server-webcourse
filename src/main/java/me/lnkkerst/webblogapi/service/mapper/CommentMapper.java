package me.lnkkerst.webblogapi.service.mapper;

import me.lnkkerst.webblogapi.domain.Comment;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.service.dto.CommentDTO;
import me.lnkkerst.webblogapi.service.dto.PostDTO;
import me.lnkkerst.webblogapi.service.dto.UserDTO;
import org.mapstruct.*;

/** Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}. */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
  @Mapping(target = "reply", source = "reply", qualifiedByName = "commentId")
  @Mapping(target = "post", source = "post", qualifiedByName = "postId")
  @Mapping(target = "user", source = "user", qualifiedByName = "userId")
  CommentDTO toDto(Comment s);

  @Named("commentId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  CommentDTO toDtoCommentId(Comment comment);

  @Named("postId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PostDTO toDtoPostId(Post post);

  @Named("userId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  UserDTO toDtoUserId(User user);
}
