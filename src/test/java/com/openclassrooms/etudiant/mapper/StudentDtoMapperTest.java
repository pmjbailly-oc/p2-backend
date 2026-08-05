package com.openclassrooms.etudiant.mapper;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentDtoMapperTest {

    private final StudentDtoMapper studentDtoMapper = new StudentDtoMapperImpl();

    @Test
    public void test_toEntity_maps_required_fields() {
        // GIVEN
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setFirstName("Marie");
        requestDTO.setLastName("Durand");
        requestDTO.setEmail("marie.durand@example.com");

        // WHEN
        Student student = studentDtoMapper.toEntity(requestDTO);

        // THEN
        assertThat(student.getFirstName()).isEqualTo("Marie");
        assertThat(student.getLastName()).isEqualTo("Durand");
        assertThat(student.getEmail()).isEqualTo("marie.durand@example.com");
        // les champs gérés par la base ne doivent pas être mappés
        assertThat(student.getId()).isNull();
        assertThat(student.getCreated_at()).isNull();
        assertThat(student.getUpdated_at()).isNull();
    }

    @Test
    public void test_toResponseDTO_maps_all_fields() {
        // GIVEN
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 31, 16, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 8, 1, 10, 30);
        Student student = Student.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Durand")
                .email("marie.durand@example.com")
                .created_at(createdAt)
                .updated_at(updatedAt)
                .build();

        // WHEN
        StudentResponseDTO responseDTO = studentDtoMapper.toResponseDTO(student);

        // THEN
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getFirstName()).isEqualTo("Marie");
        assertThat(responseDTO.getLastName()).isEqualTo("Durand");
        assertThat(responseDTO.getEmail()).isEqualTo("marie.durand@example.com");
        assertThat(responseDTO.getCreatedAt()).isEqualTo(createdAt);
        assertThat(responseDTO.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
