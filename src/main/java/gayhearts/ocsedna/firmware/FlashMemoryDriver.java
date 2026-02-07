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

import gayhearts.ocsedna.SednaInitialization;
import gayhearts.ocsedna.firmware.FlashMemory;


public class FlashMemoryDriver extends DriverItem {
    public FlashMemoryDriver() {
        super(new ItemStack(SednaInitialization.flash.flash_item));
    }

    @Override
    public String slot(ItemStack stack) {
        return Slot.EEPROM();
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container) {
		return new Environment(container);
    }


    public class Environment extends FlashMemory {
        protected EnvironmentHost container;

        public Environment(EnvironmentHost container) {
            this.container = container;
        }
    }
}
