package gayhearts.ocsedna.memory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MemoryItem extends Item {
	private final String name;

    MemoryItem(String name) {
		if( name != null && name.length() > 0 ){
			this.name = name;
	        this.setUnlocalizedName(this.name);
		    this.setTextureName("ocsedna:" + this.name);
	        this.setHasSubtypes(true);
		    this.setMaxDamage(0);
			this.setMaxStackSize(1);
	        this.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
		} else{
			if( name == null ) {
				throw new IllegalArgumentException("argument \"name\" is null.");
			} else if( name.length() <= 0 ){
				throw new IllegalArgumentException("argument \"name\" is empty.");
			} else{
				throw new IllegalArgumentException("unknown error with argument \"name\".");
			}
		}
	}

    @SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("ocsedna:" + this.name);
	}
}
