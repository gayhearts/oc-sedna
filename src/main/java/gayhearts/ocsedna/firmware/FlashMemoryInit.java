package gayhearts.ocsedna.firmware;

import scala.collection.JavaConverters;
import scala.collection.Seq;

// Java
import java.util.List;
import java.util.ArrayList;

// OpenComputers
import li.cil.oc.common.recipe.ExtendedShapelessOreRecipe;
//import li.cil.oc.common.item;

//import li.cil.oc.api.machine.

// MC
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class FlashMemoryInit {
	private FlashMemoryInit(){}
	
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
		List<Object> item_type = new ArrayList<Object>();
		item_type.add(bare_stack);
		Seq<Object> flash_item_seq = JavaConverters.asScalaIteratorConverter(item_type.iterator()).asScala().toSeq();

		GameRegistry.addRecipe(new ExtendedShapelessOreRecipe(osbi_stack, flash_item_seq));

		return bare_stack;
	}
}
