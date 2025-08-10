package com.anpilogoff.audioDataService.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class JsonStreamingUtilsTest {

    @Test
    void testCheckTracksItemsNotEmpty_emptyArray() {
        String json = "{\"tracks\":{\"items\":[]}}";
        assertFalse(JsonStreamingUtils.checkTracksItemsNotEmpty(json));
    }

    @Test
    void testCheckTracksItemsNotEmpty_nonEmptyArray() {
        String json = "{\"tracks\":{\"items\":[{\"id\":1}]}}";
        assertTrue(JsonStreamingUtils.checkTracksItemsNotEmpty(json));
    }

    @Test
    void testHasTrackFullDuration_durationGreaterThan30() {
        String json = "{\"duration\":45}";
        assertTrue(JsonStreamingUtils.hasTrackFullDuration(json));
    }

    @Test
    void testHasTrackFullDuration_durationLessEqual30() {
        String json = "{\"duration\":30}";
        assertFalse(JsonStreamingUtils.hasTrackFullDuration(json));
    }
}
