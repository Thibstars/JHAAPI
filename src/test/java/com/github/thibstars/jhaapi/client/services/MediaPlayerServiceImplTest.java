package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link MediaPlayerServiceImpl}.
 *
 * @author Thibault Helsmoortel
 */
class MediaPlayerServiceImplTest {

    private ServiceService serviceService;
    private StatesService statesService;
    private ObjectMapper objectMapper;
    private MediaPlayerServiceImpl mediaPlayerService;

    @BeforeEach
    void setUp() {
        Configuration configuration = Mockito.mock(Configuration.class);
        serviceService = Mockito.mock(ServiceService.class);
        statesService = Mockito.mock(StatesService.class);
        objectMapper = Mockito.mock(ObjectMapper.class);

        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        mediaPlayerService = new MediaPlayerServiceImpl(configuration, serviceService, statesService);
    }

    @Test
    void shouldToggleFromOnToOff() throws JsonProcessingException {
        String entityName = "living_room_tv";
        String entityId = "media_player.living_room_tv";

        State onState = Mockito.mock(State.class);
        Mockito.when(onState.state()).thenReturn("on");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(onState));
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        mediaPlayerService.toggle(entityName);

        Mockito.verify(serviceService).callService("media_player", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("media_player", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldToggleFromOffToOn() throws JsonProcessingException {
        String entityName = "living_room_tv";
        String entityId = "media_player.living_room_tv";

        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(offState));
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        mediaPlayerService.toggle("media_player." + entityName);

        Mockito.verify(serviceService).callService("media_player", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("media_player", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldTurnOnWhenNoStateFoundOnToggle() throws JsonProcessingException {
        String entityName = "living_room_tv";
        String entityId = "media_player.living_room_tv";

        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.empty());
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        mediaPlayerService.toggle(entityName);

        Mockito.verify(serviceService).callService("media_player", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldTurnOn() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.turnOn("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "turn_on", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldTurnOff() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.turnOff("media_player.living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "turn_off", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldPlay() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.play("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_play", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldPause() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.pause("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_pause", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldPlayPause() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.playPause("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_play_pause", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldStop() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.stop("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_stop", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldNextTrack() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.nextTrack("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_next_track", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldPreviousTrack() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.previousTrack("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "media_previous_track", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldVolumeUp() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.volumeUp("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "volume_up", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldVolumeDown() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.volumeDown("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "volume_down", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldSetVolume() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"volume_level\":0.75}");

        mediaPlayerService.setVolume("living_room_tv", 0.75);

        Mockito.verify(serviceService).callService("media_player", "volume_set", "{\"entity_id\":\"media_player.living_room_tv\",\"volume_level\":0.75}");
    }

    @Test
    void shouldMuteVolume() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"is_volume_muted\":true}");

        mediaPlayerService.muteVolume("living_room_tv", true);

        Mockito.verify(serviceService).callService("media_player", "volume_mute", "{\"entity_id\":\"media_player.living_room_tv\",\"is_volume_muted\":true}");
    }

    @Test
    void shouldSeek() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"seek_position\":125.5}");

        mediaPlayerService.seek("living_room_tv", 125.5);

        Mockito.verify(serviceService).callService("media_player", "media_seek", "{\"entity_id\":\"media_player.living_room_tv\",\"seek_position\":125.5}");
    }

    @Test
    void shouldPlayMediaWithoutEnqueue() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"media_content_id\":\"http://example.com/song.mp3\",\"media_content_type\":\"music\"}");

        mediaPlayerService.playMedia("living_room_tv", "http://example.com/song.mp3", "music");

        Mockito.verify(serviceService).callService("media_player", "play_media", "{\"entity_id\":\"media_player.living_room_tv\",\"media_content_id\":\"http://example.com/song.mp3\",\"media_content_type\":\"music\"}");
    }

    @Test
    void shouldPlayMediaWithEnqueue() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"media_content_id\":\"http://example.com/song.mp3\",\"media_content_type\":\"music\",\"enqueue\":\"next\"}");

        mediaPlayerService.playMedia("living_room_tv", "http://example.com/song.mp3", "music", "next");

        Mockito.verify(serviceService).callService("media_player", "play_media", "{\"entity_id\":\"media_player.living_room_tv\",\"media_content_id\":\"http://example.com/song.mp3\",\"media_content_type\":\"music\",\"enqueue\":\"next\"}");
    }

    @Test
    void shouldSelectSource() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"source\":\"HDMI 1\"}");

        mediaPlayerService.selectSource("living_room_tv", "HDMI 1");

        Mockito.verify(serviceService).callService("media_player", "select_source", "{\"entity_id\":\"media_player.living_room_tv\",\"source\":\"HDMI 1\"}");
    }

    @Test
    void shouldSelectSoundMode() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"sound_mode\":\"Stereo\"}");

        mediaPlayerService.selectSoundMode("living_room_tv", "Stereo");

        Mockito.verify(serviceService).callService("media_player", "select_sound_mode", "{\"entity_id\":\"media_player.living_room_tv\",\"sound_mode\":\"Stereo\"}");
    }

    @Test
    void shouldClearPlaylist() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\"}");

        mediaPlayerService.clearPlaylist("living_room_tv");

        Mockito.verify(serviceService).callService("media_player", "clear_playlist", "{\"entity_id\":\"media_player.living_room_tv\"}");
    }

    @Test
    void shouldSetShuffle() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"shuffle\":true}");

        mediaPlayerService.setShuffle("living_room_tv", true);

        Mockito.verify(serviceService).callService("media_player", "shuffle_set", "{\"entity_id\":\"media_player.living_room_tv\",\"shuffle\":true}");
    }

    @Test
    void shouldSetRepeat() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_tv\",\"repeat\":\"all\"}");

        mediaPlayerService.setRepeat("living_room_tv", "all");

        Mockito.verify(serviceService).callService("media_player", "repeat_set", "{\"entity_id\":\"media_player.living_room_tv\",\"repeat\":\"all\"}");
    }

    @Test
    void shouldJoinGroupMembers() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_speaker\",\"group_members\":[\"media_player.kitchen_speaker\",\"media_player.bedroom_speaker\"]}");

        mediaPlayerService.join("living_room_speaker", java.util.List.of("kitchen_speaker", "media_player.bedroom_speaker"));

        Mockito.verify(serviceService).callService("media_player", "join", "{\"entity_id\":\"media_player.living_room_speaker\",\"group_members\":[\"media_player.kitchen_speaker\",\"media_player.bedroom_speaker\"]}");
    }

    @Test
    void shouldUnjoin() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"media_player.living_room_speaker\"}");

        mediaPlayerService.unjoin("living_room_speaker");

        Mockito.verify(serviceService).callService("media_player", "unjoin", "{\"entity_id\":\"media_player.living_room_speaker\"}");
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.turnOn(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.turnOn(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.turnOn("   "));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.toggle(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.setVolume(null, 0.5));
    }

    @Test
    void shouldThrowExceptionWhenVolumeOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.setVolume("tv", -0.1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.setVolume("tv", 1.1));
    }

    @Test
    void shouldThrowExceptionWhenSeekPositionNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.seek("tv", -1.0));
    }

    @Test
    void shouldThrowExceptionWhenPlayMediaParamsInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.playMedia("tv", null, "music"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.playMedia("tv", "", "music"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.playMedia("tv", "http://...", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.playMedia("tv", "http://...", ""));
    }

    @Test
    void shouldThrowExceptionWhenSelectSourceIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.selectSource("tv", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.selectSource("tv", "  "));
    }

    @Test
    void shouldThrowExceptionWhenSelectSoundModeIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.selectSoundMode("tv", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.selectSoundMode("tv", "  "));
    }

    @Test
    void shouldThrowExceptionWhenRepeatIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.setRepeat("tv", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.setRepeat("tv", "  "));
    }

    @Test
    void shouldThrowExceptionWhenJoinParamsInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.join(null, java.util.List.of("speaker2")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.join("speaker1", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.join("speaker1", java.util.List.of()));
    }

    @Test
    void shouldThrowExceptionWhenUnjoinNameIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.unjoin(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mediaPlayerService.unjoin("   "));
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> mediaPlayerService.play("tv"));
        Assertions.assertEquals("Failed to call media_play on media_player", exception.getMessage());
        Assertions.assertInstanceOf(JsonProcessingException.class, exception.getCause());
    }
}
