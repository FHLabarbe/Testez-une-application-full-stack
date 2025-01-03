package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateSession() {
        Session session = new Session();
        session.setName("Session de Test");

        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertThat(result).isEqualTo(session);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void shouldThrowExceptionWhenCreatingSessionWithMissingFields() {
        Session session = new Session();

        when(sessionRepository.save(session)).thenThrow(new BadRequestException());

        assertThrows(BadRequestException.class, () -> sessionService.create(session));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void shouldDeleteSessionById() {
        Long sessionId = 1L;

        sessionService.delete(sessionId);

        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    void shouldFindAllSessions() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(2);
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void shouldGetSessionById() {
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(sessionId);

        assertThat(result).isEqualTo(session);
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    void shouldUpdateSession() {
        Long sessionId = 1L;
        Session session = new Session();
        session.setName("Updated Session");

        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(sessionId, session);

        assertThat(result).isEqualTo(session);
        assertThat(session.getId()).isEqualTo(sessionId);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSessionWithMissingFields() {
        Long sessionId = 1L;
        Session session = new Session();

        when(sessionRepository.save(session)).thenThrow(new BadRequestException());

        assertThrows(BadRequestException.class, () -> sessionService.update(sessionId, session));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void shouldReturnNullWhenSessionDoesNotExist() {
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Session result = sessionService.getById(sessionId);

        assertThat(result).isNull();
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSessionNotFoundInParticipate() {
        Long sessionId = 10L;
        Long userId = 100L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(sessionRepository, userRepository);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundInParticipate() {
        Long sessionId = 10L;
        Long userId = 100L;
        Session session = new Session();
        session.setUsers(new ArrayList<>()); 
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(sessionRepository, userRepository);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUserAlreadyParticipates() {
        Long sessionId = 10L;
        Long userId = 100L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(new ArrayList<>(List.of(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // WHEN & THEN
        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(sessionRepository, userRepository);
    }   

    @Test
    void shouldAddUserWhenParticipateIsCalledWithValidData() {
        // GIVEN
        Long sessionId = 10L;
        Long userId = 100L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // WHEN
        sessionService.participate(sessionId, userId);

        // THEN
        verify(sessionRepository).save(session);
        assertThat(session.getUsers()).contains(user);
    }   


}
