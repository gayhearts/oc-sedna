package gayhearts.ocsedna.api;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

public final class LoggerAPI{
	public LoggerAPI( Logger logger ){
		log4j_logger = logger;
		initialized  = true;
	}

	private final Boolean initialized;
	private final Logger  log4j_logger;

	
	/*public synchronized boolean SetLogger( Logger logger ){
		if( ! initialized ){
			if( logger != null ){
				initialized = true;
				log4j_logger      = logger;
			} else{
				return false;
			}
		} else{
			return false;
		}

		return false;
	}*/

	public void Info( String message ){
		if( CanInfo() ){
			log4j_logger.info( message );
		}
	}

	public void InfoPrintf( String format, Object... params ){
		if( CanInfo() ){
			log4j_logger.printf( Level.INFO, format, params );
		}
	}

	private boolean CanInfo(){
		return initialized && log4j_logger.isInfoEnabled();
	}

	public Logger GetLogger(){
		if( initialized ){
			return log4j_logger;
		} else{
			return null;
		}
	}
}
