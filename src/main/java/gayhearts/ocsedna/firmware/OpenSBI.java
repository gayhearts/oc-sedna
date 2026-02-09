package gayhearts.ocsedna.firmware;

import java.io.InputStream;

public class OpenSBI {
    private static byte[] GetFile(String filename) {
        try {
            byte[] data = new byte[0x200000];
            InputStream stream = OpenSBI.class.getResourceAsStream("/assets/ocsedna/binary/" + filename);
            
            if( stream != null ) {
                stream.read(data);

                return data;
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
