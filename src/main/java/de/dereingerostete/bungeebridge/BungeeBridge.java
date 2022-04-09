package de.dereingerostete.bungeebridge;

import com.google.common.base.Preconditions;
import de.dereingerostete.bungeebridge.handler.BungeeMessageHandler;
import de.dereingerostete.bungeebridge.util.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class BungeeBridge implements IBungeeBridge {
    protected final Plugin plugin;
    protected final Messenger messenger;
    protected final String channelName;
    protected final BungeeMessageHandler handler;

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     */
    public BungeeBridge(@NotNull Plugin plugin) {
        this(plugin, 50, false);
    }

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     * @param logListener If true the listener logs warnings
     */
    public BungeeBridge(@NotNull Plugin plugin, boolean logListener) {
        this(plugin, 50, logListener);
    }

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     * @param maxCapacity The max capacity of the FutureResult queue
     * @param logListener If true the listener logs warnings
     */
    public BungeeBridge(@NotNull Plugin plugin, int maxCapacity, boolean logListener) {
        this(plugin, "BungeeCord", maxCapacity, logListener);
    }

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     * @param channelName The name of the channel that will be registered
     * @param maxCapacity The max capacity of the FutureResult queue
     * @param logListener If true the listener logs warnings
     */
    public BungeeBridge(@NotNull Plugin plugin, @NotNull String channelName,
                        int maxCapacity, boolean logListener) {
        this.plugin = plugin;
        this.channelName = channelName;
        messenger = plugin.getServer().getMessenger();

        Logger logger = plugin.getLogger();
        handler = new BungeeMessageHandler(maxCapacity, channelName, logger, logListener);

        messenger.registerOutgoingPluginChannel(plugin, channelName);
        messenger.registerIncomingPluginChannel(plugin, channelName, handler);
    }

    @Override
    public void connect(@NotNull Player player, @NotNull String serverName) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(serverName, "serverName");

        byte[] message = DataUtils.fromStrings("Connect", serverName);
        sendPluginMessage(player, message);
    }

    @Override
    public void connect(@NotNull Player player, @NotNull String playerName,
                        @NotNull String serverName) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(playerName, "playerName");
        Preconditions.checkNotNull(serverName, "serverName");

        byte[] message = DataUtils.fromStrings("ConnectOther", playerName, serverName);
        sendPluginMessage(player, message);
    }

    @Override
    public FutureResult<IPAddress> getPlayerIP(@NotNull Player player) {
        FutureResult<IPAddress> result = new FutureResult<>(Request.IP);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("IP");
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public FutureResult<IPAddress> getPlayerIP(@NotNull Player player, @NotNull String playerName) {
        FutureResult<IPAddress> result = new FutureResult<>(Request.IP_OTHER);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("IPOther", playerName);
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public FutureResult<Integer> getPlayerCount(@NotNull Player player, @NotNull String serverName) {
        FutureResult<Integer> result = new FutureResult<>(Request.PLAYER_COUNT);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("PlayerCount", serverName);
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public FutureResult<List<String>> getPlayerList(@NotNull Player player, @NotNull String serverName) {
        FutureResult<List<String>> result = new FutureResult<>(Request.PLAYER_LIST);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("PlayerList", serverName);
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public FutureResult<List<String>> getServerList(@NotNull Player player) {
        FutureResult<List<String>> result = new FutureResult<>(Request.GET_SERVERS);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("GetServers");
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String playerName,
                            @NotNull String message) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(playerName, "playerName");
        Preconditions.checkNotNull(message, "message");

        byte[] bytes = DataUtils.fromStrings("Message", playerName, message);
        sendPluginMessage(player, bytes);
    }

    @Override
    public void sendMessageRaw(@NotNull Player player, @NotNull String playerName,
                               @NotNull String rawMessage) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(playerName, "playerName");
        Preconditions.checkNotNull(rawMessage, "rawMessage");

        byte[] bytes = DataUtils.fromStrings("MessageRaw", playerName, rawMessage);
        sendPluginMessage(player, bytes);
    }

    @Override
    public FutureResult<String> getCurrentServer(@NotNull Player player) {
        FutureResult<String> result = new FutureResult<>(Request.GET_SERVER);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("GetServer");
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public void forwardCustomMessage(@NotNull Player player, @NotNull String serverName,
                                     @NotNull String subChannel, byte[] message) {
        Preconditions.checkNotNull(serverName, "serverName");
        sendForwardMessage(player, "Forward", serverName, subChannel, message);
    }

    @Override
    public void forwardToPlayer(@NotNull Player player, @NotNull String playerName,
                                @NotNull String subChannel, byte[] message) {
        Preconditions.checkNotNull(playerName, "playerName");
        sendForwardMessage(player, "ForwardToPlayer", playerName, subChannel, message);
    }

    protected void sendForwardMessage(Player player, String channel, String target,
                                       String subChannel, byte[] message) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(subChannel, "subChannel");
        Preconditions.checkNotNull(message, "message");

        int maxSize = Messenger.MAX_MESSAGE_SIZE;
        Preconditions.checkState(message.length <= maxSize,
                "Message cannot be bigger than " + maxSize);

        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(arrayOutput);

        try {
            dataOutput.writeUTF(channel);
            dataOutput.writeUTF(target);
            dataOutput.writeUTF(subChannel);
            dataOutput.writeShort(message.length);
            dataOutput.write(message);
        } catch (IOException exception) {
            throw new AssertionError(exception);
        }

        byte[] bytes = arrayOutput.toByteArray();
        sendPluginMessage(player, bytes);
    }

    @Deprecated
    @Override
    public FutureResult<UUID> getUUID(@NotNull Player player) {
        FutureResult<UUID> result = new FutureResult<>(Request.UUID);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("UUID");
        sendPluginMessage(player, message);
        return result;
    }

    @Deprecated
    @Override
    public FutureResult<UUID> getUUID(@NotNull Player player, @NotNull String playerName) {
        FutureResult<UUID> result = new FutureResult<>(Request.UUID_OTHER);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("UUIDOther");
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public FutureResult<IPAddress> getServerIP(@NotNull Player player, @NotNull String serverName) {
        FutureResult<IPAddress> result = new FutureResult<>(Request.SERVER_IP);
        handler.addToQueue(result);

        byte[] message = DataUtils.fromStrings("ServerIP", serverName);
        sendPluginMessage(player, message);
        return result;
    }

    @Override
    public void kickPlayer(@NotNull Player player, @NotNull String playerName,
                           @NotNull String reason) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(playerName, "playerName");
        Preconditions.checkNotNull(reason, "reason");

        byte[] bytes = DataUtils.fromStrings("KickPlayer", playerName, reason);
        sendPluginMessage(player, bytes);
    }

    @Override
    public void close() {
        messenger.unregisterIncomingPluginChannel(plugin, channelName, handler);
        messenger.unregisterOutgoingPluginChannel(plugin, channelName);
    }

    protected void sendPluginMessage(@NotNull Player player, byte[] bytes) {
        player.sendPluginMessage(plugin, channelName, bytes);
    }

}