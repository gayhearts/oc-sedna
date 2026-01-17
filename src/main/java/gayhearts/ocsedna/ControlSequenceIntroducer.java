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
		for( byte I=parameter_pos; I > 0; I-- ){
			parameter[I] = (byte)0x00;
			parameter_pos--;
		}
		parameter_lock = false;

		for( byte I=intermediate_pos; I > 0; I-- ){
			intermediate[I] = (byte)0x00;
			intermediate_pos--;
		}
		intermediate_lock = false;

		final_byte = '\0';

		gpu.c0_mode = '\0';
		gpu.c1_mode = '\0';
	}

	public void Interpret(OpenComputersGPU gpu, char value) {
		if( (value >= 0x30) && (value <= 0x3F) ){
			if( parameter_pos < 64 ){
				if( parameter_lock == false ){
					parameter[parameter_pos] = (byte)value;
					parameter_pos++;
				} else {
					parameter_lock = true;
				}
			}
		} else if( (value >= 0x20) && (value <=0x2F) ){
			if( intermediate_pos < 64 ){
				if( intermediate_lock == false ){
					intermediate[intermediate_pos] = (byte)value;
					intermediate_pos++;
				} else{
					intermediate_lock = true;
				}
			}
		} else if( (value >= 0x40) && (value <=0x7E) ){
			if( final_byte == '\0' ){
				final_byte = (byte)value;

				// Handle the Control Sequence.
				this.Clear(gpu);
			}
		} else{
			// In the event of undefined characters, attempt to advance.
			//   If unable, Clear().

			if( parameter_lock == false ){
				parameter_lock = true;
				return;

			} else if( intermediate_lock == false ){
				intermediate_lock = true;
				return;

			} else{
				this.Clear(gpu);
				return;
			}
		}
	}
}
