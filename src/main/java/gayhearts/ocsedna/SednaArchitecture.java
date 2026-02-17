package gayhearts.ocsedna;

import li.cil.oc.api.Driver;

import li.cil.oc.api.driver.item.Memory;

import li.cil.oc.api.driver.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.machine.Architecture;
//import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.ExecutionResult;
//import li.cil.oc.api.machine.LimitReachedException;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.Signal;


/** This is the class you implement; Architecture is from the OC API. */
@Architecture.Name("Sedna")
public class SednaArchitecture implements Architecture {
	private final Machine machine;

	private SednaVM vm;

	/** The constructor must have exactly this signature. */
	public SednaArchitecture(Machine machine) {
		this.machine = machine;
	}

	@Override
	public boolean isInitialized() { return true; }

	@Override
	public boolean initialize() {
		// Set up new VM here, and register all API callbacks you want to
		// provide to it.
		vm = new SednaVM();
		vm.machine = machine;

		vm.initialize();

		// ... more callbacks.
		return true;
	}

	@Override
	public void close() {
		vm = null;
	}

	@Override
	public boolean recomputeMemory(Iterable<ItemStack> components) {
		//li.cil.oc.api.machine.MachineHost.internalComponents()

		//System.out.println("recomputeMemory");
		for( ItemStack stack: components ){
			//System.out.println("found: " + stack.toString());

			Item   device  = Driver.driverFor(stack);
			String address = null;

			if( device instanceof Memory ){
				NBTTagCompound tag = device.dataTag(stack);

				if( tag != null ){
					NBTTagCompound node = tag.getCompoundTag("node");

					if( node != null ){
						address = node.getString("address");
					} else{
						continue;
					}
				} else{
					continue;
				}

				Memory mem = (Memory) device;
				//System.out.printf("Memory of size: %s", mem.amount(stack));
			
				if( address != null ){
					//System.out.printf(", address: %s(%d)\n", address, machine.host().componentSlot(address));
				} else{
					//System.out.printf("\n");
				}
			}
		}
		return true;
	}

	@Override
	public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
		// Perform stepping in here. Usually you'll want to resume the VM
		// by passing it the next signal from the queue, but you may decide
		// to allow your VM to poll for signals manually.
		try { 
			final Signal signal;
			if (isSynchronizedReturn) {
				// Don't pull signals when we're coming back from a sync call,
				// since we're in the middle of something else!
				signal = null;
			}
			else {
				signal = machine.popSignal();
			}
			final Object[] result;
			if (signal != null) {
				result = vm.run(new Object[]{signal.name(), signal.args()});
			}
			else {
				result = vm.run(null);
			}

			Thread.sleep(100_000);

			// You'll want to define some internal protocol by which to decide
			// when to perform a synchronized call. Let's say we expect the VM
			// to return either a number for a sleep, a boolean to indicate
			// shutdown/reboot and anything else a pending synchronous call.
			if (result != null) {
				if (result[0] instanceof Boolean) {
					return new ExecutionResult.Shutdown((Boolean)result[0]);
				}
				if (result[0] instanceof Integer) {
					return new ExecutionResult.Sleep((Integer)result[0]);
				}
			}
			// If this is returned, the next 'resume' will be runSynchronized.
			// The next call to runThreaded after that call will have the
			// isSynchronizedReturn argument set to true.
			return new ExecutionResult.SynchronizedCall();
		}
		catch (Throwable t) {
			return new ExecutionResult.Error(t.toString());
		}
	}

	@Override
	public void runSynchronized() {
		// Synchronized calls are run from the MC server thread, making it
		// easier for callbacks to interact with the world (because sync is
		// taken care for them by the machine / architecture).
		// This means that if some code in the VM starts a sync call it has
		// to *pause* and relinquish control to the host, where we then
		// switch to sync call mode (see runThreaded), wait for the MC server
		// thread, and then do the actual call. It'd be possible to pass the
		// info required for the call out in runThreaded, keep it around in
		// the arch and do the call directly here. For this example, let's
		// assume the state info is kept inside the VM, and the next resume
		// makes it perform the *actual* call. For some pseudo-code handling
		// this in the VM, see below.
		//vm.run(null);
	}

	@Override
	public void onConnect(){
		// TODO: Currently unimplemented.
	}

	// Use this to load the VM state, if it can be persisted.
	@Override
	public void load( NBTTagCompound nbt ){
		// TODO: Currently unimplemented.
	}

	// Use this to save the VM state, if it can be persisted.
	@Override
	public void save( NBTTagCompound nbt ){
		// TODO: Currently unimplemented.
	}

	@Override
	public void onSignal(){
		// TODO: Currently unimplemented.
	}
}
