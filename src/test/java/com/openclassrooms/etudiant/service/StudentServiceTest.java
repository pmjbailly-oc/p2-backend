package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Marie";
    private static final String LAST_NAME = "Durand";
    private static final String EMAIL = "marie.durand@example.com";

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentDtoMapper studentDtoMapper;
    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO buildRequestDTO() {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        return dto;
    }

    private Student buildStudent() {
        return Student.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .created_at(LocalDateTime.of(2026, 7, 31, 16, 0))
                .updated_at(LocalDateTime.of(2026, 7, 31, 16, 0))
                .build();
    }

    private StudentResponseDTO buildResponseDTO() {
        return new StudentResponseDTO(ID, FIRST_NAME, LAST_NAME, EMAIL,
                LocalDateTime.of(2026, 7, 31, 16, 0), LocalDateTime.of(2026, 7, 31, 16, 0));
    }

    @Test
    public void test_create_student_null_throws_IllegalArgumentException() {
        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.createStudent(null));
    }

    @Test
    public void test_create_student_email_already_exists_throws_IllegalArgumentException() {
        // GIVEN
        Student existingStudent = buildStudent();
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingStudent));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.createStudent(buildRequestDTO()));
        // le repository ne doit jamais être appelé pour sauvegarder
        verify(studentRepository, never()).save(any());
    }

    @Test
    public void test_create_student_success() {
        // GIVEN
        Student studentToSave = buildStudent();
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(studentDtoMapper.toEntity(any(StudentRequestDTO.class))).thenReturn(studentToSave);
        when(studentRepository.save(studentToSave)).thenReturn(studentToSave);
        when(studentDtoMapper.toResponseDTO(studentToSave)).thenReturn(buildResponseDTO());

        // WHEN
        StudentResponseDTO result = studentService.createStudent(buildRequestDTO());

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(studentToSave);
        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void test_get_all_students_empty_list() {
        // GIVEN
        when(studentRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<StudentResponseDTO> result = studentService.getAllStudents();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    public void test_get_all_students_with_multiple_students() {
        // GIVEN
        Student student1 = buildStudent();
        Student student2 = Student.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean.dupont@example.com")
                .build();
        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));
        when(studentDtoMapper.toResponseDTO(student1)).thenReturn(buildResponseDTO());
        when(studentDtoMapper.toResponseDTO(student2))
                .thenReturn(new StudentResponseDTO(2L, "Jean", "Dupont", "jean.dupont@example.com", null, null));

        // WHEN
        List<StudentResponseDTO> result = studentService.getAllStudents();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    public void test_get_student_by_id_success() {
        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findById(ID)).thenReturn(Optional.of(student));
        when(studentDtoMapper.toResponseDTO(student)).thenReturn(buildResponseDTO());

        // WHEN
        StudentResponseDTO result = studentService.getStudentById(ID);

        // THEN
        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void test_get_student_by_id_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.getStudentById(ID));
    }

    @Test
    public void test_update_student_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(ID, buildRequestDTO()));
    }

    @Test
    public void test_update_student_email_already_used_by_another_throws_IllegalArgumentException() {
        // GIVEN
        Student existingStudent = buildStudent();
        Student otherStudent = Student.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Dupont")
                .email(EMAIL)
                .build();
        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(otherStudent));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(ID, buildRequestDTO()));
    }

    @Test
    public void test_update_student_success() {
        // GIVEN
        Student existingStudent = buildStudent();
        StudentRequestDTO requestDTO = buildRequestDTO();
        requestDTO.setFirstName("Marie-Anne");

        when(studentRepository.findById(ID)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(existingStudent)).thenReturn(existingStudent);

        StudentResponseDTO responseDTO = buildResponseDTO();
        responseDTO.setFirstName("Marie-Anne");
        when(studentDtoMapper.toResponseDTO(existingStudent)).thenReturn(responseDTO);

        // WHEN
        StudentResponseDTO result = studentService.updateStudent(ID, requestDTO);

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getFirstName()).isEqualTo("Marie-Anne");
        assertThat(result.getFirstName()).isEqualTo("Marie-Anne");
    }

    @Test
    public void test_delete_student_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.deleteStudent(ID));
    }

    @Test
    public void test_delete_student_success() {
        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findById(ID)).thenReturn(Optional.of(student));

        // WHEN
        studentService.deleteStudent(ID);

        // THEN
        verify(studentRepository).delete(student);
    }
}
