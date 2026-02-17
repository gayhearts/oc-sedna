package gayhearts.ocsedna.firmware;

import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Slot;
import net.minecraft.item.ItemStack;
import li.cil.oc.api.prefab.DriverItem;

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
