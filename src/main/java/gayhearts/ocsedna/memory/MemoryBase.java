package gayhearts.ocsedna.memory;


import scala.collection.JavaConverters;
import scala.collection.Seq;

// Java
import static java.util.Map.entry;
import java.util.Map;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.lang.String;

import java.util.zip.CRC32;

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
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.machine.Callback;

// for node
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;

// Sedna
import li.cil.sedna.device.flash.FlashMemoryDevice;

// MC
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MemoryBase extends ManagedEnvironment implements DeviceInfo {
	protected final EnvironmentHost host;
	protected static int tier;
	protected static double clock;
	
	MemoryBase(EnvironmentHost host, int tier) {
		this.host = host;
		MemoryBase.tier = tier;
		MemoryBase.clock = (Settings.get().callBudgets()[tier] * 1000);
		
		this.setNode(Network.newNode(this, Visibility.Neighbors).
					 withComponent("memory", Visibility.Neighbors).
					 withConnector().
					 create());
//		this.setNode(tmp);
	}


	private static String  namespace   = "ocsedna";

	protected static Map<String, String> deviceInfo() {
		System.out.println("deviceInfo");
		final Map<String, String> device_info = Map.ofEntries
		(
			entry(DeviceAttribute.Class,       DeviceClass.Memory),
			entry(DeviceAttribute.Description, "Memory Bank"),
			entry(DeviceAttribute.Vendor,      "gayhearts"),
			entry(DeviceAttribute.Product,     "Multipurpose RAM Type"),
			entry(DeviceAttribute.Clock,       String.valueOf(clock))
		);

		return device_info;
	};
	
	@Override
	public Map<String, String> getDeviceInfo() {
		System.out.println("getDeviceInfo");
		return deviceInfo();
	}
}
