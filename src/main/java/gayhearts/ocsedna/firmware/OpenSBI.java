package gayhearts.ocsedna.firmware;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class OpenSBI {
    private static byte[] GetFile(String filename) {
        try {
            InputStream in_stream = OpenSBI.class.getResourceAsStream("/assets/ocsedna/binary/" + filename);
            ByteArrayOutputStream out_stream = new ByteArrayOutputStream();

            if( in_stream != null && out_stream != null ) {
                int tmp = 0;

                // Read until EOF.
                while(tmp != -1) {
                    tmp = in_stream.read();
                    out_stream.write(tmp);
                }

                if( out_stream != null ) {
                    return out_stream.toByteArray();
                } else {
                    return new byte[] {};
                }
            } else {
                System.out.printf("Error loading firmware. Got null.\n");
            }
        } catch (Throwable thrown) {
            System.out.println(thrown.toString());
        }

        return new byte[] {};
    }

    public static byte[] GetDynamicFirmware() {
        return GetFile("fw_dynamic.bin");
    }

    public static byte[] GetJumpFirmware() {
        return GetFile("fw_jump.bin");

    }
}
