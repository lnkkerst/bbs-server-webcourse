package me.lnkkerst.webblogapi.web.rest;

import static me.lnkkerst.webblogapi.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import me.lnkkerst.webblogapi.IntegrationTest;
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link PostResource} REST controller. */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostResourceIT {

  private static final String DEFAULT_TITLE = "AAAAAAAAAA";
  private static final String UPDATED_TITLE = "BBBBBBBBBB";

  private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
  private static final String UPDATED_CONTENT = "BBBBBBBBBB";

  private static final ZonedDateTime DEFAULT_CREATED_AT =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
  private static final ZonedDateTime UPDATED_CREATED_AT =
      ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
  private static final ZonedDateTime SMALLER_CREATED_AT =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

  private static final String ENTITY_API_URL = "/api/posts";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  @Autowired private PostRepository postRepository;

  @Autowired private EntityManager em;

  @Autowired private MockMvc restPostMockMvc;

  private Post post;

  /**
   * Create an entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Post createEntity(EntityManager em) {
    Post post =
        new Post().title(DEFAULT_TITLE).content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    post.setUser(user);
    // Add required entity
    Node node;
    if (TestUtil.findAll(em, Node.class).isEmpty()) {
      node = NodeResourceIT.createEntity(em);
      em.persist(node);
      em.flush();
    } else {
      node = TestUtil.findAll(em, Node.class).get(0);
    }
    post.setNode(node);
    return post;
  }

  /**
   * Create an updated entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Post createUpdatedEntity(EntityManager em) {
    Post post =
        new Post().title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    post.setUser(user);
    // Add required entity
    Node node;
    if (TestUtil.findAll(em, Node.class).isEmpty()) {
      node = NodeResourceIT.createUpdatedEntity(em);
      em.persist(node);
      em.flush();
    } else {
      node = TestUtil.findAll(em, Node.class).get(0);
    }
    post.setNode(node);
    return post;
  }

  @BeforeEach
  public void initTest() {
    post = createEntity(em);
  }

  @Test
  @Transactional
  void createPost() throws Exception {
    int databaseSizeBeforeCreate = postRepository.findAll().size();
    // Create the Post
    restPostMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isCreated());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
    Post testPost = postList.get(postList.size() - 1);
    assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
    assertThat(testPost.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
  }

  @Test
  @Transactional
  void createPostWithExistingId() throws Exception {
    // Create the Post with an existing ID
    post.setId(1L);

    int databaseSizeBeforeCreate = postRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restPostMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void checkTitleIsRequired() throws Exception {
    int databaseSizeBeforeTest = postRepository.findAll().size();
    // set the field null
    post.setTitle(null);

    // Create the Post, which fails.

    restPostMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void checkContentIsRequired() throws Exception {
    int databaseSizeBeforeTest = postRepository.findAll().size();
    // set the field null
    post.setContent(null);

    // Create the Post, which fails.

    restPostMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void checkCreatedAtIsRequired() throws Exception {
    int databaseSizeBeforeTest = postRepository.findAll().size();
    // set the field null
    post.setCreatedAt(null);

    // Create the Post, which fails.

    restPostMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void getAllPosts() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList
    restPostMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
        .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
        .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
        .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
  }

  @Test
  @Transactional
  void getPost() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get the post
    restPostMockMvc
        .perform(get(ENTITY_API_URL_ID, post.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(post.getId().intValue()))
        .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
        .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
        .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
  }

  @Test
  @Transactional
  void getPostsByIdFiltering() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    Long id = post.getId();

    defaultPostShouldBeFound("id.equals=" + id);
    defaultPostShouldNotBeFound("id.notEquals=" + id);

    defaultPostShouldBeFound("id.greaterThanOrEqual=" + id);
    defaultPostShouldNotBeFound("id.greaterThan=" + id);

    defaultPostShouldBeFound("id.lessThanOrEqual=" + id);
    defaultPostShouldNotBeFound("id.lessThan=" + id);
  }

  @Test
  @Transactional
  void getAllPostsByTitleIsEqualToSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where title equals to DEFAULT_TITLE
    defaultPostShouldBeFound("title.equals=" + DEFAULT_TITLE);

    // Get all the postList where title equals to UPDATED_TITLE
    defaultPostShouldNotBeFound("title.equals=" + UPDATED_TITLE);
  }

  @Test
  @Transactional
  void getAllPostsByTitleIsInShouldWork() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where title in DEFAULT_TITLE or UPDATED_TITLE
    defaultPostShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

    // Get all the postList where title equals to UPDATED_TITLE
    defaultPostShouldNotBeFound("title.in=" + UPDATED_TITLE);
  }

  @Test
  @Transactional
  void getAllPostsByTitleIsNullOrNotNull() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where title is not null
    defaultPostShouldBeFound("title.specified=true");

    // Get all the postList where title is null
    defaultPostShouldNotBeFound("title.specified=false");
  }

  @Test
  @Transactional
  void getAllPostsByTitleContainsSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where title contains DEFAULT_TITLE
    defaultPostShouldBeFound("title.contains=" + DEFAULT_TITLE);

    // Get all the postList where title contains UPDATED_TITLE
    defaultPostShouldNotBeFound("title.contains=" + UPDATED_TITLE);
  }

  @Test
  @Transactional
  void getAllPostsByTitleNotContainsSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where title does not contain DEFAULT_TITLE
    defaultPostShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

    // Get all the postList where title does not contain UPDATED_TITLE
    defaultPostShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
  }

  @Test
  @Transactional
  void getAllPostsByContentIsEqualToSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where content equals to DEFAULT_CONTENT
    defaultPostShouldBeFound("content.equals=" + DEFAULT_CONTENT);

    // Get all the postList where content equals to UPDATED_CONTENT
    defaultPostShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllPostsByContentIsInShouldWork() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where content in DEFAULT_CONTENT or UPDATED_CONTENT
    defaultPostShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

    // Get all the postList where content equals to UPDATED_CONTENT
    defaultPostShouldNotBeFound("content.in=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllPostsByContentIsNullOrNotNull() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where content is not null
    defaultPostShouldBeFound("content.specified=true");

    // Get all the postList where content is null
    defaultPostShouldNotBeFound("content.specified=false");
  }

  @Test
  @Transactional
  void getAllPostsByContentContainsSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where content contains DEFAULT_CONTENT
    defaultPostShouldBeFound("content.contains=" + DEFAULT_CONTENT);

    // Get all the postList where content contains UPDATED_CONTENT
    defaultPostShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllPostsByContentNotContainsSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where content does not contain DEFAULT_CONTENT
    defaultPostShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

    // Get all the postList where content does not contain UPDATED_CONTENT
    defaultPostShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsEqualToSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt equals to DEFAULT_CREATED_AT
    defaultPostShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

    // Get all the postList where createdAt equals to UPDATED_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsInShouldWork() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
    defaultPostShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

    // Get all the postList where createdAt equals to UPDATED_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsNullOrNotNull() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt is not null
    defaultPostShouldBeFound("createdAt.specified=true");

    // Get all the postList where createdAt is null
    defaultPostShouldNotBeFound("createdAt.specified=false");
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt is greater than or equal to DEFAULT_CREATED_AT
    defaultPostShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

    // Get all the postList where createdAt is greater than or equal to UPDATED_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt is less than or equal to DEFAULT_CREATED_AT
    defaultPostShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

    // Get all the postList where createdAt is less than or equal to SMALLER_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsLessThanSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt is less than DEFAULT_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

    // Get all the postList where createdAt is less than UPDATED_CREATED_AT
    defaultPostShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByCreatedAtIsGreaterThanSomething() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    // Get all the postList where createdAt is greater than DEFAULT_CREATED_AT
    defaultPostShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

    // Get all the postList where createdAt is greater than SMALLER_CREATED_AT
    defaultPostShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllPostsByUserIsEqualToSomething() throws Exception {
    User user;
    if (TestUtil.findAll(em, User.class).isEmpty()) {
      postRepository.saveAndFlush(post);
      user = UserResourceIT.createEntity(em);
    } else {
      user = TestUtil.findAll(em, User.class).get(0);
    }
    em.persist(user);
    em.flush();
    post.setUser(user);
    postRepository.saveAndFlush(post);
    Long userId = user.getId();
    // Get all the postList where user equals to userId
    defaultPostShouldBeFound("userId.equals=" + userId);

    // Get all the postList where user equals to (userId + 1)
    defaultPostShouldNotBeFound("userId.equals=" + (userId + 1));
  }

  @Test
  @Transactional
  void getAllPostsByNodeIsEqualToSomething() throws Exception {
    Node node;
    if (TestUtil.findAll(em, Node.class).isEmpty()) {
      postRepository.saveAndFlush(post);
      node = NodeResourceIT.createEntity(em);
    } else {
      node = TestUtil.findAll(em, Node.class).get(0);
    }
    em.persist(node);
    em.flush();
    post.setNode(node);
    postRepository.saveAndFlush(post);
    Long nodeId = node.getId();
    // Get all the postList where node equals to nodeId
    defaultPostShouldBeFound("nodeId.equals=" + nodeId);

    // Get all the postList where node equals to (nodeId + 1)
    defaultPostShouldNotBeFound("nodeId.equals=" + (nodeId + 1));
  }

  /** Executes the search, and checks that the default entity is returned. */
  private void defaultPostShouldBeFound(String filter) throws Exception {
    restPostMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
        .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
        .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
        .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

    // Check, that the count call also returns 1
    restPostMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("1"));
  }

  /** Executes the search, and checks that the default entity is not returned. */
  private void defaultPostShouldNotBeFound(String filter) throws Exception {
    restPostMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    // Check, that the count call also returns 0
    restPostMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("0"));
  }

  @Test
  @Transactional
  void getNonExistingPost() throws Exception {
    // Get the post
    restPostMockMvc
        .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingPost() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    int databaseSizeBeforeUpdate = postRepository.findAll().size();

    // Update the post
    Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
    // Disconnect from session so that the updates on updatedPost are not directly saved in db
    em.detach(updatedPost);
    updatedPost.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restPostMockMvc
        .perform(
            put(ENTITY_API_URL_ID, updatedPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
        .andExpect(status().isOk());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    Post testPost = postList.get(postList.size() - 1);
    assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testPost.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void putNonExistingPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            put(ENTITY_API_URL_ID, post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdatePostWithPatch() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    int databaseSizeBeforeUpdate = postRepository.findAll().size();

    // Update the post using partial update
    Post partialUpdatedPost = new Post();
    partialUpdatedPost.setId(post.getId());

    partialUpdatedPost.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restPostMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost)))
        .andExpect(status().isOk());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    Post testPost = postList.get(postList.size() - 1);
    assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testPost.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void fullUpdatePostWithPatch() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    int databaseSizeBeforeUpdate = postRepository.findAll().size();

    // Update the post using partial update
    Post partialUpdatedPost = new Post();
    partialUpdatedPost.setId(post.getId());

    partialUpdatedPost.title(UPDATED_TITLE).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restPostMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost)))
        .andExpect(status().isOk());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    Post testPost = postList.get(postList.size() - 1);
    assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testPost.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void patchNonExistingPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, post.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isBadRequest());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamPost() throws Exception {
    int databaseSizeBeforeUpdate = postRepository.findAll().size();
    post.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPostMockMvc
        .perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(post)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Post in the database
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deletePost() throws Exception {
    // Initialize the database
    postRepository.saveAndFlush(post);

    int databaseSizeBeforeDelete = postRepository.findAll().size();

    // Delete the post
    restPostMockMvc
        .perform(delete(ENTITY_API_URL_ID, post.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Post> postList = postRepository.findAll();
    assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
