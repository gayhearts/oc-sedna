package gayhearts.ocsedna.memory;

import scala.collection.JavaConverters;
import scala.collection.Seq;

// Java
import static java.util.Map.entry;
import java.util.Map;
import java.util.ArrayList;
import java.io.InputStream;

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
import li.cil.oc.api.Driver;
import li.cil.oc.common.recipe.ExtendedShapelessOreRecipe;
//import li.cil.oc.common.item;

import li.cil.oc.api.machine.Context;
//import li.cil.oc.api.machine.

// MC
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class MemoryInit {
    public static ItemStack Setup(String name) {
		// Create data.
        MemoryItem mem = new MemoryItem(name);
        ItemStack mem_stack = new ItemStack(mem, 1);

        // Register bare flash.
        GameRegistry.registerItem(mem, name);
        GameRegistry.registerCustomItemStack(name, mem_stack);
        OreDictionary.registerOre("ocsedna:" + name, mem);
        
        return mem_stack;
    }
}
