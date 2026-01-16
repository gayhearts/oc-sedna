package gayhearts.ocsedna;

import gayhearts.ocsedna.SednaVM;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.internal.TextBuffer;

import java.lang.StringBuilder;

public class OpenComputersGPU {
   String address = null;
   String screen_address = null;
   
   boolean initialized = false;
   
   int width = 0;
   int height = 0;

   TextBuffer text_buffer;
   int[][] text_buffer_swap;
   
   class CursorClass {
      int x = 0;
      int y = 0;
   }
   CursorClass cursor = new CursorClass();
   
   public void initialize( Machine machine ) {
      Object[] gpu_size;
      
      try {
	 gpu_size = machine.invoke(this.address, "getResolution",
					 new Object[]{});
      } catch (Throwable t) {
	 System.out.printf( "%s\n", t.toString() );
	 
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
      
      return;
   }
   
   public void Scroll (int scroll_amount) {
      for( int line=1; line < this.height; line++ ) {
	 for( int pos=0; pos < this.width; pos++ ) {
	    this.text_buffer_swap[line - 1][pos] = this.text_buffer_swap[line][pos];
	    this.text_buffer_swap[line][pos] = 32;
	 }
      }
      
      this.text_buffer.fill(0, 0, this.width, this.height, ' ');
      
      try { 
	 this.text_buffer.rawSetText(0, 0, this.text_buffer_swap);
      } catch (Throwable t) {
	 System.out.printf( "Scroll: %s\n", t.toString() );
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
      } catch (Throwable t) {
	 System.out.printf( "Clear failed: %s\n", t.toString() );
      }
   }

   public void WriteChar (char character) {
      if( this.initialized == true ) {
	 if( character == '\n' ){
		 this.cursor.x = this.width;

		 return;
	 } else {
	    try {
	       this.text_buffer_swap[this.cursor.y][this.cursor.x] = character;
	       this.text_buffer.set(this.cursor.x, this.cursor.y, String.valueOf(character), false);
	    } catch (Throwable t) {
	       System.out.printf( "WriteChar failed: %s\n", t.toString() );
	    }
	 }
	 
      
	 if( this.cursor.y >= (this.height - 1) && this.cursor.x >= (this.width - 1) ) {
	    this.Scroll(1);
	 }	   
      
	 if( this.cursor.y < (this.height - 1) && this.cursor.x >= (this.width - 1) ) {
	    this.cursor.y = ((this.cursor.y + 1) % this.height);
	 }
       
	 // handle cursor movement, wraps.
	 this.cursor.x = ((this.cursor.x + 1) % this.width);

	 return;
      }      
   }
   
   public void WriteString (String message) {
      for( int I = 0; I < message.length(); I++ ) {
	 this.WriteChar(message.charAt(I));
      }	   
   }	
}
