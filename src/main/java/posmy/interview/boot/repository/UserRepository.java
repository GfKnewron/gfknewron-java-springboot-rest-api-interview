package posmy.interview.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import posmy.interview.boot.entity.User;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByDeletedFlag(boolean deletedFlag, Pageable pageable);
}
