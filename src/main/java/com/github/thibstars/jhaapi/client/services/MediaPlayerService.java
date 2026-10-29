package com.github.thibstars.jhaapi.client.services;

import java.util.List;

/**
 * Service for controlling media player entities in Home Assistant.
 *
 * @author Thibault Helsmoortel
 */
public interface MediaPlayerService extends ToggleableService {

    /**
     * Toggles a media player on or off.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    @Override
    void toggle(String mediaPlayerName);

    /**
     * Turns on a media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void turnOn(String mediaPlayerName);

    /**
     * Turns off a media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void turnOff(String mediaPlayerName);

    /**
     * Sends the play command to the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void play(String mediaPlayerName);

    /**
     * Sends the pause command to the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void pause(String mediaPlayerName);

    /**
     * Toggles playback between play and pause on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void playPause(String mediaPlayerName);

    /**
     * Sends the stop command to the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void stop(String mediaPlayerName);

    /**
     * Selects the next track on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void nextTrack(String mediaPlayerName);

    /**
     * Selects the previous track on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void previousTrack(String mediaPlayerName);

    /**
     * Steps up the volume on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void volumeUp(String mediaPlayerName);

    /**
     * Steps down the volume on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void volumeDown(String mediaPlayerName);

    /**
     * Sets the volume level on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param volumeLevel     the volume level between 0.0 and 1.0
     * @throws IllegalArgumentException if mediaPlayerName is blank or volumeLevel is out of range
     */
    void setVolume(String mediaPlayerName, double volumeLevel);

    /**
     * Mutes or unmutes the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param isVolumeMuted   true to mute, false to unmute
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void muteVolume(String mediaPlayerName, boolean isVolumeMuted);

    /**
     * Seeks to a specific position in the media currently playing.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param seekPosition    the position to seek to in seconds
     * @throws IllegalArgumentException if mediaPlayerName is blank or seekPosition is negative
     */
    void seek(String mediaPlayerName, double seekPosition);

    /**
     * Plays specified media on the media player.
     *
     * @param mediaPlayerName  the name or entity ID of the media player (must not be blank)
     * @param mediaContentId   the ID or URL of the media content (must not be blank)
     * @param mediaContentType the type of media (e.g., "music", "video", "channel", etc.) (must not be blank)
     * @throws IllegalArgumentException if any argument is blank
     */
    void playMedia(String mediaPlayerName, String mediaContentId, String mediaContentType);

    /**
     * Plays specified media on the media player with enqueue option.
     *
     * @param mediaPlayerName  the name or entity ID of the media player (must not be blank)
     * @param mediaContentId   the ID or URL of the media content (must not be blank)
     * @param mediaContentType the type of media (e.g., "music", "video", "channel", etc.) (must not be blank)
     * @param enqueue          how to handle the media if something is already playing ("play", "next", "add", "replace")
     * @throws IllegalArgumentException if mediaPlayerName, mediaContentId, or mediaContentType is blank
     */
    void playMedia(String mediaPlayerName, String mediaContentId, String mediaContentType, String enqueue);

    /**
     * Selects an input source for the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param source          the name of the source (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName or source is blank
     */
    void selectSource(String mediaPlayerName, String source);

    /**
     * Selects a sound mode for the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param soundMode       the sound mode (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName or soundMode is blank
     */
    void selectSoundMode(String mediaPlayerName, String soundMode);

    /**
     * Clears the current playlist on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void clearPlaylist(String mediaPlayerName);

    /**
     * Sets the shuffle mode on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param shuffle         true to enable shuffle, false to disable
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void setShuffle(String mediaPlayerName, boolean shuffle);

    /**
     * Sets the repeat mode on the media player.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @param repeat          the repeat mode ("off", "all", "one") (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName or repeat is blank
     */
    void setRepeat(String mediaPlayerName, String repeat);

    /**
     * Groups player with one or more other media players.
     *
     * @param mediaPlayerName the name or entity ID of the master media player (must not be blank)
     * @param groupMembers    the list of media player names or entity IDs to join with the master player (must not be empty)
     * @throws IllegalArgumentException if mediaPlayerName is blank or groupMembers is null or empty
     */
    void join(String mediaPlayerName, List<String> groupMembers);

    /**
     * Unjoins the media player from a multiroom group.
     *
     * @param mediaPlayerName the name or entity ID of the media player (must not be blank)
     * @throws IllegalArgumentException if mediaPlayerName is blank
     */
    void unjoin(String mediaPlayerName);
}
