package gayhearts.ocsedna;

import gayhearts.ocsedna.OpenComputersGPU;

import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.Signal;
import li.cil.oc.api.prefab.AbstractValue;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SednaVM {
   Machine machine;
   
   String computer_address = null;
   String screen_address = null;
   String eeprom_address = null;
   String keyboard_address = null;

   OpenComputersGPU gpu = new OpenComputersGPU();
   SednaVMRunner sedna_vm = new SednaVMRunner();

   Object[] run(Object[] args) {
	  // Text string when needed.
		//gpu.WriteString( "What's the plan, Stan? " );
		sedna_vm.SednaVMStep();
		return new Object[10];
   }
   
   void initialize () {
   Map<String, String> m = this.machine.components();
	  for(String key: m.keySet()) {
	 String value = m.get(key);
		
	 switch (value) {
	  case "computer":
		this.computer_address = key;
		break;
	  case "gpu":
		this.gpu.address = key;
		break;
	  case "screen":
		this.gpu.screen_address = key;
		this.screen_address = key;
		break;
	  case "eeprom":
		this.eeprom_address = key;
		break;
	  case "keyboard":
		this.gpu.keyboard_address = key;
		break;
	 }

	  System.out.printf("Found \"%s\": \"%s\".\n", value, key);
	  }

	  if(screen_address != null && gpu.address != null) {
	 try {
		machine.invoke(gpu.address, "bind", new Object[]{screen_address});
	 } catch (Throwable t) {
		System.out.printf( "%s\n", t.toString() );
	 }
	  }

	  if(gpu.address != null) {
	 try {
		machine.invoke(gpu.address, "setForeground", new Object[]{0xFECC1F, false});
		machine.invoke(gpu.address, "setBackground", new Object[]{0x000000, false});
		
		
		gpu.initialize(machine);
		this.sedna_vm.gpu = this.gpu;
		this.sedna_vm.machine = this.machine;
		this.sedna_vm.eeprom_address = this.eeprom_address;
		this.sedna_vm.SednaVMRunner();
	 } catch (Throwable t) {
		System.out.printf( "%s\n", t.toString() );
	 }
	  } else {
	 System.out.printf( "gpu.address is null.\n" );
	  }
	  
	  return;
   }
}
