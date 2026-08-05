package com.openclassrooms.etudiant.mapper;

import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoMapperTest {

    private final UserDtoMapper userDtoMapper = new UserDtoMapperImpl();

    @Test
    public void test_toEntity_maps_required_fields() {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName("John");
        registerDTO.setLastName("Doe");
        registerDTO.setLogin("jdoe");
        registerDTO.setPassword("password");

        // WHEN
        User user = userDtoMapper.toEntity(registerDTO);

        // THEN
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getLogin()).isEqualTo("jdoe");
        assertThat(user.getPassword()).isEqualTo("password");
        // les champs gérés par la base ou Spring Security ne doivent pas être mappés
        assertThat(user.getId()).isNull();
        assertThat(user.getCreated_at()).isNull();
        assertThat(user.getUpdated_at()).isNull();
    }
}
