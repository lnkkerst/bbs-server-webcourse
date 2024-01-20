package me.lnkkerst.webblogapi.service;

import java.util.List;
import me.lnkkerst.webblogapi.domain.*; // for static metamodels
import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.repository.NodeRepository;
import me.lnkkerst.webblogapi.service.criteria.NodeCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Node} entities in the database. The main input
 * is a {@link NodeCriteria} which gets converted to {@link Specification}, in a way that all the
 * filters must apply. It returns a {@link List} of {@link Node} or a {@link Page} of {@link Node}
 * which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NodeQueryService extends QueryService<Node> {

  private final Logger log = LoggerFactory.getLogger(NodeQueryService.class);

  private final NodeRepository nodeRepository;

  public NodeQueryService(NodeRepository nodeRepository) {
    this.nodeRepository = nodeRepository;
  }

  /**
   * Return a {@link List} of {@link Node} which matches the criteria from the database.
   *
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<Node> findByCriteria(NodeCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<Node> specification = createSpecification(criteria);
    return nodeRepository.findAll(specification);
  }

  /**
   * Return a {@link Page} of {@link Node} which matches the criteria from the database.
   *
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<Node> findByCriteria(NodeCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Node> specification = createSpecification(criteria);
    return nodeRepository.findAll(specification, page);
  }

  /**
   * Return the number of matching entities in the database.
   *
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(NodeCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Node> specification = createSpecification(criteria);
    return nodeRepository.count(specification);
  }

  /**
   * Function to convert {@link NodeCriteria} to a {@link Specification}
   *
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Node> createSpecification(NodeCriteria criteria) {
    Specification<Node> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Node_.id));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), Node_.name));
      }
    }
    return specification;
  }
}
