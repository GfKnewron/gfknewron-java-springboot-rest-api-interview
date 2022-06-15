package posmy.interview.boot;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import posmy.interview.boot.dto.UserDTO;
import posmy.interview.boot.entity.Account;
import posmy.interview.boot.entity.User;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DtoUnitTest {
    @Test
    void toDto() {
        ModelMapper modelMapper = new ModelMapper();

        User user = new User();
        user.setAccount(new Account(BigDecimal.ONE));

        UserDTO dto = modelMapper.map(user, UserDTO.class);
        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.isBlockedFlag(), user.isBlockedFlag());
    }
}
