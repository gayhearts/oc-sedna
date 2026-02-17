package gayhearts.ocsedna.firmware;

// Java
import static java.util.Map.entry;
import java.util.Map;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import java.util.zip.CRC32;

// OpenComputers
import li.cil.oc.api.driver.DeviceInfo;
//import li.cil.oc.Settings;
import li.cil.oc.api.prefab.ManagedEnvironment;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.machine.Callback;

// for node
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.Network;

// MC
import net.minecraft.nbt.NBTTagCompound;

public class FlashMemoryBase extends ManagedEnvironment implements DeviceInfo {
	protected final EnvironmentHost host;

	public FlashMemoryBase(EnvironmentHost host) {
		this.host = host;
	}

	public void init() {
		this.setNode(Network.newNode(this, Visibility.Neighbors).
				withComponent("eeprom", Visibility.Neighbors).
				withConnector().
				create());
	}

	private static Integer eeprom_size    = 524288;

	private static Integer MIN_LABEL_SIZE = 1;
	private static Integer MAX_LABEL_SIZE = 24;
	//private static String  namespace   = "ocsedna";


	// Default EEPROM data.
	protected ByteBuffer codeData     = ByteBuffer.allocate(eeprom_size);
	protected ByteBuffer volatileData = ByteBuffer.allocate(eeprom_size);
	protected Boolean    readonly     = false;
	protected String     label        = "FlashMemory";

	public String checksum() {
		//System.out.println("checksum");
		CRC32 hash = new CRC32();
		hash.update(codeData);
		return hash.toString();
	}

	protected static Map<String, String> deviceInfo() {
		//System.out.println("deviceInfo");
		return Map.ofEntries(
				entry(DeviceAttribute.Class,       DeviceClass.Memory),
				entry(DeviceAttribute.Description, "EEPROM"),
				entry(DeviceAttribute.Vendor,      "gayhearts"),
				entry(DeviceAttribute.Product,     "FlashStick512K"),
				entry(DeviceAttribute.Capacity,    eeprom_size.toString()),
				entry(DeviceAttribute.Size,        eeprom_size.toString())
				);
	}

	@Callback(direct = true, doc = "function():string -- Get the currently stored byte array.")
	public Object[] get(Context context, Arguments args) {
		//System.out.println("get");
		return new Object[] {this.codeData.array()};
	}

	@Callback(direct = true, doc = "function():string -- Get the label of the EEPROM.")
	public Object[] getLabel(Context context, Arguments args) {
		//System.out.println("getLabel");
		return new Object[] {this.label};
	}

	@Callback(doc = "function(data:string):string -- Set the label of the EEPROM.")
	public Object[] setLabel(Context context, Arguments args) {
		//System.out.println("setLabel");
		if( this.readonly ){
			return new Object[] {"Storage is readonly."};
		}

		String new_label = args.optString(0, "FlashMemory");

		if( new_label.length() > MAX_LABEL_SIZE ){
			this.label = new_label.substring( 0, MAX_LABEL_SIZE );
		} else if( new_label.length() >= MIN_LABEL_SIZE ){
			this.label = new_label;
		}

		return new Object[] {this.label};
	}

	@Callback(direct = true, doc = "function():number -- Get the storage capacity of this EEPROM.")
	public Object[] getSize(Context context, Arguments args) {
		//System.out.println("getSize");
		return new Object[] {eeprom_size};
	}

	@Callback(direct = true, doc = "function():string -- Get the checksum of the data on this EEPROM.")
	public Object[] getChecksum(Context context, Arguments args) {
		//System.out.println("getChecksum");
		return new Object[]{ checksum() };
	}

	@Callback(direct = true, doc = "function():number -- Get the storage capacity of this EEPROM.")
	public Object[] getDataSize(Context context, Arguments args) {
		//System.out.println("getDataSize");
		return new Object[]{ eeprom_size };
	}

	@Callback(direct = true, doc = "function(checksum:string):boolean -- Make this EEPROM readonly if it isn't already. This process cannot be reversed!")
	public Object[] makeReadonly(Context context, Arguments args) {
		if( args.checkString(0).equals( this.checksum() ) ){
			this.readonly = true;
			return new Object[]{ true };
		} else{
			return new Object[]{ "Incorrect checksum." };
		}
	}
	
	@Callback(direct = true, doc = "function():string -- Get the currently stored byte array.")
	public Object[] getData(Context context, Arguments args) {
		//System.out.println("getData");
		return new Object[] {volatileData.array()};
	}

	@Callback(doc = "function(data:string) -- Overwrite the currently stored byte array.")
	public Object[] setData(Context context, Arguments args) {
		//System.out.println("setData");
		//if (!node().tryChangeBuffer(-Settings.get().eepromWriteCost())) {
		//    return new Object[] {"not enough energy"};
		//} 

		byte[] new_data = args.optByteArray(0, new byte[eeprom_size]);

		if (new_data.length > eeprom_size) {
			throw new IllegalArgumentException("not enough space");
		}

		volatileData.clear();
		volatileData.put(new_data);

		context.pause(1);

		return new Object[] {};
	}


	@Override
	public Map<String, String> getDeviceInfo() {
		//System.out.println("getDeviceInfo");
		return deviceInfo();
	}

	@Override
	public void load(NBTTagCompound nbt) {
		//System.out.println("load");
		super.load(nbt);
		codeData.clear();
		codeData.put(nbt.getByteArray("oc:eeprom"));

		if (nbt.hasKey("oc:label")) {
			label = nbt.getString("oc:label");
		}

		readonly = nbt.getBoolean("oc:readonly");

		volatileData.clear();
		volatileData.put(nbt.getByteArray("oc:userdata"));
	}


	@Override
	public void save(NBTTagCompound nbt) {
		//System.out.println("save");
		super.save(nbt);

		nbt.setByteArray("oc:eeprom", codeData.array());
		nbt.setString("oc:label", label);
		nbt.setBoolean("oc:readonly", readonly);
		nbt.setByteArray("oc:userdata", volatileData.array());
	}

	public InputStream getStream() {
		//System.out.println("getStream");
		return new ByteArrayInputStream(this.codeData.array());
	}
}
