package me.lnkkerst.webblogapi.service;

import java.util.Optional;
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service Implementation for managing {@link me.lnkkerst.webblogapi.domain.Node}. */
@Service
@Transactional
public class NodeService {

  private final Logger log = LoggerFactory.getLogger(NodeService.class);

  private final NodeRepository nodeRepository;

  public NodeService(NodeRepository nodeRepository) {
    this.nodeRepository = nodeRepository;
  }

  /**
   * Save a node.
   *
   * @param node the entity to save.
   * @return the persisted entity.
   */
  public Node save(Node node) {
    log.debug("Request to save Node : {}", node);
    return nodeRepository.save(node);
  }

  /**
   * Update a node.
   *
   * @param node the entity to save.
   * @return the persisted entity.
   */
  public Node update(Node node) {
    log.debug("Request to update Node : {}", node);
    return nodeRepository.save(node);
  }

  /**
   * Partially update a node.
   *
   * @param node the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<Node> partialUpdate(Node node) {
    log.debug("Request to partially update Node : {}", node);

    return nodeRepository
        .findById(node.getId())
        .map(
            existingNode -> {
              if (node.getName() != null) {
                existingNode.setName(node.getName());
              }

              return existingNode;
            })
        .map(nodeRepository::save);
  }

  /**
   * Get all the nodes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<Node> findAll(Pageable pageable) {
    log.debug("Request to get all Nodes");
    return nodeRepository.findAll(pageable);
  }

  /**
   * Get one node by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<Node> findOne(Long id) {
    log.debug("Request to get Node : {}", id);
    return nodeRepository.findById(id);
  }

  /**
   * Delete the node by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Node : {}", id);
    nodeRepository.deleteById(id);
  }
}
