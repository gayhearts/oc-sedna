package gayhearts.ocsedna.api;

import net.minecraft.world.World ;

public final class WorldAPI {
	private final Boolean initialized;
	
	public WorldAPI(){
		initialized  = true;
	}

	private World world = null;

	public void SetWorld( World world ){
		if( initialized ){
			this.world = world;
		}
	}

	public World GetWorld(){
		if( initialized ){
			return world;
		} else{
			return null;
		}
	}

	private boolean WorldReady(){
		return initialized && (world != null);
	}

	public long TotalWorldTime(){
		if( WorldReady() ){
			return world.getTotalWorldTime();
		} else{
			return 0;
		}
	}

	public long WorldTime(){
		if( WorldReady()  ){
			return world.getWorldTime();
		} else{
			return 0;
		}
	}
}
