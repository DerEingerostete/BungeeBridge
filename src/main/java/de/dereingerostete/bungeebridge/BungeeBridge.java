package de.dereingerostete.bungeebridge;

import com.google.common.base.Preconditions;
import de.dereingerostete.bungeebridge.handler.BungeeMessageHandler;
import de.dereingerostete.bungeebridge.util.DataUtils;
import de.dereingerostete.bungeebridge.util.IBungeeBridge;
import de.dereingerostete.bungeebridge.util.IPAddress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public class BungeeBridge implements IBungeeBridge {
    protected final Plugin plugin;
    protected final Messenger messenger;
    protected final String channelName;
    protected final PluginMessageListener listener;

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     */
    public BungeeBridge(@NotNull Plugin plugin) {
        this(plugin, "BungeeCord");
    }

    /**
     * Creates a new BungeeCord bridge
     * @param plugin The plugin used to register the {@link Messenger}
     * @param channelName The name of the channel that will be registered
     */
    public BungeeBridge(@NotNull Plugin plugin, @NotNull String channelName) {
        this.plugin = plugin;
        this.channelName = channelName;
        messenger = plugin.getServer().getMessenger();

        listener = new BungeeMessageHandler();
        messenger.registerOutgoingPluginChannel(plugin, channelName);
        messenger.registerIncomingPluginChannel(plugin, channelName, listener);
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
    public Future<IPAddress> getPlayerIP(@NotNull Player player) {
        return null;
    }

    @Override
    public Future<IPAddress> getPlayerIP(@NotNull Player player, @NotNull String playerName) {
        return null;
    }

    @Override
    public Future<Integer> getPlayerCount(@NotNull Player player, @NotNull String serverName) {
        return null;
    }

    @Override
    public Future<List<String>> getPlayerList(@NotNull Player player, @NotNull String serverName) {
        return null;
    }

    @Override
    public Future<List<String>> getServerList(@NotNull Player player) {
        return null;
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
    public Future<String> getCurrentServer(@NotNull Player player) {
        return null;
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
    public Future<UUID> getUUID(@NotNull Player player) {
        return null;
    }

    @Deprecated
    @Override
    public Future<UUID> getUUID(@NotNull Player player, @NotNull String playerName) {
        return null;
    }

    @Override
    public Future<IPAddress> getServerIP(@NotNull Player player, @NotNull String serverName) {
        return null;
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
        messenger.unregisterIncomingPluginChannel(plugin, channelName, listener);
        messenger.unregisterOutgoingPluginChannel(plugin, channelName);
    }

    protected void sendPluginMessage(Player player, byte[] bytes) {
        player.sendPluginMessage(plugin, channelName, bytes);
    }

}
