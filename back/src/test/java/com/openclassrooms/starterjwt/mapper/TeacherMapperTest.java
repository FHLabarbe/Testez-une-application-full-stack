package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@ExtendWith(MockitoExtension.class)
class TeacherMapperTest {

    private TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    void shouldMapToEntityAndDto() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setLastName("Doe");
        dto.setFirstName("John");

        Teacher entity = teacherMapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getFirstName()).isEqualTo("John");

        TeacherDto dto2 = teacherMapper.toDto(entity);
        assertThat(dto2.getId()).isEqualTo(1L);
        assertThat(dto2.getLastName()).isEqualTo("Doe");
        assertThat(dto2.getFirstName()).isEqualTo("John");
    }
}
