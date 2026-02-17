package gayhearts.ocsedna.memory;

// Java
import static java.util.Map.entry;
import java.util.Map;

// OpenComputers
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.Settings;
import li.cil.oc.api.prefab.ManagedEnvironment;

import li.cil.oc.api.network.EnvironmentHost;

// for node
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.Network;

public class MemoryBase extends ManagedEnvironment implements DeviceInfo {
	protected final EnvironmentHost host;
	protected final int tier;
	protected final double clock;

	MemoryBase(EnvironmentHost host, int tier) {
		this.host = host;
		this.tier = tier;
		this.clock = (Settings.get().callBudgets()[tier] * 1000);

		this.setNode(Network.newNode(this, Visibility.Neighbors).
				withComponent("memory", Visibility.Neighbors).
				withConnector().
				create());
	}

	protected final Map<String, String> deviceInfo() {
		//System.out.println("deviceInfo");
		return Map.ofEntries
			(
			 entry(DeviceAttribute.Class,       DeviceClass.Memory),
			 entry(DeviceAttribute.Description, "Memory Bank"),
			 entry(DeviceAttribute.Vendor,      "gayhearts"),
			 entry(DeviceAttribute.Product,     "Multipurpose RAM Type"),
			 entry(DeviceAttribute.Clock,       String.valueOf(clock))
			);
	}

	@Override
	public Map<String, String> getDeviceInfo() {
		//System.out.println("getDeviceInfo");
		return deviceInfo();
	}
}
