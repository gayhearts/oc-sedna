package gayhearts.ocsedna;

public class ControlSequenceIntroducer {
	byte[]   parameter         = new byte[64];
	boolean  parameter_lock    = false;
	byte     parameter_pos     = 0;

	byte[]   intermediate      = new byte[64];
	boolean  intermediate_lock = false;
	byte     intermediate_pos  = 0;

	byte     final_byte        = '\0';

	public void Clear(OpenComputersGPU gpu) {
		for( byte i=parameter_pos; i > 0; i-- ){
			parameter[i] = (byte)0x00;
			parameter_pos--;
		}
		parameter_lock = false;

		for( byte i=intermediate_pos; i > 0; i-- ){
			intermediate[i] = (byte)0x00;
			intermediate_pos--;
		}
		intermediate_lock = false;

		final_byte = '\0';

		gpu.c0_mode = '\0';
		gpu.c1_mode = '\0';
	}


	final static class CSI_TYPE {
		final static char PARAMETER    = 'P';
		final static char INTERMEDIATE = 'I';
		final static char FINAL        = 'F';
		final static char UNKNOWN      = 'U';
	}

	private char GetCSIType( char value ){
		// Parameter byte range.
		if( (value >= 0x30) && (value <= 0x3F) ){
			return CSI_TYPE.PARAMETER;

			// Intermediate byte range.
		} else if( (value >= 0x20) && (value <= 0x2F) ){
			return CSI_TYPE.INTERMEDIATE;

			// Final byte range.
		} else if( (value >= 0x40) && (value <= 0x7E) ){
			return CSI_TYPE.FINAL;

			// Unknown.
		} else{
			return CSI_TYPE.UNKNOWN;
		}
	}

	public void Interpret(OpenComputersGPU gpu, char value) {
		char csi_type = GetCSIType( value );

		switch( csi_type ){
		case CSI_TYPE.PARAMETER:
			if( parameter_pos >= 64 ){
				parameter_lock = true;
			} else if( parameter_lock == false ){
				parameter[parameter_pos] = (byte)value;
				parameter_pos++;
			}

			break;
		case CSI_TYPE.INTERMEDIATE:
			if( intermediate_pos >= 64 ){
				intermediate_lock = true;
			} else if( intermediate_lock == false ){
				intermediate[intermediate_pos] = (byte)value;
				intermediate_pos++;
			}
			
			break;
		case CSI_TYPE.FINAL:
			if( final_byte == '\0' ){
				final_byte = (byte)value;

				// Handle the Control Sequence.
				this.Clear(gpu);
			}

			break;
		default:
			// In the event of undefined characters, attempt to advance.
			//   If unable, Clear().
			if( parameter_lock == false ){
				parameter_lock = true;
				
			} else if( intermediate_lock == false ){
				intermediate_lock = true;
				
			} else{
				this.Clear(gpu);
			}
		}
	}
}
