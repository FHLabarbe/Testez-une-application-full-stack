package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapToEntityAndDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("john@example.com");
        dto.setLastName("Doe");
        dto.setFirstName("John");
        dto.setAdmin(true);
        dto.setPassword("password");

        User entity = userMapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getEmail()).isEqualTo("john@example.com");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getPassword()).isEqualTo("password");

        UserDto dto2 = userMapper.toDto(entity);
        assertThat(dto2.getId()).isEqualTo(1L);
        assertThat(dto2.getEmail()).isEqualTo("john@example.com");
        assertThat(dto2.getLastName()).isEqualTo("Doe");
        assertThat(dto2.getFirstName()).isEqualTo("John");
        assertThat(dto2.getPassword()).isEqualTo("password");
    }
}
