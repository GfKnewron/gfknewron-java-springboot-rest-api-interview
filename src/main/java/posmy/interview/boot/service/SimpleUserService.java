package posmy.interview.boot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import posmy.interview.boot.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;


@Service
public class SimpleUserService implements AccountService {

    @Autowired
    UserRepository userRepository;

    @Override
    public void deposit(BigDecimal amount) {
        userRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
    }
}
