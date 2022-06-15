package posmy.interview.boot.controller;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.dto.UserDTO;
import posmy.interview.boot.entity.User;
import posmy.interview.boot.repository.UserRepository;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/manage/users"})
@RolesAllowed("BANK_MANAGER")
@Log4j2
public class ManageController {

    private static final String NEW_USER_LOG = "New User was created id:{}";
    private static final String LIST_LOG = "Fetched {} users";
    private static final String UPDATE_LOG = "Update user id:{}";
    private static final String BLOCK_LOG = "Blocked user id:{}";
    private static final String UNBLOCK_LOG = "Unblocked user id:{}";
    private static final String DELETE_LOG = "Delete user id:{}";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping(path = "create")
    public ResponseEntity<Long> createUser() {
        User createdUser = userRepository.save(new User());
        log.info(NEW_USER_LOG, createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser.getId());
    }

    @GetMapping(path = "page", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> pageUsers(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "10") int limit) {
        Pageable pageble = PageRequest.of(start, limit);
        List<User> users = userRepository.findAllByDeletedFlag(false, pageble).getContent();

        log.info(LIST_LOG, users.size());
        return ResponseEntity.status(HttpStatus.OK).body(users.stream().map(this::toDto).toList());
    }

    @PutMapping(path = "update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUser(@RequestBody UserDTO userDTO) {
        log.info(UPDATE_LOG, userDTO.getId());
        UnaryOperator<User> function = (user) -> {
            user.setBlockedFlag(userDTO.isBlockedFlag());
            return user;
        };
        return patchUser(userDTO.getId(), function);
    }

    @PostMapping(path = "block/{userId}")
    public ResponseEntity<Void> blockUser(@PathVariable(value = "userId") Long userId) {
        log.info(BLOCK_LOG, userId);
        UnaryOperator<User> function = (user) -> {
            user.setBlockedFlag(true);
            return user;
        };
        return patchUser(userId, function);
    }

    @PostMapping(path = "unblock/{userId}")
    public ResponseEntity<Void> unblockUser(@PathVariable(value = "userId") Long userId) {
        log.info(UNBLOCK_LOG, userId);
        UnaryOperator<User> function = (user) -> {
            user.setBlockedFlag(false);
            return user;
        };
        return patchUser(userId, function);
    }

    @PostMapping(path = "delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info(DELETE_LOG, userId);
        UnaryOperator<User> function = (user) -> {
            user.setDeletedFlag(true);
            return user;
        };
        return patchUser(userId, function);
    }


    private ResponseEntity<Void> patchUser(Long userId, UnaryOperator<User> function) {
        Optional<User> userFound = userRepository.findById(userId);

        if (userFound.isPresent()) {
            User user = userFound.get();
            userRepository.save(function.apply(user));
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
    }

    private UserDTO toDto(User entity) {
        return modelMapper.map(entity, UserDTO.class);
    }
}
