package me.lnkkerst.webblogapi.repository;

import java.util.List;
import me.lnkkerst.webblogapi.domain.Comment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Comment entity. */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository
    extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
  @Query("select comment from Comment comment where comment.user.login = ?#{authentication.name}")
  List<Comment> findByUserIsCurrentUser();
}
