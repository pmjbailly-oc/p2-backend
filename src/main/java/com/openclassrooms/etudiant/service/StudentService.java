package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDtoMapper studentDtoMapper;

    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        Assert.notNull(studentRequestDTO, "Student must not be null");
        Assert.hasText(studentRequestDTO.getEmail(), "Email must not be empty");
        log.info("Creating new student");

        checkEmailUniqueness(studentRequestDTO.getEmail(), null);

        Student student = studentDtoMapper.toEntity(studentRequestDTO);
        return studentDtoMapper.toResponseDTO(studentRepository.save(student));
    }

    public List<StudentResponseDTO> getAllStudents() {
        log.info("Retrieving all students");
        return studentRepository.findAll().stream()
                .map(studentDtoMapper::toResponseDTO)
                .toList();
    }

    public StudentResponseDTO getStudentById(Long id) {
        Assert.notNull(id, "Id must not be null");
        log.info("Retrieving student with id {}", id);
        return studentDtoMapper.toResponseDTO(findStudentById(id));
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO) {
        Assert.notNull(id, "Id must not be null");
        Assert.notNull(studentRequestDTO, "Student must not be null");
        log.info("Updating student with id {}", id);

        Student existingStudent = findStudentById(id);
        checkEmailUniqueness(studentRequestDTO.getEmail(), id);

        existingStudent.setFirstName(studentRequestDTO.getFirstName());
        existingStudent.setLastName(studentRequestDTO.getLastName());
        existingStudent.setEmail(studentRequestDTO.getEmail());

        return studentDtoMapper.toResponseDTO(studentRepository.save(existingStudent));
    }

    public void deleteStudent(Long id) {
        Assert.notNull(id, "Id must not be null");
        log.info("Deleting student with id {}", id);

        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    private Student findStudentById(Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }
        return optionalStudent.get();
    }

    private void checkEmailUniqueness(String email, Long currentId) {
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isPresent() && !optionalStudent.get().getId().equals(currentId)) {
            throw new IllegalArgumentException("Student with email " + email + " already exists");
        }
    }
}
