package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testParticipateInSession_Success() {
        Session session = sessionRepository.findById(1L).orElseThrow(() -> new AssertionError("Session non trouvée"));
        User user = userRepository.findById(3L).orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));

        assertFalse(session.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));

        sessionService.participate(session.getId(), user.getId());

        Session updatedSession = sessionRepository.findById(session.getId())
                .orElseThrow(() -> new AssertionError("Session non trouvée après mise à jour"));
        assertTrue(updatedSession.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    @Test
    void testDatabaseInitialization() {
        assertTrue(sessionRepository.existsById(1L), "La session avec l'ID 1 n'existe pas !");
        assertTrue(userRepository.existsById(3L), "L'utilisateur avec l'ID 3 n'existe pas !");
    }
}
