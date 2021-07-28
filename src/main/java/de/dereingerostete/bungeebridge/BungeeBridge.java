package de.dereingerostete.bungeebridge;

import de.dereingerostete.bungeebridge.handler.BungeeMessageHandler;
import de.dereingerostete.bungeebridge.util.IBungeeBridge;
import de.dereingerostete.bungeebridge.util.IPAddress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

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

    }

    @Override
    public void connect(@NotNull Player player, @NotNull String playerName,
                        @NotNull String serverName) {

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

    }

    @Override
    public void sendMessageRaw(@NotNull Player player, @NotNull String playerName,
                               @NotNull String rawMessage) {

    }

    @Override
    public Future<String> getCurrentServer(@NotNull Player player) {
        return null;
    }

    @Override
    public void forwardCustomMessage(@NotNull Player player, @NotNull String serverName,
                                     @NotNull String subChannel, byte[] message) {

    }

    @Override
    public void forwardToPlayer(@NotNull Player player, @NotNull String playerName,
                                @NotNull String subChannel, byte[] message) {

    }

    @Override
    public Future<UUID> getUUID(@NotNull Player player) {
        return null;
    }

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

    }

    @Override
    public void close() {
        messenger.unregisterIncomingPluginChannel(plugin, channelName, listener);
        messenger.unregisterOutgoingPluginChannel(plugin, channelName);
    }

}
