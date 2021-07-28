package de.dereingerostete.bungeebridge.handler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class BungeeMessageHandler implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] bytes) {

    }

}
