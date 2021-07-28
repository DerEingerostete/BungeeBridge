package de.dereingerostete.bungeebridge.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataUtils {

    public static byte[] fromStrings(String... strings) {
        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(arrayOutput);

        try {
            for (String string : strings)
                dataOutput.writeUTF(string);
        } catch (IOException exception) {
            throw new AssertionError(exception);
        }

        return arrayOutput.toByteArray();
    }

}
