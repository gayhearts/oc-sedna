package gayhearts.ocsedna.firmware;


import scala.collection.JavaConverters;
import scala.collection.Seq;

// Java
import static java.util.Map.entry;
import java.util.Map;
import java.util.ArrayList;

// OpenComputers
import li.cil.oc.api.driver.DeviceInfo.DeviceAttribute;
import li.cil.oc.api.driver.DeviceInfo.DeviceClass;
import li.cil.oc.server.component.EEPROM;
import li.cil.oc.common.init.Items;
import li.cil.oc.common.item.Delegator;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.common.recipe.Recipes;
import li.cil.oc.Constants;
import li.cil.oc.Settings;
import li.cil.oc.api.prefab.ManagedEnvironment;
//import li.cil.oc.common.item;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Arguments;

// MC
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class FlashMemory extends EEPROM {
    private static Integer eeprom_size = 2097152;
    private static String  namespace   = "ocsedna";


    protected static Map<String, String> deviceInfo() {
        final Map<String, String> device_info = Map.ofEntries(
                entry(DeviceAttribute.Class,       DeviceClass.Memory),
                entry(DeviceAttribute.Description, "FlashMemory"),
                entry(DeviceAttribute.Vendor,      "gayhearts"),
                entry(DeviceAttribute.Product,     "FlashStick2M"),
                entry(DeviceAttribute.Capacity,    eeprom_size.toString()),
                entry(DeviceAttribute.Size,        eeprom_size.toString())
                );

        return device_info;
    };

    @Override
    public Object[] getSize(Context context, Arguments args) {
        return new Object[] {eeprom_size};
    }

    @Override
    public Map<String, String> getDeviceInfo() {
        return deviceInfo();
    }
}
