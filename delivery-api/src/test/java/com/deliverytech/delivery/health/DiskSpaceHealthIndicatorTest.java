package com.deliverytech.delivery.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiskSpaceHealthIndicatorTest {

    @Test
    void shouldReturnUpWhenFreeSpaceAboveThreshold() {
        File mockFile = mock(File.class);
        when(mockFile.getFreeSpace()).thenReturn(200L * 1024 * 1024); // 200 MB

        DiskSpaceHealthIndicator indicator = new DiskSpaceHealthIndicator(mockFile);
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(200L * 1024 * 1024, health.getDetails().get("freeSpace"));
    }

    @Test
    void shouldReturnDownWhenFreeSpaceBelowThreshold() {
        File mockFile = mock(File.class);
        when(mockFile.getFreeSpace()).thenReturn(50L * 1024 * 1024); // 50 MB

        DiskSpaceHealthIndicator indicator = new DiskSpaceHealthIndicator(mockFile);
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(50L * 1024 * 1024, health.getDetails().get("freeSpace"));
        assertEquals(100L * 1024 * 1024, health.getDetails().get("threshold"));
    }
}
