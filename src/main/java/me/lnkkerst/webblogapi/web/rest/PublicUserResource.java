package me.lnkkerst.webblogapi.web.rest;

import java.util.*;
import java.util.Collections;
import me.lnkkerst.webblogapi.domain.User;
import me.lnkkerst.webblogapi.service.UserService;
import me.lnkkerst.webblogapi.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

  private static final List<String> ALLOWED_ORDERED_PROPERTIES =
      Collections.unmodifiableList(
          Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey"));

  private final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

  private final UserService userService;

  public PublicUserResource(UserService userService) {
    this.userService = userService;
  }

  /**
   * {@code GET /users} : get all users with only public information - calling this method is
   * allowed for anyone.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
   */
  @GetMapping("/public-users")
  public ResponseEntity<List<UserDTO>> getAllPublicUsers(
      @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get all public User names");
    if (!onlyContainsAllowedProperties(pageable)) {
      return ResponseEntity.badRequest().build();
    }

    final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
    HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  /**
   * {@code GET /users} : get all users with all information - calling this method is allowed for
   * anyone.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers(
      @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get all public User names");
    if (!onlyContainsAllowedProperties(pageable)) {
      return ResponseEntity.badRequest().build();
    }

    final Page<User> page = userService.getAllUsers(pageable);
    HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  private boolean onlyContainsAllowedProperties(Pageable pageable) {
    return pageable.getSort().stream()
        .map(Sort.Order::getProperty)
        .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
  }

  /**
   * {@code GET /users/:login} : get the "login" user.
   *
   * @param login the login of the user to find.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user,
   *     or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/users-by-login/{login}")
  public ResponseEntity<User> getUserByLogin(@PathVariable("login") String login) {
    log.debug("REST request to get User : {}", login);
    return ResponseUtil.wrapOrNotFound(userService.getUserByLogin(login));
  }

  /**
   * {@code GET /users/:id} : get the user by given id.
   *
   * @param id the id of the user to find.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user,
   *     or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
    log.debug("REST request to get User : {}", id);
    return ResponseUtil.wrapOrNotFound(userService.getUserById(id));
  }

  /**
   * Gets a list of all roles.
   *
   * @return a string list of all roles.
   */
  @GetMapping("/authorities")
  public List<String> getAuthorities() {
    return userService.getAuthorities();
  }
}
