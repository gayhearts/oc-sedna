package gayhearts.ocsedna;

import gayhearts.ocsedna.SednaVM;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.internal.TextBuffer;

public class OpenComputersGPU {
   String address = null;
   
   int width = 0;
   int height = 0;

   class CursorClass {
      int x = 1;
      int y = 1;
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
      
      return;
   }
   
   public void clear (Machine machine) {
      try {
	 machine.invoke(this.address, "fill", new Object[]{1, 1, this.width, this.height, " "});
      } catch (Throwable t) {
	 System.out.printf( "%s\n", t.toString() );
      }
   }
}
