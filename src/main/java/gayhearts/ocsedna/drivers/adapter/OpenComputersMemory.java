package gayhearts.ocsedna.drivers.adapter;

import li.cil.sedna.api.device.PhysicalMemory;
import net.minecraft.item.ItemStack;

public class OpenComputersMemory{
	private final li.cil.oc.api.driver.item.Memory driver;

	private final ItemStack      stack;
	private final Integer        size;
	private final PhysicalMemory device;

	public OpenComputersMemory( li.cil.oc.api.driver.item.Memory driver, ItemStack stack ){
		double tmp = driver.amount( stack );
		Integer size;

		if( tmp > Integer.MIN_VALUE && tmp < Integer.MAX_VALUE ){
			size = (int) tmp;
		} else{
			throw new IllegalArgumentException( String.format("Value outside of Integer bounds.") );
		}

		this.driver = driver;
		this.stack  = stack;
		this.size   = size;
		this.device = li.cil.sedna.device.memory.Memory.create(size);
	}

	public Integer GetSize(){
		return size;
	}

	public PhysicalMemory GetDevice(){
		return device;
	}

	public ItemStack GetStack(){
		return stack;
	}

	public li.cil.oc.api.driver.item.Memory GetDriver(){
		return driver;
	}
}
