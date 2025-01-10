package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(Teacher.builder()
                        .id(1L)
                        .lastName("Doe")
                        .firstName("John")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
        teachers.add(Teacher.builder()
                        .id(2L)
                        .lastName("Smith")
                        .firstName("Jane")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> result = teacherService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsAll(teachers);
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoTeachers() {
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        List<Teacher> result = teacherService.findAll();

        assertThat(result).isEmpty();
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnTeacherById() {
        Long teacherId = 1L;
        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(teacherId);

        assertThat(result).isEqualTo(teacher);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    public void shouldReturnNullWhenTeacherDoesNotExist() {
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());
        Teacher result = teacherService.findById(teacherId);
        assertThat(result).isNull();
        verify(teacherRepository, times(1)).findById(teacherId);
    }

}
