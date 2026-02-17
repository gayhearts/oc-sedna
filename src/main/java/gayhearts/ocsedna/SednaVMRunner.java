package gayhearts.ocsedna;

import gayhearts.ocsedna.api.API;

import li.cil.sedna.Sedna;
import li.cil.sedna.api.Sizes;
import li.cil.sedna.api.device.BlockDevice;
import li.cil.sedna.api.device.PhysicalMemory;
import li.cil.sedna.buildroot.Buildroot;
import li.cil.sedna.device.block.ByteBufferBlockDevice;
import li.cil.sedna.device.memory.Memory;
import li.cil.sedna.device.rtc.GoldfishRTC;
import li.cil.sedna.device.rtc.SystemTimeRealTimeCounter;
import li.cil.sedna.device.serial.UART16550A;
import li.cil.sedna.device.virtio.VirtIOBlockDevice;
//import li.cil.sedna.device.virtio.VirtIOFileSystemDevice;
//import li.cil.sedna.fs.HostFileSystem;
import li.cil.sedna.riscv.R5Board;
//import li.cil.sedna.riscv.R5CPU;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

// OpenComputers
import li.cil.oc.api.machine.Machine;

public class SednaVMRunner {
	public static int MEMORY_MBYTES = 32;
	public static int MEMORY_BYTES  = MEMORY_MBYTES * 1024 * 1024;

	public static int VM_CPU_FREQUENCY = 25_000_000;

	public Machine machine;
	public String eeprom_address = "";
	public OpenComputersGPU gpu;

	public R5Board board = new R5Board();
	public PhysicalMemory memory = Memory.create(MEMORY_BYTES);
	public GoldfishRTC rtc = new GoldfishRTC(SystemTimeRealTimeCounter.get());
	public UART16550A uart = new UART16550A();

	public Images images;

	public void SednaVMStep() {
		int remaining = 0;
		if( board.isRunning() ) {
			final int cycles_per_second = board.getCpu().getFrequency();
			final int cycles_per_step = 1_000;

			remaining = cycles_per_second;
			while (remaining > 0) {
				board.step(cycles_per_step);
				remaining -= cycles_per_step;

				int value;
				while ((value = uart.read()) != -1) {
					this.gpu.WriteChar((char) value);
					//API.Logger.InfoPrintf( "%c", (char) value );
					//System.out.print((char) value);
				}

				ProcessInput( gpu.GetInput() );

				//while (br.ready() && builtinDevices.uart.canPutByte()) {
				//	builtinDevices.uart.putByte((byte) br.read());
				//}


				if (board.isRestarting()) {
					try {
						loadProgramFile(memory, ReadFlash(this.machine, this.eeprom_address));
						loadProgramFile(memory, images.kernel(), 0x200000);

						board.initialize();
					} catch( Exception exception ){
						this.gpu.WriteString( "%s\n" + exception.toString() );
					}
				}
			}
		}

	}

	// Keep these held in-class to close later.
	private BlockDevice bootfs    = null;
	private VirtIOBlockDevice vda = null;

	private BlockDevice rootfs    = null;
	private VirtIOBlockDevice vdb = null;

	public void CloseDevices(){
		try{
			if( bootfs != null ){
				bootfs.close();
			}
		} catch( IOException exception ){
			API.Logger.Info( exception.toString() );
		}

		try{
			if( vda != null ){
				vda.close();
			}
		} catch( IOException exception ){
			API.Logger.Info( exception.toString() );
		}

		try{
			if( rootfs != null ){
				rootfs.close();
			}
		} catch( IOException exception ){
			API.Logger.Info( exception.toString() );
		}

		try{
			if( vdb != null ){
				vdb.close();
			}
		} catch( IOException exception ){
			API.Logger.Info( exception.toString() );
		}
	}

	public void SednaVMRunner() throws Exception {
		this.gpu.WriteString("starting VM\n");
		Sedna.initialize();

		// Get Minux images.
		images = getImages();
		this.gpu.WriteString("images gotten\n");



		// mount bootfs for first block device (vda)
		//   can we add this to context?
		bootfs = ByteBufferBlockDevice.createFromStream(images.bootfs(), true);
		vda = new VirtIOBlockDevice(board.getMemoryMap(), bootfs);
		vda.getInterrupt().set(0x1, board.getInterruptController());
		board.addDevice(vda);

		rootfs = ByteBufferBlockDevice.createFromStream(images.rootfs(), false);
		vdb = new VirtIOBlockDevice(board.getMemoryMap(), rootfs);
		vdb.getInterrupt().set(0x2, board.getInterruptController());
		board.addDevice(vdb);

		this.gpu.WriteString("drives added\n");

		uart.getInterrupt().set(0xA, board.getInterruptController());
		rtc.getInterrupt().set(0xB, board.getInterruptController());

		board.addDevice(0x80000000L, memory);
		board.addDevice(uart);
		board.addDevice(rtc);

		board.getCpu().setFrequency(VM_CPU_FREQUENCY);
		board.setBootArguments("root=/dev/vda rw");
		board.setStandardOutputDevice(uart);

		board.reset();

		this.gpu.WriteString("loading firmware\n");
		// Add device firmware.
		loadProgramFile(memory, ReadFlash(this.machine, this.eeprom_address));
		loadProgramFile(memory, images.kernel(), 0x200000);
		this.gpu.WriteString("Firmware loaded!\n");

		try {
			board.initialize();
		} catch( Exception exception ){
			this.gpu.WriteString( exception.toString() + '\n' );
			return;
		}

		this.gpu.WriteString("board initialized\n");

		board.setRunning(true);
	}

	private void ProcessInput( int input ){
		if( input != '\0' ){
			//System.out.printf("%d - %c\n", user_input, (char)KeyCodes.lwjgl_keys[user_input]);

			switch( KeyCodes.lwjgl_keys[input] ){
				case KeyCodes.SPECIAL_RETURN:
					uart.putByte((byte)'\n');
					break;
				case KeyCodes.SPECIAL_UP:
					uart.putByte((byte)0x1B);
					uart.putByte((byte)'[');
					uart.putByte((byte)'A');
					break;
				case KeyCodes.SPECIAL_LEFT: //left
					uart.putByte((byte)0x1B);
					uart.putByte((byte)'[');
					uart.putByte((byte)'D');
					break;
				case KeyCodes.SPECIAL_RIGHT: //right
					uart.putByte((byte)0x1B);
					uart.putByte((byte)'[');
					uart.putByte((byte)'C');
					break;
				case KeyCodes.SPECIAL_DOWN: //down
					uart.putByte((byte)0x1B);
					uart.putByte((byte)'[');
					uart.putByte((byte)'B');
					break;
				default:
					uart.putByte((byte)KeyCodes.lwjgl_keys[input]);
			}
		}
	}

	private static InputStream ReadFlash(Machine machine, String address) {
		try {
			Object[] size   = machine.invoke(address, "getSize", new Object[]{});
			Object[] buffer = machine.invoke(address, "get", new Object[]{});

			if( size[0] instanceof Integer && buffer[0] instanceof byte[] ) {
				return new ByteArrayInputStream((byte[])buffer[0], 0, (int)size[0]);
			} else {
				return new ByteArrayInputStream(null);
			}
		} catch( Exception exception ){
			API.Logger.Info( exception.toString() );
		}


		return new ByteArrayInputStream(null);
	}

	private static void loadProgramFile(final PhysicalMemory memory, final InputStream stream) throws Exception {
		loadProgramFile(memory, stream, 0);
	}

	private static void loadProgramFile(final PhysicalMemory memory, final InputStream stream, final int offset) throws IOException {
		final BufferedInputStream bis = new BufferedInputStream(stream);
		for (int address = offset, value = bis.read(); value != -1; value = bis.read(), address++) {
			memory.store(address, (byte) value, Sizes.SIZE_8_LOG2);
		}
	}

	private static Images getImages() throws IOException {
		return new Images(
				Buildroot.getFirmware(),
				Buildroot.getLinuxImage(),
				Buildroot.getBootFilesystem(),
				Buildroot.getRootFilesystem());
	}

	private record Images(InputStream firmware, InputStream kernel, InputStream bootfs, InputStream rootfs) { }
}

