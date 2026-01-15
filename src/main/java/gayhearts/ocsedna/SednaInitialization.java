package gayhearts.ocsedna;

import li.cil.oc.api.Machine;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "OpenComputers|Sedna",
	name = "OpenComputers Sedna",
	version	= "0.0.1",
	dependencies = "required-after:OpenComputers@[1.4.0,)")

public class SednaInitialization {
	@Mod.Instance
	public static SednaInitialization instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Machine.add(SednaArchitecture.class);
	}
}
