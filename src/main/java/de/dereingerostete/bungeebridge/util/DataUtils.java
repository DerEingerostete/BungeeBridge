package de.dereingerostete.bungeebridge.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;

public class DataUtils {

    @SuppressWarnings("UnstableApiUsage")
    public static byte @NotNull [] fromStrings(String @NotNull ... strings) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        for (String string : strings)
            dataOutput.writeUTF(string);
        return dataOutput.toByteArray();
    }

}
