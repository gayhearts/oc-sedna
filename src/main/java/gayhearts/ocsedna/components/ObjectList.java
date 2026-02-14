package gayhearts.ocsedna.components;

import java.security.SecureRandom;
import java.util.HashMap;

public abstract class ObjectList {
	private HashMap<String, Object> items = new HashMap<String, Object>();
	private HashMap<Long, String>   locks = new HashMap<Long, String>();
	
	private Class<?> object_class;
	
	public ObjectList( Class<?> object_class ){
		this.object_class = object_class;
	}
	
	public Long GetLock( String type ){		
		SecureRandom random = new SecureRandom();
		
		if( locks.containsValue(type) == true ){
			// If any Locks are type.
			return null;
		} else {
			Long new_lock = null;
			
			// Try for a lock 10 times.
			for( int I=0; I < 10; I++ ){
				Long tmp_lock = random.nextLong();
				
				if( locks.containsKey(new_lock) == true ){
					continue;
				} else{
					new_lock = tmp_lock;
					break;
				}
			}

			// If no lock created, it's unsafe to lock. Otherwise, store and return it.
			if( new_lock == null ){
				return null;
			} else{
				locks.put( new_lock, type );
				return new_lock;
			}
		}
	}
	
	protected boolean CheckLock( Long lock, String type ){
		if( locks.containsKey(lock) && locks.get(lock) == type ){
			return true;
		} else{
			return false;
		}
	}

	public boolean CanProcess( Long lock, Object device ){
		if( CheckLock(lock, "Process") == true && this.object_class.isInstance(device) ){
			return true;
		} else{
			return false;
		}
	}

	public boolean CloseLock( Long lock ){
		if( locks.containsKey(lock) == true ){
			locks.remove( lock );
			return true;
		} else{
			return false;
		}
	}
	
	public boolean HasItems(){
		return ! items.isEmpty();
	}
}
