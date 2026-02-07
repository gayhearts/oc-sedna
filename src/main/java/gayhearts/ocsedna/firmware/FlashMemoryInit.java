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
    public FlashMemoryItem flash_item;
    public Seq<Object>     flash_item_seq;
    public ItemStack       opensbi;

    public FlashMemoryInit() {
        /// Add to OC???
        this.flash_item = new FlashMemoryItem();

        
        ArrayList<Object> item_type = new ArrayList<Object>();
        item_type.add(new ItemStack(flash_item));
        this.flash_item_seq = JavaConverters.asScalaIteratorConverter(item_type.iterator()).asScala().toSeq();

        
        byte[] code = {'h', 'r', 'm', 'm', 'm', 'm', '.'};
        byte[] data = {'t', 'e', 's', 't', 'i', 'n', 'g'};
       
        this.opensbi = CreateFlash(this.flash_item, "OpenSBI", code, data, true);
       

        // Main item.
        GameRegistry.registerItem(this.flash_item, "flash");
        OreDictionary.registerOre("ocsedna:flash", this.flash_item);

        // OpenSBI Stack.
        GameRegistry.registerCustomItemStack("flash", this.opensbi);
        OreDictionary.registerOre("ocsedna:flash",    this.opensbi);
    }

    public void RegisterRecipes() {
        GameRegistry.addRecipe(new ExtendedShapelessOreRecipe(this.opensbi, this.flash_item_seq));
    }

    public static ItemStack CreateFlash(Item item, String name, byte[] code, byte[] data, boolean readonly){
        NBTTagCompound nbt = new NBTTagCompound();

        if( name != null ) {
            nbt.setString("oc:label", name);
        }

        if( code != null ) {
            nbt.setByteArray("oc:eeprom", code);
        }

        if( data != null ) {
            nbt.setByteArray("oc:userdata", data);
        }

        nbt.setBoolean("oc:readonly", readonly);

        NBTTagCompound stackNbt = new NBTTagCompound();
        stackNbt.setTag("oc:data", nbt);

        ItemStack stack = new ItemStack(item);
        stack.setTagCompound(stackNbt);

        stack.copy();

        return stack;
    }
}
