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
import me.lnkkerst.webblogapi.domain.Comment;
import me.lnkkerst.webblogapi.domain.Post;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link CommentResource} REST controller. */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommentResourceIT {

  private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
  private static final String UPDATED_CONTENT = "BBBBBBBBBB";

  private static final ZonedDateTime DEFAULT_CREATED_AT =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
  private static final ZonedDateTime UPDATED_CREATED_AT =
      ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
  private static final ZonedDateTime SMALLER_CREATED_AT =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

  private static final String ENTITY_API_URL = "/api/comments";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  @Autowired private CommentRepository commentRepository;

  @Autowired private EntityManager em;

  @Autowired private MockMvc restCommentMockMvc;

  private Comment comment;

  /**
   * Create an entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Comment createEntity(EntityManager em) {
    Comment comment = new Comment().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT);
    // Add required entity
    Post post;
    if (TestUtil.findAll(em, Post.class).isEmpty()) {
      post = PostResourceIT.createEntity(em);
      em.persist(post);
      em.flush();
    } else {
      post = TestUtil.findAll(em, Post.class).get(0);
    }
    comment.setPost(post);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    comment.setUser(user);
    return comment;
  }

  /**
   * Create an updated entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Comment createUpdatedEntity(EntityManager em) {
    Comment comment = new Comment().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);
    // Add required entity
    Post post;
    if (TestUtil.findAll(em, Post.class).isEmpty()) {
      post = PostResourceIT.createUpdatedEntity(em);
      em.persist(post);
      em.flush();
    } else {
      post = TestUtil.findAll(em, Post.class).get(0);
    }
    comment.setPost(post);
    // Add required entity
    User user = UserResourceIT.createEntity(em);
    em.persist(user);
    em.flush();
    comment.setUser(user);
    return comment;
  }

  @BeforeEach
  public void initTest() {
    comment = createEntity(em);
  }

  @Test
  @Transactional
  void createComment() throws Exception {
    int databaseSizeBeforeCreate = commentRepository.findAll().size();
    // Create the Comment
    restCommentMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isCreated());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
    Comment testComment = commentList.get(commentList.size() - 1);
    assertThat(testComment.getContent()).isEqualTo(DEFAULT_CONTENT);
    assertThat(testComment.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
  }

  @Test
  @Transactional
  void createCommentWithExistingId() throws Exception {
    // Create the Comment with an existing ID
    comment.setId(1L);

    int databaseSizeBeforeCreate = commentRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restCommentMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void checkContentIsRequired() throws Exception {
    int databaseSizeBeforeTest = commentRepository.findAll().size();
    // set the field null
    comment.setContent(null);

    // Create the Comment, which fails.

    restCommentMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void checkCreatedAtIsRequired() throws Exception {
    int databaseSizeBeforeTest = commentRepository.findAll().size();
    // set the field null
    comment.setCreatedAt(null);

    // Create the Comment, which fails.

    restCommentMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void getAllComments() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList
    restCommentMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
        .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
        .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
  }

  @Test
  @Transactional
  void getComment() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get the comment
    restCommentMockMvc
        .perform(get(ENTITY_API_URL_ID, comment.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
        .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
        .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
  }

  @Test
  @Transactional
  void getCommentsByIdFiltering() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    Long id = comment.getId();

    defaultCommentShouldBeFound("id.equals=" + id);
    defaultCommentShouldNotBeFound("id.notEquals=" + id);

    defaultCommentShouldBeFound("id.greaterThanOrEqual=" + id);
    defaultCommentShouldNotBeFound("id.greaterThan=" + id);

    defaultCommentShouldBeFound("id.lessThanOrEqual=" + id);
    defaultCommentShouldNotBeFound("id.lessThan=" + id);
  }

  @Test
  @Transactional
  void getAllCommentsByContentIsEqualToSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where content equals to DEFAULT_CONTENT
    defaultCommentShouldBeFound("content.equals=" + DEFAULT_CONTENT);

    // Get all the commentList where content equals to UPDATED_CONTENT
    defaultCommentShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllCommentsByContentIsInShouldWork() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where content in DEFAULT_CONTENT or UPDATED_CONTENT
    defaultCommentShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

    // Get all the commentList where content equals to UPDATED_CONTENT
    defaultCommentShouldNotBeFound("content.in=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllCommentsByContentIsNullOrNotNull() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where content is not null
    defaultCommentShouldBeFound("content.specified=true");

    // Get all the commentList where content is null
    defaultCommentShouldNotBeFound("content.specified=false");
  }

  @Test
  @Transactional
  void getAllCommentsByContentContainsSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where content contains DEFAULT_CONTENT
    defaultCommentShouldBeFound("content.contains=" + DEFAULT_CONTENT);

    // Get all the commentList where content contains UPDATED_CONTENT
    defaultCommentShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllCommentsByContentNotContainsSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where content does not contain DEFAULT_CONTENT
    defaultCommentShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

    // Get all the commentList where content does not contain UPDATED_CONTENT
    defaultCommentShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsEqualToSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt equals to DEFAULT_CREATED_AT
    defaultCommentShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

    // Get all the commentList where createdAt equals to UPDATED_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsInShouldWork() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
    defaultCommentShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

    // Get all the commentList where createdAt equals to UPDATED_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsNullOrNotNull() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt is not null
    defaultCommentShouldBeFound("createdAt.specified=true");

    // Get all the commentList where createdAt is null
    defaultCommentShouldNotBeFound("createdAt.specified=false");
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt is greater than or equal to DEFAULT_CREATED_AT
    defaultCommentShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

    // Get all the commentList where createdAt is greater than or equal to UPDATED_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt is less than or equal to DEFAULT_CREATED_AT
    defaultCommentShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

    // Get all the commentList where createdAt is less than or equal to SMALLER_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsLessThanSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt is less than DEFAULT_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

    // Get all the commentList where createdAt is less than UPDATED_CREATED_AT
    defaultCommentShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByCreatedAtIsGreaterThanSomething() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    // Get all the commentList where createdAt is greater than DEFAULT_CREATED_AT
    defaultCommentShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

    // Get all the commentList where createdAt is greater than SMALLER_CREATED_AT
    defaultCommentShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
  }

  @Test
  @Transactional
  void getAllCommentsByPostIsEqualToSomething() throws Exception {
    Post post;
    if (TestUtil.findAll(em, Post.class).isEmpty()) {
      commentRepository.saveAndFlush(comment);
      post = PostResourceIT.createEntity(em);
    } else {
      post = TestUtil.findAll(em, Post.class).get(0);
    }
    em.persist(post);
    em.flush();
    comment.setPost(post);
    commentRepository.saveAndFlush(comment);
    Long postId = post.getId();
    // Get all the commentList where post equals to postId
    defaultCommentShouldBeFound("postId.equals=" + postId);

    // Get all the commentList where post equals to (postId + 1)
    defaultCommentShouldNotBeFound("postId.equals=" + (postId + 1));
  }

  @Test
  @Transactional
  void getAllCommentsByUserIsEqualToSomething() throws Exception {
    User user;
    if (TestUtil.findAll(em, User.class).isEmpty()) {
      commentRepository.saveAndFlush(comment);
      user = UserResourceIT.createEntity(em);
    } else {
      user = TestUtil.findAll(em, User.class).get(0);
    }
    em.persist(user);
    em.flush();
    comment.setUser(user);
    commentRepository.saveAndFlush(comment);
    Long userId = user.getId();
    // Get all the commentList where user equals to userId
    defaultCommentShouldBeFound("userId.equals=" + userId);

    // Get all the commentList where user equals to (userId + 1)
    defaultCommentShouldNotBeFound("userId.equals=" + (userId + 1));
  }

  @Test
  @Transactional
  void getAllCommentsByReplyIsEqualToSomething() throws Exception {
    Comment reply;
    if (TestUtil.findAll(em, Comment.class).isEmpty()) {
      commentRepository.saveAndFlush(comment);
      reply = CommentResourceIT.createEntity(em);
    } else {
      reply = TestUtil.findAll(em, Comment.class).get(0);
    }
    em.persist(reply);
    em.flush();
    comment.setReply(reply);
    commentRepository.saveAndFlush(comment);
    Long replyId = reply.getId();
    // Get all the commentList where reply equals to replyId
    defaultCommentShouldBeFound("replyId.equals=" + replyId);

    // Get all the commentList where reply equals to (replyId + 1)
    defaultCommentShouldNotBeFound("replyId.equals=" + (replyId + 1));
  }

  /** Executes the search, and checks that the default entity is returned. */
  private void defaultCommentShouldBeFound(String filter) throws Exception {
    restCommentMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
        .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
        .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

    // Check, that the count call also returns 1
    restCommentMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("1"));
  }

  /** Executes the search, and checks that the default entity is not returned. */
  private void defaultCommentShouldNotBeFound(String filter) throws Exception {
    restCommentMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    // Check, that the count call also returns 0
    restCommentMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("0"));
  }

  @Test
  @Transactional
  void getNonExistingComment() throws Exception {
    // Get the comment
    restCommentMockMvc
        .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingComment() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    int databaseSizeBeforeUpdate = commentRepository.findAll().size();

    // Update the comment
    Comment updatedComment = commentRepository.findById(comment.getId()).orElseThrow();
    // Disconnect from session so that the updates on updatedComment are not directly saved in db
    em.detach(updatedComment);
    updatedComment.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restCommentMockMvc
        .perform(
            put(ENTITY_API_URL_ID, updatedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
        .andExpect(status().isOk());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    Comment testComment = commentList.get(commentList.size() - 1);
    assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testComment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void putNonExistingComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            put(ENTITY_API_URL_ID, comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateCommentWithPatch() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    int databaseSizeBeforeUpdate = commentRepository.findAll().size();

    // Update the comment using partial update
    Comment partialUpdatedComment = new Comment();
    partialUpdatedComment.setId(comment.getId());

    partialUpdatedComment.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restCommentMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedComment.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComment)))
        .andExpect(status().isOk());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    Comment testComment = commentList.get(commentList.size() - 1);
    assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testComment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void fullUpdateCommentWithPatch() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    int databaseSizeBeforeUpdate = commentRepository.findAll().size();

    // Update the comment using partial update
    Comment partialUpdatedComment = new Comment();
    partialUpdatedComment.setId(comment.getId());

    partialUpdatedComment.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

    restCommentMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedComment.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComment)))
        .andExpect(status().isOk());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    Comment testComment = commentList.get(commentList.size() - 1);
    assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
    assertThat(testComment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
  }

  @Test
  @Transactional
  void patchNonExistingComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, comment.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isBadRequest());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamComment() throws Exception {
    int databaseSizeBeforeUpdate = commentRepository.findAll().size();
    comment.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCommentMockMvc
        .perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(comment)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Comment in the database
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteComment() throws Exception {
    // Initialize the database
    commentRepository.saveAndFlush(comment);

    int databaseSizeBeforeDelete = commentRepository.findAll().size();

    // Delete the comment
    restCommentMockMvc
        .perform(delete(ENTITY_API_URL_ID, comment.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Comment> commentList = commentRepository.findAll();
    assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
