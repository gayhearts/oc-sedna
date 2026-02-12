package gayhearts.ocsedna.firmware;

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

public class FlashMemoryInit {
    public static ItemStack Setup() {
        // Initialize 
        byte[] data = {'h', 'r', 'm', 'm', 'm', 'm', '.'};
        byte[] code = OpenSBI.GetDynamicFirmware();

        // Create data.
        FlashMemoryItem flash = new FlashMemoryItem();
        ItemStack bare_stack = flash.createItemStack(1);
        ItemStack osbi_stack = flash.createItemStack("OpenSBI", code, data, false, 1);

        // Register bare flash.
        GameRegistry.registerItem(flash, "flash");
        GameRegistry.registerCustomItemStack("flash", bare_stack);
        OreDictionary.registerOre("ocsedna:flash", flash);

        // Register OpenSBI.
        GameRegistry.registerCustomItemStack("flash", osbi_stack);
        OreDictionary.registerOre("ocsedna:flash",    osbi_stack);

        // Add OpenSBI Recipe.
        ArrayList<Object> item_type = new ArrayList<Object>();
        item_type.add(bare_stack);
        Seq<Object> flash_item_seq = JavaConverters.asScalaIteratorConverter(item_type.iterator()).asScala().toSeq();

        GameRegistry.addRecipe(new ExtendedShapelessOreRecipe(osbi_stack, flash_item_seq));

        return bare_stack;
    }
}
