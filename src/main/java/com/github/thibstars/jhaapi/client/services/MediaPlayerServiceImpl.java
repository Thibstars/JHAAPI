package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.StatesServiceImpl;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link MediaPlayerService} interface.
 *
 * @author Thibault Helsmoortel
 */
public class MediaPlayerServiceImpl extends ToggleableServiceImpl implements MediaPlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaPlayerServiceImpl.class);
    private static final String DOMAIN = "media_player";

    private static final String TURN_ON_SERVICE = "turn_on";
    private static final String TURN_OFF_SERVICE = "turn_off";
    private static final String MEDIA_PLAY_SERVICE = "media_play";
    private static final String MEDIA_PAUSE_SERVICE = "media_pause";
    private static final String MEDIA_PLAY_PAUSE_SERVICE = "media_play_pause";
    private static final String MEDIA_STOP_SERVICE = "media_stop";
    private static final String MEDIA_NEXT_TRACK_SERVICE = "media_next_track";
    private static final String MEDIA_PREVIOUS_TRACK_SERVICE = "media_previous_track";
    private static final String VOLUME_UP_SERVICE = "volume_up";
    private static final String VOLUME_DOWN_SERVICE = "volume_down";
    private static final String VOLUME_SET_SERVICE = "volume_set";
    private static final String VOLUME_MUTE_SERVICE = "volume_mute";
    private static final String MEDIA_SEEK_SERVICE = "media_seek";
    private static final String PLAY_MEDIA_SERVICE = "play_media";
    private static final String SELECT_SOURCE_SERVICE = "select_source";
    private static final String SELECT_SOUND_MODE_SERVICE = "select_sound_mode";
    private static final String CLEAR_PLAYLIST_SERVICE = "clear_playlist";
    private static final String SHUFFLE_SET_SERVICE = "shuffle_set";
    private static final String REPEAT_SET_SERVICE = "repeat_set";
    private static final String JOIN_SERVICE = "join";
    private static final String UNJOIN_SERVICE = "unjoin";

    private final Configuration configuration;
    private final ServiceService serviceService;

    /**
     * Creates a new MediaPlayerServiceImpl.
     *
     * @param configuration the configuration to use
     * @param serviceService the service service to use
     * @param statesService the states service to use
     */
    public MediaPlayerServiceImpl(Configuration configuration, ServiceService serviceService, StatesService statesService) {
        super(DOMAIN, configuration, serviceService, statesService);
        this.configuration = configuration;
        this.serviceService = serviceService;
    }

    /**
     * Creates a new MediaPlayerServiceImpl.
     *
     * @param configuration the configuration to use
     */
    public MediaPlayerServiceImpl(Configuration configuration) {
        this(
            configuration,
            new ServiceServiceImpl(configuration),
            new StatesServiceImpl(configuration)
        );
    }

    @Override
    public void toggle(String mediaPlayerName) {
        super.toggle(resolveEntityName(mediaPlayerName));
    }

    @Override
    public void turnOn(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, TURN_ON_SERVICE);
    }

    @Override
    public void turnOff(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, TURN_OFF_SERVICE);
    }

    @Override
    public void play(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_PLAY_SERVICE);
    }

    @Override
    public void pause(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_PAUSE_SERVICE);
    }

    @Override
    public void playPause(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_PLAY_PAUSE_SERVICE);
    }

    @Override
    public void stop(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_STOP_SERVICE);
    }

    @Override
    public void nextTrack(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_NEXT_TRACK_SERVICE);
    }

    @Override
    public void previousTrack(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, MEDIA_PREVIOUS_TRACK_SERVICE);
    }

    @Override
    public void volumeUp(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, VOLUME_UP_SERVICE);
    }

    @Override
    public void volumeDown(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, VOLUME_DOWN_SERVICE);
    }

    @Override
    public void setVolume(String mediaPlayerName, double volumeLevel) {
        if (volumeLevel < 0.0 || volumeLevel > 1.0) {
            throw new IllegalArgumentException("Volume level must be between 0.0 and 1.0");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Setting volume of {} to {}", entityId, volumeLevel);
        callService(VOLUME_SET_SERVICE, new VolumeSetData(entityId, volumeLevel));
    }

    @Override
    public void muteVolume(String mediaPlayerName, boolean isVolumeMuted) {
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Setting volume mute of {} to {}", entityId, isVolumeMuted);
        callService(VOLUME_MUTE_SERVICE, new VolumeMuteData(entityId, isVolumeMuted));
    }

    @Override
    public void seek(String mediaPlayerName, double seekPosition) {
        if (seekPosition < 0.0) {
            throw new IllegalArgumentException("Seek position must not be negative");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Seeking {} to {} seconds", entityId, seekPosition);
        callService(MEDIA_SEEK_SERVICE, new MediaSeekData(entityId, seekPosition));
    }

    @Override
    public void playMedia(String mediaPlayerName, String mediaContentId, String mediaContentType) {
        playMedia(mediaPlayerName, mediaContentId, mediaContentType, null);
    }

    @Override
    public void playMedia(String mediaPlayerName, String mediaContentId, String mediaContentType, String enqueue) {
        if (mediaContentId == null || mediaContentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Media content ID must not be blank");
        }
        if (mediaContentType == null || mediaContentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Media content type must not be blank");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Playing media on {}: contentId={}, contentType={}, enqueue={}", entityId, mediaContentId, mediaContentType, enqueue);
        callService(PLAY_MEDIA_SERVICE, new PlayMediaData(entityId, mediaContentId, mediaContentType, enqueue));
    }

    @Override
    public void selectSource(String mediaPlayerName, String source) {
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("Source must not be blank");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Selecting source '{}' on {}", source, entityId);
        callService(SELECT_SOURCE_SERVICE, new SelectSourceData(entityId, source));
    }

    @Override
    public void selectSoundMode(String mediaPlayerName, String soundMode) {
        if (soundMode == null || soundMode.trim().isEmpty()) {
            throw new IllegalArgumentException("Sound mode must not be blank");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Selecting sound mode '{}' on {}", soundMode, entityId);
        callService(SELECT_SOUND_MODE_SERVICE, new SelectSoundModeData(entityId, soundMode));
    }

    @Override
    public void clearPlaylist(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, CLEAR_PLAYLIST_SERVICE);
    }

    @Override
    public void setShuffle(String mediaPlayerName, boolean shuffle) {
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Setting shuffle of {} to {}", entityId, shuffle);
        callService(SHUFFLE_SET_SERVICE, new ShuffleSetData(entityId, shuffle));
    }

    @Override
    public void setRepeat(String mediaPlayerName, String repeat) {
        if (repeat == null || repeat.trim().isEmpty()) {
            throw new IllegalArgumentException("Repeat mode must not be blank");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Setting repeat of {} to {}", entityId, repeat);
        callService(REPEAT_SET_SERVICE, new RepeatSetData(entityId, repeat));
    }

    @Override
    public void join(String mediaPlayerName, List<String> groupMembers) {
        if (groupMembers == null || groupMembers.isEmpty()) {
            throw new IllegalArgumentException("Group members must not be empty");
        }
        String entityId = resolveEntityId(mediaPlayerName);
        List<String> resolvedMembers = groupMembers.stream()
                .map(this::resolveEntityId)
                .toList();
        LOGGER.info("Joining group members {} with master media player {}", resolvedMembers, entityId);
        callService(JOIN_SERVICE, new JoinData(entityId, resolvedMembers));
    }

    @Override
    public void unjoin(String mediaPlayerName) {
        callSimpleService(mediaPlayerName, UNJOIN_SERVICE);
    }

    private void callSimpleService(String mediaPlayerName, String serviceName) {
        String entityId = resolveEntityId(mediaPlayerName);
        LOGGER.info("Calling {} on media player {}", serviceName, entityId);
        callService(serviceName, new EntityData(entityId));
    }

    private void callService(String serviceName, Object serviceData) {
        try {
            String json = configuration.getObjectMapper().writeValueAsString(serviceData);
            serviceService.callService(DOMAIN, serviceName, json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize service data for service {} in domain {}", serviceName, DOMAIN, e);
            throw new RuntimeException("Failed to call " + serviceName + " on " + DOMAIN, e);
        }
    }

    private String resolveEntityId(String mediaPlayerName) {
        if (mediaPlayerName == null || mediaPlayerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Media player name must not be blank");
        }
        return mediaPlayerName.startsWith(DOMAIN + ".") ? mediaPlayerName : DOMAIN + "." + mediaPlayerName;
    }

    private String resolveEntityName(String mediaPlayerName) {
        if (mediaPlayerName == null || mediaPlayerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Media player name must not be blank");
        }
        return mediaPlayerName.startsWith(DOMAIN + ".") ? mediaPlayerName.substring((DOMAIN + ".").length()) : mediaPlayerName;
    }

    private record EntityData(String entityId) {}

    private record VolumeSetData(String entityId, double volumeLevel) {}

    private record VolumeMuteData(String entityId, @JsonProperty("is_volume_muted") boolean isVolumeMuted) {}

    private record MediaSeekData(String entityId, double seekPosition) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record PlayMediaData(String entityId, String mediaContentId, String mediaContentType, String enqueue) {}

    private record SelectSourceData(String entityId, String source) {}

    private record SelectSoundModeData(String entityId, String soundMode) {}

    private record ShuffleSetData(String entityId, boolean shuffle) {}

    private record RepeatSetData(String entityId, String repeat) {}

    private record JoinData(String entityId, @JsonProperty("group_members") List<String> groupMembers) {}
}
