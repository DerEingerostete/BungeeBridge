package de.dereingerostete.bungeebridge.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public interface IBungeeBridge {

    /**
     * Connects a player to the said subserver
     * @param player The player you want to send
     * @param serverName Name of server to connect to, as defined in BungeeCord config.yml
     */
    void connect(@NotNull Player player, @NotNull String serverName);

    /**
     * Connect a named player to said subserver
     * @param player The player that will executes the event (can be any player)
     * @param playerName Name of the player to teleport
     * @param serverName Name of server to connect to, as defined in BungeeCord config.yml
     */
    void connect(@NotNull Player player, @NotNull String playerName, @NotNull String serverName);

    /**
     * Gets the (real) IP of a player
     * @param player The player you wish to get the IP of
     * @return The IP of the player as a Future
     */
    Future<IPAddress> getPlayerIP(@NotNull Player player);

    /**
     * Gets the (real) IP of another player
     * @param player The player that will executes the event (can be any player)
     * @param playerName The username of the player you wish to get the IP of
     * @return The IP of the player as a Future
     */
    Future<IPAddress> getPlayerIP(@NotNull Player player, @NotNull String playerName);

    /**
     * Get the amount of players on a certain server, or on ALL the servers
     * @param player The player that will executes the event (can be any player)
     * @param serverName The name of the server to get the player count of, or ALL to get the global player count
     * @return The amount of players as a Future
     */
    Future<Integer> getPlayerCount(@NotNull Player player, @NotNull String serverName);

    /**
     * Get a list of players connected on a certain server, or on ALL of the servers.
     * @param player The player that will executes the event (can be any player)
     * @param serverName The name of the server to get the list of
     *                   connected players, or ALL for global online player list
     * @return List of players as a Future
     */
    Future<List<String>> getPlayerList(@NotNull Player player, @NotNull String serverName);

    /**
     * Get a list of server names, as defined in BungeeCord's config.yml
     * @param player The player that will executes the event (can be any player)
     * @return List of server names as a Future
     */
    Future<List<String>> getServerList(@NotNull Player player);

    /**
     * Send a message (as in, a chat message) to the specified player
     * @param player The player that will executes the event (can be any player)
     * @param playerName The name of the player to send the chat message, or ALL to send to all players
     * @param message The message to send to the player
     */
    void sendMessage(@NotNull Player player, @NotNull String playerName, @NotNull String message);

    /**
     * Send a message (as in, a chat message) to the specified player
     * @param player The player that will executes the event (can be any player)
     * @param playerName The name of the player to send the chat message, or ALL to send to all players
     * @param rawMessage The raw message to send to the player
     */
    void sendMessageRaw(@NotNull Player player, @NotNull String playerName, @NotNull String rawMessage);

    /**
     * Get this server's name, as defined in BungeeCord's config.yml
     * @param player The player that will executes the event (can be any player)
     * @return The server name as a Future
     */
    Future<String> getCurrentServer(@NotNull Player player);

    /**
     * Send a custom plugin message to said server. This is one of the most useful channels ever
     * <b>Remember, the sending and receiving server(s) need to have a player online.</b>
     * @param player The player that will executes the event (can be any player)
     * @param serverName The server to send to;
     *                  ALL to send to every server (except the one sending the plugin message);
     *                  or ONLINE to send to every server that's online
     *                  (except the one sending the plugin message)
     * @param subChannel SubChannel for plugin usage.
     * @param message The message to send (max length around 32766 [see {@link Messenger#MAX_MESSAGE_SIZE}])
     */
    void forwardCustomMessage(@NotNull Player player, @NotNull String serverName,
                              @NotNull String subChannel, byte[] message);

    /**
     * Send a custom plugin message to specific player
     * @param player The player that will executes the event (can be any player)
     * @param playerName The player name to send to
     * @param subChannel SubChannel for plugin usage
     * @param message The message to send (max length around 32766 [see {@link Messenger#MAX_MESSAGE_SIZE}])
     */
    void forwardToPlayer(@NotNull Player player, @NotNull String playerName,
                         @NotNull String subChannel, byte[] message);

    /**
     * Gets the UUID of the specified player
     * @param player The player whose UUID will be returned
     * @return The UUID of the player as a Future
     */
    Future<UUID> getUUID(@NotNull Player player);

    /**
     * Gets the UUID of the specified player
     * @param player The player that will executes the event (can be any player)
     * @param playerName The name of the player whose UUID will be returned
     * @return The UUID of the player as a Future
     */
    Future<UUID> getUUID(@NotNull Player player, @NotNull String playerName);

    /**
     * Gets the IP of any server on this proxy
     * @param player The player that will executes the event (can be any player)
     * @param serverName The name of the server whose IP will be returned
     * @return The server IP as a Future
     */
    Future<IPAddress> getServerIP(@NotNull Player player, @NotNull String serverName);

    /**
     * Kick any player on this proxy
     * @param player The player that will executes the event (can be any player)
     * @param playerName The name of the player that will be kicked
     * @param reason The reason the player is kicked with
     */
    void kickPlayer(@NotNull Player player, @NotNull String playerName, @NotNull String reason);

    /**
     * Closes the BungeeBridge. This is done by unregistering the channels
     * @see Messenger#unregisterOutgoingPluginChannel(Plugin, String)
     * @see Messenger#unregisterIncomingPluginChannel(Plugin, String, PluginMessageListener)
     */
    void close();

}
