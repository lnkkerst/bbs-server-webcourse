package me.lnkkerst.webblogapi.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.repository.NodeRepository;
import me.lnkkerst.webblogapi.service.NodeQueryService;
import me.lnkkerst.webblogapi.service.NodeService;
import me.lnkkerst.webblogapi.service.criteria.NodeCriteria;
import me.lnkkerst.webblogapi.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/** REST controller for managing {@link me.lnkkerst.webblogapi.domain.Node}. */
@RestController
@RequestMapping("/api/nodes")
public class NodeResource {

  private final Logger log = LoggerFactory.getLogger(NodeResource.class);

  private static final String ENTITY_NAME = "node";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final NodeService nodeService;

  private final NodeRepository nodeRepository;

  private final NodeQueryService nodeQueryService;

  public NodeResource(
      NodeService nodeService, NodeRepository nodeRepository, NodeQueryService nodeQueryService) {
    this.nodeService = nodeService;
    this.nodeRepository = nodeRepository;
    this.nodeQueryService = nodeQueryService;
  }

  /**
   * {@code POST /nodes} : Create a new node.
   *
   * @param node the node to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     node, or with status {@code 400 (Bad Request)} if the node has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<Node> createNode(@Valid @RequestBody Node node) throws URISyntaxException {
    log.debug("REST request to save Node : {}", node);
    if (node.getId() != null) {
      throw new BadRequestAlertException(
          "A new node cannot already have an ID", ENTITY_NAME, "idexists");
    }
    Node result = nodeService.save(node);
    return ResponseEntity.created(new URI("/api/nodes/" + result.getId()))
        .headers(
            HeaderUtil.createEntityCreationAlert(
                applicationName, false, ENTITY_NAME, result.getId().toString()))
        .body(result);
  }

  /**
   * {@code PUT /nodes/:id} : Updates an existing node.
   *
   * @param id the id of the node to save.
   * @param node the node to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated node,
   *     or with status {@code 400 (Bad Request)} if the node is not valid, or with status {@code
   *     500 (Internal Server Error)} if the node couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Node> updateNode(
      @PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Node node)
      throws URISyntaxException {
    log.debug("REST request to update Node : {}, {}", id, node);
    if (node.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, node.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!nodeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Node result = nodeService.update(node);
    return ResponseEntity.ok()
        .headers(
            HeaderUtil.createEntityUpdateAlert(
                applicationName, false, ENTITY_NAME, node.getId().toString()))
        .body(result);
  }

  /**
   * {@code PATCH /nodes/:id} : Partial updates given fields of an existing node, field will ignore
   * if it is null
   *
   * @param id the id of the node to save.
   * @param node the node to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated node,
   *     or with status {@code 400 (Bad Request)} if the node is not valid, or with status {@code
   *     404 (Not Found)} if the node is not found, or with status {@code 500 (Internal Server
   *     Error)} if the node couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
      value = "/{id}",
      consumes = {"application/json", "application/merge-patch+json"})
  public ResponseEntity<Node> partialUpdateNode(
      @PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Node node)
      throws URISyntaxException {
    log.debug("REST request to partial update Node partially : {}, {}", id, node);
    if (node.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, node.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!nodeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<Node> result = nodeService.partialUpdate(node);

    return ResponseUtil.wrapOrNotFound(
        result,
        HeaderUtil.createEntityUpdateAlert(
            applicationName, false, ENTITY_NAME, node.getId().toString()));
  }

  /**
   * {@code GET /nodes} : get all the nodes.
   *
   * @param pageable the pagination information.
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nodes in body.
   */
  @GetMapping("")
  public ResponseEntity<List<Node>> getAllNodes(
      NodeCriteria criteria, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get Nodes by criteria: {}", criteria);

    Page<Node> page = nodeQueryService.findByCriteria(criteria, pageable);
    HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET /nodes/count} : count all the nodes.
   *
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
   */
  @GetMapping("/count")
  public ResponseEntity<Long> countNodes(NodeCriteria criteria) {
    log.debug("REST request to count Nodes by criteria: {}", criteria);
    return ResponseEntity.ok().body(nodeQueryService.countByCriteria(criteria));
  }

  /**
   * {@code GET /nodes/:id} : get the "id" node.
   *
   * @param id the id of the node to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the node, or with
   *     status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Node> getNode(@PathVariable("id") Long id) {
    log.debug("REST request to get Node : {}", id);
    Optional<Node> node = nodeService.findOne(id);
    return ResponseUtil.wrapOrNotFound(node);
  }

  /**
   * {@code DELETE /nodes/:id} : delete the "id" node.
   *
   * @param id the id of the node to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNode(@PathVariable("id") Long id) {
    log.debug("REST request to delete Node : {}", id);
    nodeService.delete(id);
    return ResponseEntity.noContent()
        .headers(
            HeaderUtil.createEntityDeletionAlert(
                applicationName, false, ENTITY_NAME, id.toString()))
        .build();
  }
}
