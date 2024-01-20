package me.lnkkerst.webblogapi.repository;

import java.util.List;
import me.lnkkerst.webblogapi.domain.Favorite;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Favorite entity. */
@SuppressWarnings("unused")
@Repository
public interface FavoriteRepository
    extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {
  @Query(
      "select favorite from Favorite favorite where favorite.owner.login = ?#{authentication.name}")
  List<Favorite> findByOwnerIsCurrentUser();

  @Query(
      "select favorite from Favorite favorite where favorite.user.login = ?#{authentication.name}")
  List<Favorite> findByUserIsCurrentUser();
}
