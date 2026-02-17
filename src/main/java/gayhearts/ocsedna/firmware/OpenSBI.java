package gayhearts.ocsedna.firmware;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public final class OpenSBI {
	private OpenSBI(){}
	
	private static byte[] GetFile(String filename) {
		try( InputStream in_stream = OpenSBI.class.getResourceAsStream(filename) ){
			try( ByteArrayOutputStream out_stream = new ByteArrayOutputStream() ){
				in_stream.transferTo( out_stream );
				
				return out_stream.toByteArray();
				//System.out.printf("Error loading firmware. Got null.\n");
			} catch( IOException exception ){
				return new byte[] {};
			}
		} catch( IOException exception ){
			return new byte[] {};
		}
	}

	public static byte[] GetDynamicFirmware() {
		return GetFile("/assets/ocsedna/binary/fw_dynamic.bin");
	}

	public static byte[] GetJumpFirmware() {
		return GetFile("/assets/ocsedna/binary/fw_jump.bin");

	}

	public static byte[] GetLegacyFirmware() {
		return GetFile("/generated/fw_jump.bin");
	}
}
