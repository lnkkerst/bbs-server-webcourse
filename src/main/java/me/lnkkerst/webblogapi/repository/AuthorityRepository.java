package me.lnkkerst.webblogapi.repository;

import me.lnkkerst.webblogapi.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the {@link Authority} entity. */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
