package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.User;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserListDTO {

    private long totalUsers;
    private int totalPages;
    private List<UserDTO> users;

    public UserListDTO(Page<User> userPage) {
        totalUsers = userPage.getTotalElements();
        totalPages = userPage.getTotalPages();
        users = userPage.stream().map(UserDTO::new).collect(Collectors.toList());
    }

}
