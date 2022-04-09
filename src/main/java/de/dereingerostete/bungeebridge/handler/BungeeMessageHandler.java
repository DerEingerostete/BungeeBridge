package de.dereingerostete.bungeebridge.handler;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.dereingerostete.bungeebridge.util.FutureResult;
import de.dereingerostete.bungeebridge.util.IPAddress;
import de.dereingerostete.bungeebridge.util.Request;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApiStatus.Internal
public class BungeeMessageHandler implements PluginMessageListener {
    private final BlockingQueue<FutureResult<Object>> futureQueue;
    private final String channelName;
    private final Logger logger;
    private final boolean logActions;

    public BungeeMessageHandler(int maxCapacity, String channelName,
                                Logger logger, boolean logActions) {
        this.futureQueue = new ArrayBlockingQueue<>(maxCapacity);
        this.logger = logger;
        this.logActions = logActions;
        this.channelName = channelName;
    }

    @SuppressWarnings("unchecked")
    public void addToQueue(FutureResult<?> result) {
        boolean added = futureQueue.offer((FutureResult<Object>) result);
        if (!added) warn("Failed to add FutureResult to queue");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] bytes) {
        if (!channel.equals(channelName)) return;
        FutureResult<Object> result = futureQueue.poll();
        if (result == null) {
            warn("Missed PluginMessage: No request was given");
            return;
        }

        try {
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(bytes);
            String subChannel = dataInput.readUTF();

            Request request = result.getRequest();
            String requestChannelName = request.getChannelName();
            if (!subChannel.equals(requestChannelName)) {
                warn("SubChannel(" + subChannel + ") did not match request(" + requestChannelName + ")");
                result.setThrowable(new IllegalStateException(
                        "SubChannel(" + subChannel + ") did not match request"));
                return;
            }

            switch (request) {
                case IP:
                    answerIP(result, dataInput);
                    break;
                case IP_OTHER:
                    answerIPOther(result, dataInput);
                    break;
                case PLAYER_COUNT:
                    answerPlayerCount(result, dataInput);
                    break;
                case PLAYER_LIST:
                    answerPlayerList(result, dataInput);
                    break;
                case GET_SERVERS:
                    answerGetServers(result, dataInput);
                    break;
                case GET_SERVER:
                    answerGetServer(result, dataInput);
                    break;
                case UUID:
                    answerUUID(result, dataInput);
                    break;
                case UUID_OTHER:
                    answerUUIDOther(result, dataInput);
                    break;
                case SERVER_IP:
                    answerServerIP(result, dataInput);
                    break;
                default:
                    warn("Missed PluginMessage: Request '" + request.name() + "' is unknown");
                    result.setThrowable(new IllegalStateException("Unknown request: " + request.name()));
                    break;
            }
        } catch (Throwable throwable) {
            result.setThrowable(throwable);
            if (logActions) logger.log(Level.WARNING,
                    "[BungeeBridge] Missed PluginMessage: Exception was thrown", throwable);
        }
    }

    private void answerServerIP(@NotNull FutureResult<Object> result,
                                @NotNull ByteArrayDataInput dataInput) {
        dataInput.readUTF(); //Reads the serverName
        String ip = dataInput.readUTF();
        int port = dataInput.readUnsignedShort();

        IPAddress address = new IPAddress(ip, port);
        result.finish(address);
    }

    private void answerUUIDOther(@NotNull FutureResult<Object> result,
                                 @NotNull ByteArrayDataInput dataInput) {
        dataInput.readUTF(); //Reads the username
        String uuidString = dataInput.readUTF();

        UUID uuid = UUID.fromString(uuidString);
        result.finish(uuid);
    }

    private void answerUUID(@NotNull FutureResult<Object> result,
                            @NotNull ByteArrayDataInput dataInput) {
        String uuidString = dataInput.readUTF();
        UUID uuid = UUID.fromString(uuidString);
        result.finish(uuid);
    }

    private void answerGetServer(@NotNull FutureResult<Object> result,
                                 @NotNull ByteArrayDataInput dataInput) {
        String name = dataInput.readUTF();
        result.finish(name);
    }

    private void answerGetServers(@NotNull FutureResult<Object> result,
                                  @NotNull ByteArrayDataInput dataInput) {
        String[] serverNames = dataInput.readUTF().split(", ");
        List<String> list = Lists.newArrayList(serverNames);
        result.finish(list);
    }

    private void answerPlayerList(@NotNull FutureResult<Object> result,
                                  @NotNull ByteArrayDataInput dataInput) {
        dataInput.readUTF(); //Reads the server name
        String[] playerNames = dataInput.readUTF().split(", ");

        List<String> list = Lists.newArrayList(playerNames);
        result.finish(list);
    }

    private void answerPlayerCount(@NotNull FutureResult<Object> result,
                                   @NotNull ByteArrayDataInput dataInput) {
        dataInput.readUTF(); //Reads the server name
        int playerCount = dataInput.readInt();
        result.finish(playerCount);
    }

    private void answerIPOther(@NotNull FutureResult<Object> result,
                               @NotNull ByteArrayDataInput dataInput) {
        dataInput.readUTF(); //Reads the username
        String ip = dataInput.readUTF();
        int port = dataInput.readInt();

        IPAddress address = new IPAddress(ip, port);
        result.finish(address);
    }

    private void answerIP(@NotNull FutureResult<Object> result,
                          @NotNull ByteArrayDataInput dataInput) {
        String ip = dataInput.readUTF();
        int port = dataInput.readInt();

        IPAddress address = new IPAddress(ip, port);
        result.finish(address);
    }

    protected void warn(@NotNull String message) {
        if (logActions) logger.warning("[BungeeBridge] " + message);
    }

}
