package me.lnkkerst.webblogapi.repository;

import java.util.List;
import me.lnkkerst.webblogapi.domain.Post;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Post entity. */
@SuppressWarnings("unused")
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
  @Query("select post from Post post where post.user.login = ?#{authentication.name}")
  List<Post> findByUserIsCurrentUser();
}
