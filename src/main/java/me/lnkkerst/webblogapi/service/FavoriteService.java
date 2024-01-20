package me.lnkkerst.webblogapi.service;

import java.util.Optional;
import me.lnkkerst.webblogapi.domain.Favorite;
import me.lnkkerst.webblogapi.repository.FavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service Implementation for managing {@link me.lnkkerst.webblogapi.domain.Favorite}. */
@Service
@Transactional
public class FavoriteService {

  private final Logger log = LoggerFactory.getLogger(FavoriteService.class);

  private final FavoriteRepository favoriteRepository;

  public FavoriteService(FavoriteRepository favoriteRepository) {
    this.favoriteRepository = favoriteRepository;
  }

  /**
   * Save a favorite.
   *
   * @param favorite the entity to save.
   * @return the persisted entity.
   */
  public Favorite save(Favorite favorite) {
    log.debug("Request to save Favorite : {}", favorite);
    return favoriteRepository.save(favorite);
  }

  /**
   * Update a favorite.
   *
   * @param favorite the entity to save.
   * @return the persisted entity.
   */
  public Favorite update(Favorite favorite) {
    log.debug("Request to update Favorite : {}", favorite);
    return favoriteRepository.save(favorite);
  }

  /**
   * Partially update a favorite.
   *
   * @param favorite the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<Favorite> partialUpdate(Favorite favorite) {
    log.debug("Request to partially update Favorite : {}", favorite);

    return favoriteRepository
        .findById(favorite.getId())
        .map(
            existingFavorite -> {
              if (favorite.getType() != null) {
                existingFavorite.setType(favorite.getType());
              }

              return existingFavorite;
            })
        .map(favoriteRepository::save);
  }

  /**
   * Get all the favorites.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<Favorite> findAll(Pageable pageable) {
    log.debug("Request to get all Favorites");
    return favoriteRepository.findAll(pageable);
  }

  /**
   * Get one favorite by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<Favorite> findOne(Long id) {
    log.debug("Request to get Favorite : {}", id);
    return favoriteRepository.findById(id);
  }

  /**
   * Delete the favorite by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Favorite : {}", id);
    favoriteRepository.deleteById(id);
  }
}
