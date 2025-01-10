package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @Test
    void shouldMapToEntityWithTeacherAndUsers() {
        SessionDto dto = new SessionDto();
        dto.setTeacher_id(1L);
        dto.setUsers(Arrays.asList(10L, 20L));

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(10L)).thenReturn(user1);
        when(userService.findById(20L)).thenReturn(user2);

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void shouldMapToDtoWithTeacherAndUsers() {
        Session session = new Session();
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        session.setTeacher(teacher);
        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);
        session.setUsers(Arrays.asList(user1, user2));

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getTeacher_id()).isEqualTo(1L);
        assertThat(dto.getUsers()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void shouldHandleNullTeacherIdAndNullUsersInToEntity() {
        SessionDto dto = new SessionDto();
        dto.setTeacher_id(null);
        dto.setUsers(null);

        Session session = sessionMapper.toEntity(dto);
        assertThat(session.getTeacher()).isNull();
        assertThat(session.getUsers()).isEmpty();
    }
}
