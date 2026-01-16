package gayhearts.ocsedna;

import li.cil.sedna.*;
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
import li.cil.sedna.device.virtio.VirtIOFileSystemDevice;
import li.cil.sedna.fs.HostFileSystem;
import li.cil.sedna.riscv.R5Board;
import li.cil.sedna.riscv.R5CPU;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.*;
import java.util.Arrays;
import java.util.List;


public class SednaVMRunner {
	public static int VM_MEMORY_MEGABYTES = 32;
	public static int VM_MEMORY_BYTES = (VM_MEMORY_MEGABYTES * 1024 * 1024);

	public static int VM_CPU_FREQUENCY = 25_000_000;

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
				System.out.print((char) value);
			}

			//while (br.ready() && builtinDevices.uart.canPutByte()) {
			//	builtinDevices.uart.putByte((byte) br.read());
			//}
			

			if (board.isRestarting()) {
				try {
					loadProgramFile(memory, images.firmware());
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
				//final GlobalVMContext context = new GlobalVMContext(board);
		//final BuiltinDevices builtinDevices;

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
		loadProgramFile(memory, images.firmware());
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

