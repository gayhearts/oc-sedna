package gayhearts.ocsedna.firmware;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FlashMemoryItem extends Item {
	FlashMemoryItem() {
		this.setUnlocalizedName("flash");
		this.setTextureName("ocsedna:flash");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
	}

	public ItemStack createItemStack() {
		return createItemStack("FlashMemory", null, null, false, 1);
	}

	public ItemStack createItemStack(int count) {
		return createItemStack("FlashMemory", null, null, false, count);
	}

	// camelCase to fit in with Item data type.
	public ItemStack createItemStack(String name, byte[] code, byte[] data, boolean readonly, int count){
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

		NBTTagCompound nbt_wrapped = new NBTTagCompound();
		nbt_wrapped.setTag("oc:data", nbt);

		ItemStack stack = new ItemStack(this, count);
		stack.setTagCompound(nbt_wrapped);

		stack.copy();

		return stack;
	}

	@Override 
	public String getItemStackDisplayName( final ItemStack stack ){
		String parent_name = super.getItemStackDisplayName( stack );

		NBTTagCompound tag;
		NBTTagCompound data;
		
		if( stack.hasTagCompound() ){
			tag = stack.stackTagCompound;
		} else{
			return parent_name;
		}

		if( tag.hasKey("oc:data") ){
			data = tag.getCompoundTag("oc:data");
		} else{
			return parent_name;
		}

		if( data.hasKey("oc:label") ){
			return data.getString("oc:label");
		} else{
			return parent_name;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister Icon_Register)
	{
		this.itemIcon = Icon_Register.registerIcon("ocsedna:flash");
	}
}
