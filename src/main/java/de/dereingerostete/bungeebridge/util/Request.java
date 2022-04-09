package de.dereingerostete.bungeebridge.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum Request {
    CONNECT(null),
    CONNECT_OTHER(null),
    IP("IP"),
    IP_OTHER("IPOther"),
    PLAYER_COUNT("PlayerCount"),
    PLAYER_LIST("PlayerList"),
    GET_SERVERS("GetServers"),
    MESSAGE(null),
    MESSAGE_RAW(null),
    GET_SERVER("GetServer"),
    FORWARD(null),
    FORWARD_TO_PLAYER(null),
    UUID("UUID"),
    UUID_OTHER("UUIDOther"),
    SERVER_IP("ServerIP"),
    KICK_PLAYER(null);

    private final @Nullable String channelName;

}
