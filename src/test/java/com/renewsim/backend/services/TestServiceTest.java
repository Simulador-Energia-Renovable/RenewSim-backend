package com.renewsim.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.repositories.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class TestServiceTest {

    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private TestService testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllTests() {
        when(testRepository.findAll()).thenReturn(List.of(new TestEntity(1L, "Test Name")));
        List<TestEntity> result = testService.getAllTests();
        assertEquals(1, result.size());
        assertEquals("Test Name", result.get(0).getName());
    }

    @Test
    void shouldCreateNewTest() {
        TestEntity newTest = new TestEntity(null, "New Test");
        when(testRepository.save(any(TestEntity.class))).thenReturn(new TestEntity(2L, "New Test"));
        TestEntity result = testService.createTest("New Test");
        assertNotNull(result.getId());
        assertEquals("New Test", result.getName());
    }
}
