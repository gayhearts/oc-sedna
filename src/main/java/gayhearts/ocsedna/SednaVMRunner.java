package gayhearts.ocsedna;

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
	public static int VM_MEMORY_MEGABYTES = 32;
	public static int VM_MEMORY_BYTES = (VM_MEMORY_MEGABYTES * 1024 * 1024);

	public static int VM_CPU_FREQUENCY = 25_000_000;

	public Machine machine;
	public String eeprom_address = "";
	public OpenComputersGPU gpu;

	public R5Board board = new R5Board();
	public PhysicalMemory memory = Memory.create(VM_MEMORY_BYTES);
	public GoldfishRTC rtc = new GoldfishRTC(SystemTimeRealTimeCounter.get());
	public UART16550A uart = new UART16550A();

	public Images images;

	public void SednaVMStep() {
		int remaining = 0;
		if( board.isRunning() ) {
			final int cyclesPerSecond = board.getCpu().getFrequency();
			final int cyclesPerStep = 1_000;

			remaining = cyclesPerSecond;
			while (remaining > 0) {
				board.step(cyclesPerStep);
				remaining -= cyclesPerStep;

				int value;
				while ((value = uart.read()) != -1) {
					this.gpu.WriteChar((char) value);
					//System.out.print((char) value);
				}

				int user_input = gpu.GetInput();
				if( user_input != '\0' ){
					////System.out.printf("%d - %c\n", user_input, (char)KeyCodes.lwjgl_keys[user_input]);

					switch (KeyCodes.lwjgl_keys[user_input]) {
						case KeyCodes.special_offset+28:
							uart.putByte((byte)'\n');
							break;
						case KeyCodes.special_offset+200: //up
							uart.putByte((byte)0x1B);
							uart.putByte((byte)'[');
							uart.putByte((byte)'A');
							break;
						case KeyCodes.special_offset+203: //left
							uart.putByte((byte)0x1B);
							uart.putByte((byte)'[');
							uart.putByte((byte)'D');
							break;
						case KeyCodes.special_offset+205: //right
							uart.putByte((byte)0x1B);
							uart.putByte((byte)'[');
							uart.putByte((byte)'C');
							break;
						case KeyCodes.special_offset+208: //down
							uart.putByte((byte)0x1B);
							uart.putByte((byte)'[');
							uart.putByte((byte)'B');
							break;
						default:
							uart.putByte((byte)KeyCodes.lwjgl_keys[user_input]);
					}
				}


				//while (br.ready() && builtinDevices.uart.canPutByte()) {
				//	builtinDevices.uart.putByte((byte) br.read());
				//}


				if (board.isRestarting()) {
					try {
						loadProgramFile(memory, ReadFlash(this.machine, this.eeprom_address));
						loadProgramFile(memory, images.kernel(), 0x200000);

						board.initialize();
					} catch (Throwable t) {
						this.gpu.WriteString("%s\n" + t.toString());
					}
				}
			}
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
		final BlockDevice bootfs = ByteBufferBlockDevice.createFromStream(images.bootfs(), true);
		final VirtIOBlockDevice vda = new VirtIOBlockDevice(board.getMemoryMap(), bootfs);
		vda.getInterrupt().set(0x1, board.getInterruptController());
		board.addDevice(vda);

		final BlockDevice rootfs = ByteBufferBlockDevice.createFromStream(images.rootfs(), false);
		final VirtIOBlockDevice vdb = new VirtIOBlockDevice(board.getMemoryMap(), rootfs);
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
		} catch (Throwable t) {
			this.gpu.WriteString(t.toString() + '\n');
			return;
		}

		this.gpu.WriteString("board initialized\n");

		board.setRunning(true);
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
		} catch (Throwable thrown) {
			//System.out.println(thrown.toString());
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

