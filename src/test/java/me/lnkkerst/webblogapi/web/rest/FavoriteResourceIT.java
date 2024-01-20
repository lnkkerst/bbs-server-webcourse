package me.lnkkerst.webblogapi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import me.lnkkerst.webblogapi.IntegrationTest;
import me.lnkkerst.webblogapi.domain.Favorite;
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.domain.enumeration.FavoriteType;
import me.lnkkerst.webblogapi.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link FavoriteResource} REST controller. */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FavoriteResourceIT {

  private static final FavoriteType DEFAULT_TYPE = FavoriteType.NODE;
  private static final FavoriteType UPDATED_TYPE = FavoriteType.USER;

  private static final String ENTITY_API_URL = "/api/favorites";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  @Autowired private FavoriteRepository favoriteRepository;

  @Autowired private EntityManager em;

  @Autowired private MockMvc restFavoriteMockMvc;

  private Favorite favorite;

  /**
   * Create an entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Favorite createEntity(EntityManager em) {
    Favorite favorite = new Favorite().type(DEFAULT_TYPE);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    favorite.setOwner(user);
    return favorite;
  }

  /**
   * Create an updated entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Favorite createUpdatedEntity(EntityManager em) {
    Favorite favorite = new Favorite().type(UPDATED_TYPE);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    favorite.setOwner(user);
    return favorite;
  }

  @BeforeEach
  public void initTest() {
    favorite = createEntity(em);
  }

  @Test
  @Transactional
  void createFavorite() throws Exception {
    int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
    // Create the Favorite
    restFavoriteMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isCreated());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeCreate + 1);
    Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
    assertThat(testFavorite.getType()).isEqualTo(DEFAULT_TYPE);
  }

  @Test
  @Transactional
  void createFavoriteWithExistingId() throws Exception {
    // Create the Favorite with an existing ID
    favorite.setId(1L);

    int databaseSizeBeforeCreate = favoriteRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restFavoriteMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isBadRequest());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllFavorites() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    // Get all the favoriteList
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
        .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
  }

  @Test
  @Transactional
  void getFavorite() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    // Get the favorite
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL_ID, favorite.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
        .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
  }

  @Test
  @Transactional
  void getFavoritesByIdFiltering() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    Long id = favorite.getId();

    defaultFavoriteShouldBeFound("id.equals=" + id);
    defaultFavoriteShouldNotBeFound("id.notEquals=" + id);

    defaultFavoriteShouldBeFound("id.greaterThanOrEqual=" + id);
    defaultFavoriteShouldNotBeFound("id.greaterThan=" + id);

    defaultFavoriteShouldBeFound("id.lessThanOrEqual=" + id);
    defaultFavoriteShouldNotBeFound("id.lessThan=" + id);
  }

  @Test
  @Transactional
  void getAllFavoritesByTypeIsEqualToSomething() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    // Get all the favoriteList where type equals to DEFAULT_TYPE
    defaultFavoriteShouldBeFound("type.equals=" + DEFAULT_TYPE);

    // Get all the favoriteList where type equals to UPDATED_TYPE
    defaultFavoriteShouldNotBeFound("type.equals=" + UPDATED_TYPE);
  }

  @Test
  @Transactional
  void getAllFavoritesByTypeIsInShouldWork() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    // Get all the favoriteList where type in DEFAULT_TYPE or UPDATED_TYPE
    defaultFavoriteShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

    // Get all the favoriteList where type equals to UPDATED_TYPE
    defaultFavoriteShouldNotBeFound("type.in=" + UPDATED_TYPE);
  }

  @Test
  @Transactional
  void getAllFavoritesByTypeIsNullOrNotNull() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    // Get all the favoriteList where type is not null
    defaultFavoriteShouldBeFound("type.specified=true");

    // Get all the favoriteList where type is null
    defaultFavoriteShouldNotBeFound("type.specified=false");
  }

  @Test
  @Transactional
  void getAllFavoritesByOwnerIsEqualToSomething() throws Exception {
    User owner;
    if (TestUtil.findAll(em, User.class).isEmpty()) {
      favoriteRepository.saveAndFlush(favorite);
      owner = UserResourceIT.createEntity(em);
    } else {
      owner = TestUtil.findAll(em, User.class).get(0);
    }
    em.persist(owner);
    em.flush();
    favorite.setOwner(owner);
    favoriteRepository.saveAndFlush(favorite);
    Long ownerId = owner.getId();
    // Get all the favoriteList where owner equals to ownerId
    defaultFavoriteShouldBeFound("ownerId.equals=" + ownerId);

    // Get all the favoriteList where owner equals to (ownerId + 1)
    defaultFavoriteShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
  }

  @Test
  @Transactional
  void getAllFavoritesByUserIsEqualToSomething() throws Exception {
    User user;
    if (TestUtil.findAll(em, User.class).isEmpty()) {
      favoriteRepository.saveAndFlush(favorite);
      user = UserResourceIT.createEntity(em);
    } else {
      user = TestUtil.findAll(em, User.class).get(0);
    }
    em.persist(user);
    em.flush();
    favorite.setUser(user);
    favoriteRepository.saveAndFlush(favorite);
    Long userId = user.getId();
    // Get all the favoriteList where user equals to userId
    defaultFavoriteShouldBeFound("userId.equals=" + userId);

    // Get all the favoriteList where user equals to (userId + 1)
    defaultFavoriteShouldNotBeFound("userId.equals=" + (userId + 1));
  }

  @Test
  @Transactional
  void getAllFavoritesByNodeIsEqualToSomething() throws Exception {
    Node node;
    if (TestUtil.findAll(em, Node.class).isEmpty()) {
      favoriteRepository.saveAndFlush(favorite);
      node = NodeResourceIT.createEntity(em);
    } else {
      node = TestUtil.findAll(em, Node.class).get(0);
    }
    em.persist(node);
    em.flush();
    favorite.setNode(node);
    favoriteRepository.saveAndFlush(favorite);
    Long nodeId = node.getId();
    // Get all the favoriteList where node equals to nodeId
    defaultFavoriteShouldBeFound("nodeId.equals=" + nodeId);

    // Get all the favoriteList where node equals to (nodeId + 1)
    defaultFavoriteShouldNotBeFound("nodeId.equals=" + (nodeId + 1));
  }

  @Test
  @Transactional
  void getAllFavoritesByPostIsEqualToSomething() throws Exception {
    Post post;
    if (TestUtil.findAll(em, Post.class).isEmpty()) {
      favoriteRepository.saveAndFlush(favorite);
      post = PostResourceIT.createEntity(em);
    } else {
      post = TestUtil.findAll(em, Post.class).get(0);
    }
    em.persist(post);
    em.flush();
    favorite.setPost(post);
    favoriteRepository.saveAndFlush(favorite);
    Long postId = post.getId();
    // Get all the favoriteList where post equals to postId
    defaultFavoriteShouldBeFound("postId.equals=" + postId);

    // Get all the favoriteList where post equals to (postId + 1)
    defaultFavoriteShouldNotBeFound("postId.equals=" + (postId + 1));
  }

  /** Executes the search, and checks that the default entity is returned. */
  private void defaultFavoriteShouldBeFound(String filter) throws Exception {
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
        .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

    // Check, that the count call also returns 1
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("1"));
  }

  /** Executes the search, and checks that the default entity is not returned. */
  private void defaultFavoriteShouldNotBeFound(String filter) throws Exception {
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    // Check, that the count call also returns 0
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("0"));
  }

  @Test
  @Transactional
  void getNonExistingFavorite() throws Exception {
    // Get the favorite
    restFavoriteMockMvc
        .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingFavorite() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

    // Update the favorite
    Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).orElseThrow();
    // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
    em.detach(updatedFavorite);
    updatedFavorite.type(UPDATED_TYPE);

    restFavoriteMockMvc
        .perform(
            put(ENTITY_API_URL_ID, updatedFavorite.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedFavorite)))
        .andExpect(status().isOk());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
    assertThat(testFavorite.getType()).isEqualTo(UPDATED_TYPE);
  }

  @Test
  @Transactional
  void putNonExistingFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            put(ENTITY_API_URL_ID, favorite.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isBadRequest());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isBadRequest());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateFavoriteWithPatch() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

    // Update the favorite using partial update
    Favorite partialUpdatedFavorite = new Favorite();
    partialUpdatedFavorite.setId(favorite.getId());

    partialUpdatedFavorite.type(UPDATED_TYPE);

    restFavoriteMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite)))
        .andExpect(status().isOk());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
    assertThat(testFavorite.getType()).isEqualTo(UPDATED_TYPE);
  }

  @Test
  @Transactional
  void fullUpdateFavoriteWithPatch() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

    // Update the favorite using partial update
    Favorite partialUpdatedFavorite = new Favorite();
    partialUpdatedFavorite.setId(favorite.getId());

    partialUpdatedFavorite.type(UPDATED_TYPE);

    restFavoriteMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite)))
        .andExpect(status().isOk());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
    assertThat(testFavorite.getType()).isEqualTo(UPDATED_TYPE);
  }

  @Test
  @Transactional
  void patchNonExistingFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, favorite.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isBadRequest());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isBadRequest());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamFavorite() throws Exception {
    int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
    favorite.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFavoriteMockMvc
        .perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Favorite in the database
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteFavorite() throws Exception {
    // Initialize the database
    favoriteRepository.saveAndFlush(favorite);

    int databaseSizeBeforeDelete = favoriteRepository.findAll().size();

    // Delete the favorite
    restFavoriteMockMvc
        .perform(delete(ENTITY_API_URL_ID, favorite.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Favorite> favoriteList = favoriteRepository.findAll();
    assertThat(favoriteList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
