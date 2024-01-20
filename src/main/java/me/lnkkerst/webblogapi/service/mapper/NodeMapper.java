package me.lnkkerst.webblogapi.service.mapper;

import me.lnkkerst.webblogapi.domain.Node;
import me.lnkkerst.webblogapi.service.dto.NodeDTO;
import org.mapstruct.*;

/** Mapper for the entity {@link Node} and its DTO {@link NodeDTO}. */
@Mapper(componentModel = "spring")
public interface NodeMapper extends EntityMapper<NodeDTO, Node> {}
