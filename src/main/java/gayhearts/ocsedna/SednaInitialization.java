package gayhearts.ocsedna;

// ocsedna
import gayhearts.ocsedna.firmware.FlashMemoryInit;
import gayhearts.ocsedna.firmware.FlashMemoryDriver;

// OpenComputers
import li.cil.oc.api.Machine;

// MC
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ocsedna",
	name         = "OpenComputersSedna",
	version	     = "0.0.1",
	dependencies = "required-after:OpenComputers@[1.12.12-GTNH,)")

public class SednaInitialization {
	@Mod.Instance
	public static SednaInitialization instance;

    public static FlashMemoryInit flash;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Machine.add(SednaArchitecture.class);
    
        flash = new FlashMemoryInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        flash.RegisterRecipes();

        li.cil.oc.api.Driver.add(new FlashMemoryDriver());
    }
}
