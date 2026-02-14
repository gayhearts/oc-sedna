package gayhearts.ocsedna.memory;

import li.cil.oc.Constants;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Slot;
import li.cil.oc.common.Tier;
import net.minecraft.item.ItemStack;
import li.cil.oc.api.driver.Item;
import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.Driver;
import li.cil.oc.api.prefab.DriverItem;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.Network;
import li.cil.oc.api.Items;
import li.cil.oc.api.detail.ItemInfo;

import li.cil.oc.integration.opencomputers.DriverMemory;
//import li.cil.oc.common.item.Memory;
import li.cil.oc.api.driver.item.Memory;

import gayhearts.ocsedna.SednaInitialization;
import gayhearts.ocsedna.firmware.FlashMemoryBase;
import gayhearts.ocsedna.firmware.FlashMemoryItem;


public class MemoryDriver extends DriverItem implements Memory {
	// size - Size in Kibibytes.
	// name - Name for textures and such.
	public final int size;
	public final String name;

	public MemoryDriver(int size, String name) {
		this.size = size;
		this.name = name;
		super(MemoryInit.Setup(name));
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		if (host.world()  != null && host.world().isRemote) {
			return null;
		} else {
			MemoryBase mem = new MemoryBase(host, 1);
			return mem;
		}
	}

	@Override
	public String slot(ItemStack stack){
		return Slot.Memory();
	}
	
	@Override
	public double amount(ItemStack stack){
		System.out.println("amount");
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
