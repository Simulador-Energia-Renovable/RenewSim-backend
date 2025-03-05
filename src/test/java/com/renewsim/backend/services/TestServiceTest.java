package com.renewsim.backend.services;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.repositories.TestRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServiceTest {

    private final TestRepository testRepository = mock(TestRepository.class);
    private final TestService testService = new TestService(testRepository);

    @Test
    void testCreateTest() {
        String message = "Test Message";
        TestEntity testEntity = new TestEntity(message);

        when(testRepository.save(any(TestEntity.class))).thenReturn(testEntity);

        TestEntity result = testService.createTest(message);

        assertNotNull(result);
        assertEquals(message, result.getMessage());
    }

    @Test
    void testGetAllTests() {
        when(testRepository.findAll()).thenReturn(List.of(new TestEntity("Message 1"), new TestEntity("Message 2")));

        List<TestEntity> result = testService.getAllTests();

        assertEquals(2, result.size());
    }
}
