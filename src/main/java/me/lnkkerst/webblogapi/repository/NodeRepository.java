package me.lnkkerst.webblogapi.repository;

import me.lnkkerst.webblogapi.domain.Node;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Node entity. */
@SuppressWarnings("unused")
@Repository
public interface NodeRepository extends JpaRepository<Node, Long>, JpaSpecificationExecutor<Node> {}
