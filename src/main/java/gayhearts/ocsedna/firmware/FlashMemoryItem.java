package gayhearts.ocsedna.firmware;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public class FlashMemoryItem extends Item {
    public void FlashMemoryItem() {
        this.setUnlocalizedName("flash");
        this.setTextureName("ocsedna:flash");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
		this.setMaxStackSize(1);
        this.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
    }

    @Override 
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("oc:data")) {
                NBTTagCompound data = tag.getCompoundTag("oc:data");
                    if (data.hasKey("oc:label")) {
                        return data.getString("oc:label");
                    }
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    @SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("ocsedna:flash");
	}
}
