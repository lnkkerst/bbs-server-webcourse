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
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.repository.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link NodeResource} REST controller. */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeResourceIT {

  private static final String DEFAULT_NAME = "AAAAAAAAAA";
  private static final String UPDATED_NAME = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/nodes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  @Autowired private NodeRepository nodeRepository;

  @Autowired private EntityManager em;

  @Autowired private MockMvc restNodeMockMvc;

  private Node node;

  /**
   * Create an entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Node createEntity(EntityManager em) {
    Node node = new Node().name(DEFAULT_NAME);
    return node;
  }

  /**
   * Create an updated entity for this test.
   *
   * <p>This is a static method, as tests for other entities might also need it, if they test an
   * entity which requires the current entity.
   */
  public static Node createUpdatedEntity(EntityManager em) {
    Node node = new Node().name(UPDATED_NAME);
    return node;
  }

  @BeforeEach
  public void initTest() {
    node = createEntity(em);
  }

  @Test
  @Transactional
  void createNode() throws Exception {
    int databaseSizeBeforeCreate = nodeRepository.findAll().size();
    // Create the Node
    restNodeMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isCreated());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeCreate + 1);
    Node testNode = nodeList.get(nodeList.size() - 1);
    assertThat(testNode.getName()).isEqualTo(DEFAULT_NAME);
  }

  @Test
  @Transactional
  void createNodeWithExistingId() throws Exception {
    // Create the Node with an existing ID
    node.setId(1L);

    int databaseSizeBeforeCreate = nodeRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restNodeMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void checkNameIsRequired() throws Exception {
    int databaseSizeBeforeTest = nodeRepository.findAll().size();
    // set the field null
    node.setName(null);

    // Create the Node, which fails.

    restNodeMockMvc
        .perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void getAllNodes() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList
    restNodeMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
  }

  @Test
  @Transactional
  void getNode() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get the node
    restNodeMockMvc
        .perform(get(ENTITY_API_URL_ID, node.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(node.getId().intValue()))
        .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
  }

  @Test
  @Transactional
  void getNodesByIdFiltering() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    Long id = node.getId();

    defaultNodeShouldBeFound("id.equals=" + id);
    defaultNodeShouldNotBeFound("id.notEquals=" + id);

    defaultNodeShouldBeFound("id.greaterThanOrEqual=" + id);
    defaultNodeShouldNotBeFound("id.greaterThan=" + id);

    defaultNodeShouldBeFound("id.lessThanOrEqual=" + id);
    defaultNodeShouldNotBeFound("id.lessThan=" + id);
  }

  @Test
  @Transactional
  void getAllNodesByNameIsEqualToSomething() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList where name equals to DEFAULT_NAME
    defaultNodeShouldBeFound("name.equals=" + DEFAULT_NAME);

    // Get all the nodeList where name equals to UPDATED_NAME
    defaultNodeShouldNotBeFound("name.equals=" + UPDATED_NAME);
  }

  @Test
  @Transactional
  void getAllNodesByNameIsInShouldWork() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList where name in DEFAULT_NAME or UPDATED_NAME
    defaultNodeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

    // Get all the nodeList where name equals to UPDATED_NAME
    defaultNodeShouldNotBeFound("name.in=" + UPDATED_NAME);
  }

  @Test
  @Transactional
  void getAllNodesByNameIsNullOrNotNull() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList where name is not null
    defaultNodeShouldBeFound("name.specified=true");

    // Get all the nodeList where name is null
    defaultNodeShouldNotBeFound("name.specified=false");
  }

  @Test
  @Transactional
  void getAllNodesByNameContainsSomething() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList where name contains DEFAULT_NAME
    defaultNodeShouldBeFound("name.contains=" + DEFAULT_NAME);

    // Get all the nodeList where name contains UPDATED_NAME
    defaultNodeShouldNotBeFound("name.contains=" + UPDATED_NAME);
  }

  @Test
  @Transactional
  void getAllNodesByNameNotContainsSomething() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    // Get all the nodeList where name does not contain DEFAULT_NAME
    defaultNodeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

    // Get all the nodeList where name does not contain UPDATED_NAME
    defaultNodeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
  }

  /** Executes the search, and checks that the default entity is returned. */
  private void defaultNodeShouldBeFound(String filter) throws Exception {
    restNodeMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

    // Check, that the count call also returns 1
    restNodeMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("1"));
  }

  /** Executes the search, and checks that the default entity is not returned. */
  private void defaultNodeShouldNotBeFound(String filter) throws Exception {
    restNodeMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    // Check, that the count call also returns 0
    restNodeMockMvc
        .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("0"));
  }

  @Test
  @Transactional
  void getNonExistingNode() throws Exception {
    // Get the node
    restNodeMockMvc
        .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingNode() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

    // Update the node
    Node updatedNode = nodeRepository.findById(node.getId()).orElseThrow();
    // Disconnect from session so that the updates on updatedNode are not directly saved in db
    em.detach(updatedNode);
    updatedNode.name(UPDATED_NAME);

    restNodeMockMvc
        .perform(
            put(ENTITY_API_URL_ID, updatedNode.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedNode)))
        .andExpect(status().isOk());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
    Node testNode = nodeList.get(nodeList.size() - 1);
    assertThat(testNode.getName()).isEqualTo(UPDATED_NAME);
  }

  @Test
  @Transactional
  void putNonExistingNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            put(ENTITY_API_URL_ID, node.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateNodeWithPatch() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

    // Update the node using partial update
    Node partialUpdatedNode = new Node();
    partialUpdatedNode.setId(node.getId());

    restNodeMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNode)))
        .andExpect(status().isOk());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
    Node testNode = nodeList.get(nodeList.size() - 1);
    assertThat(testNode.getName()).isEqualTo(DEFAULT_NAME);
  }

  @Test
  @Transactional
  void fullUpdateNodeWithPatch() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

    // Update the node using partial update
    Node partialUpdatedNode = new Node();
    partialUpdatedNode.setId(node.getId());

    partialUpdatedNode.name(UPDATED_NAME);

    restNodeMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNode)))
        .andExpect(status().isOk());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
    Node testNode = nodeList.get(nodeList.size() - 1);
    assertThat(testNode.getName()).isEqualTo(UPDATED_NAME);
  }

  @Test
  @Transactional
  void patchNonExistingNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, node.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isBadRequest());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamNode() throws Exception {
    int databaseSizeBeforeUpdate = nodeRepository.findAll().size();
    node.setId(longCount.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeMockMvc
        .perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(node)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Node in the database
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteNode() throws Exception {
    // Initialize the database
    nodeRepository.saveAndFlush(node);

    int databaseSizeBeforeDelete = nodeRepository.findAll().size();

    // Delete the node
    restNodeMockMvc
        .perform(delete(ENTITY_API_URL_ID, node.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Node> nodeList = nodeRepository.findAll();
    assertThat(nodeList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
