package posmy.interview.boot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/manage/users"})
@RolesAllowed("BANK_MANAGER")
@Log4j2
public class ManageController {

    private static final String NEW_USER_LOG = "New User was created id:{}";
    private static final String LIST_LOG = "Fetched {} users";
    private static final String UPDATE_LOG = "Update user id:{}";
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "create")
    public ResponseEntity<Long> createUser() {
        User createdUser = userRepository.save(new User());
        log.info(NEW_USER_LOG, createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser.getId());
    }

    @GetMapping(path = "page", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> pageUsers(@RequestParam(defaultValue = "0") Integer start, @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageble = PageRequest.of(start, limit);
        List<User> users = userRepository.findAll(pageble).getContent();
        log.info(LIST_LOG, users.size());
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping(path = "update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        if (userRepository.existsById(user.getId())) {
            userRepository.save(user);
            log.info(UPDATE_LOG, user.getId());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
    }
}
