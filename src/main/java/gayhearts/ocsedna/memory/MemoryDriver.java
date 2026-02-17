package gayhearts.ocsedna.memory;

import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Slot;
import net.minecraft.item.ItemStack;
import li.cil.oc.api.prefab.DriverItem;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

//import li.cil.oc.common.item.Memory;
import li.cil.oc.api.driver.item.Memory;

public class MemoryDriver extends DriverItem implements Memory {
	// size - Size in Kibibytes.
	// name - Name for textures and such.
	public final int size;
	public final String name;

	public MemoryDriver(int size, String name) {
		this.size = size;
		this.name = name;

		// Create data.
		MemoryItem memory = new MemoryItem(name);
		ItemStack  stack  = new ItemStack(memory, 1);

		// Register bare flash.
		GameRegistry.registerItem(memory, name);
		GameRegistry.registerCustomItemStack(name, stack);
		OreDictionary.registerOre("ocsedna:" + name, memory);

		super(stack);
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		if (host.world()  != null && host.world().isRemote) {
			return null;
		} else {
			return new MemoryBase(host, 1);
		}
	}

	@Override
	public String slot(ItemStack stack){
		return Slot.Memory();
	}

	@Override
	public double amount(ItemStack stack){
		//System.out.println("amount");
		// Amount seems to be in kibibytes.
		// E.G. `return 1000` results in 1024000.
		return this.size;
	}

	@Override
	public int tier(ItemStack stack) {
		// One higher than OpenComputers EEPROM.
		return 1;
	}
}
