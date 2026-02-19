package gayhearts.ocsedna.drivers.minecraft;

import gayhearts.ocsedna.api.API;
import li.cil.sedna.api.device.rtc.RealTimeCounter;

public final class WorldTimeRTC implements RealTimeCounter{
	// Lowest common multiple: 5
	// E.G. 5 ticks per 18 minecraft seconds.
	// Frequency is tick-to-seconds times LCM.
	private static final int FREQUENCY       = 18;
	private static final int TIME_MULTIPLIER =  5;
	
	@Override
	public int getFrequency(){
		return FREQUENCY;
	}
	
	@Override
	public long getTime(){
		return API.World.WorldTime() * TIME_MULTIPLIER;
	}
}
