package gayhearts.ocsedna;

import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.api.machine.Signal;

import gayhearts.ocsedna.api.API;

public class OpenComputersGPU {
	Machine machine;

	String address = null;
	String screen_address = null;
	String keyboard_address = null;

	boolean initialized = false;

	int width = 0;
	int height = 0;

	// For escape sequences and such.
	// https://en.wikipedia.org/wiki/C0_and_C1_control_codes
	byte    c0_mode = '\0';
	byte    c1_mode = '\0';

	ControlSequenceIntroducer CSI = new ControlSequenceIntroducer();

	TextBuffer text_buffer;
	int[][] text_buffer_swap;

	class CursorClass {
		int x = 0;
		int y = 0;
	}
	CursorClass cursor = new CursorClass();

	public void initialize( Machine machine ) {
		// TODO NullPointerException ?
		this.machine = machine;

		//System.out.printf( "%s\n", thrown.toString() );


		Object[] gpu_size;

		try {
			gpu_size = machine.invoke(this.address, "getResolution",
					new Object[]{});
		} catch( Exception exception ){
			//System.out.printf( "%s\n", t.toString() );
			API.Logger.Info( exception.toString() );

			return;
		}

		if( gpu_size[0] != null && gpu_size[0] instanceof Integer ) {
			this.width = (Integer)gpu_size[0];
		} else {
			this.width = 40;
		}

		if( gpu_size[1] != null && gpu_size[1] instanceof Integer ) {
			this.height = (Integer)gpu_size[1];
		} else {
			this.height = 16;
		}

		// Text Buffer's swap.
		this.text_buffer_swap = new int[this.height][this.width];

		// Get the Screen's Text Buffer.
		Environment host_environment = machine.node().network().node(this.screen_address).host();
		this.text_buffer = (TextBuffer) host_environment;
		this.text_buffer.setResolution(this.width, this.height);

		// Initialize the text buffer.
		this.clear();

		this.initialized = true;
	}

	public void Scroll (int scroll_amount) {
		for( int line=1; line < this.height; line++ ) {
			for( int pos=0; pos < this.width; pos++ ) {
				this.text_buffer_swap[line - 1][pos] = this.text_buffer_swap[line][pos];
				this.text_buffer_swap[line][pos] = 32;
			}
		}

		this.text_buffer.fill(0, 0, this.width, this.height, 32);

		try { 
			this.text_buffer.rawSetText(0, 0, this.text_buffer_swap);
		} catch( Exception exception ){
			API.Logger.Info( exception.toString() );
			//System.out.printf( "Scroll: %s\n", t.toString() );
		}

	}


	public void clear () {
		try {
			for( int line=0; line < this.height; line++ ) {
				for( int pos=0; pos < this.width; pos++ ) {	 
					this.text_buffer_swap[line][pos] = 32;
				}
			}

			this.text_buffer.fill(0, 0, this.width, this.height, 32);
		} catch( Exception exception ){
			API.Logger.Info( exception.toString() );
			//System.out.printf( "Clear failed: %s\n", t.toString() );
		}
	}

	public boolean HandleChar (char character) {
		switch (character) {
			case 0x1B: //ESC
				this.c0_mode = 0x1B;
				break;
			case '\n':
				this.cursor.x = (this.width - 1);
				break;
			case '\r':
				this.cursor.x = 0;
				break;
			default:
				try {
					this.text_buffer_swap[this.cursor.y][this.cursor.x] = character;
					this.text_buffer.set(this.cursor.x, this.cursor.y, String.valueOf(character), false);
				} catch( Exception exception ){
					API.Logger.Info( exception.toString() );
					//System.out.printf( "WriteChar failed: %s\n", t.toString() );
					return false;
				}
		}

		return true;
	}

	private static final byte C1_MODE_CSI = (byte) 0x9B;

	private void ProcessC1( char character ){
		switch( this.c1_mode ){
			case '\0': // No C2 mode.
				if( character >= 64 && character <= 95 ) {
					// C1 codes seem to be simply: ascii value + 64.
					this.c1_mode = (byte)(64 + character);
				} else {
					// Invalid C1 sequence; clear mode.
					this.c0_mode = '\0';
					this.c1_mode = '\0';
				}

				break;
			case C1_MODE_CSI: // CSI/ANSI escape sequence.
				CSI.Interpret(this, character);

				break;
			default: // Unimplemented C1 sequence; clear mode.
				this.c0_mode = '\0';
				this.c1_mode = '\0';
		}
	}

	public void WriteChar( char character ){
		if( this.initialized == false ){
			return;
		}

		switch( this.c0_mode ){
			case 0x1B: // ESC/^[
				ProcessC1( character );
				return;
			case '\0':
				if( HandleChar(character) != true ) {
					return;
				}
				break;
			default:
				break;
		}

		// Advance cursor by one.
		//   If last position, scroll. 
		if( this.cursor.y >= (this.height - 1) && this.cursor.x >= (this.width - 1) ) {
			this.Scroll(1);
		}	   

		// Vertical.
		if( this.cursor.y < (this.height - 1) && this.cursor.x >= (this.width - 1) ) {
			this.cursor.y = ((this.cursor.y + 1) % this.height);
		}

		// Horizontal.
		this.cursor.x = ((this.cursor.x + 1) % this.width);
	}

	public void WriteString (String message) {
		for( int I = 0; I < message.length(); I++ ) {
			this.WriteChar(message.charAt(I));
		}	   
	}	

	public int GetInput() {
		if( this.keyboard_address == null ){
			return 0;
		}
		
		try {
			Signal signal      = this.machine.popSignal();
			String signal_name = null;

			if( signal != null ){
				signal_name = signal.name();
			} else{
				return 0;
			}
				
			if( "key_down".equals(signal_name) ) {
				return (int)signal.args()[2];
			}
		} catch( Exception exception ){
			API.Logger.Info( exception.toString() );
		}

		return 0;
	}
}
