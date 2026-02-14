package gayhearts.ocsedna.firmware;

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

import gayhearts.ocsedna.SednaInitialization;
import gayhearts.ocsedna.firmware.FlashMemoryBase;
import gayhearts.ocsedna.firmware.FlashMemoryItem;


public class FlashMemoryDriver extends DriverItem {
	public FlashMemoryDriver() {
		super(FlashMemoryInit.Setup());
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		if (host.world()  != null && host.world().isRemote) {
			return null;
		} else {
			FlashMemoryBase flash = new FlashMemoryBase(host);
			flash.init();

			return flash;
		}
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.EEPROM();
	}

	@Override
	public int tier(ItemStack stack) {
		// One higher than OpenComputers EEPROM.
		return 1;
	}
}
