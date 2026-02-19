package gayhearts.ocsedna.drivers.minecraft;

import gayhearts.ocsedna.api.API;
import li.cil.sedna.api.device.rtc.RealTimeCounter;

public final class TotalWorldTimeRTC implements RealTimeCounter{
	/*
	 * Frequency is ticks per real-time second.
	 */
	private static final int FREQUENCY = 20;
	
	@Override
	public int getFrequency(){
		return FREQUENCY;
	}
	
	@Override
	public long getTime(){
		return API.World.TotalWorldTime();
	}
}
