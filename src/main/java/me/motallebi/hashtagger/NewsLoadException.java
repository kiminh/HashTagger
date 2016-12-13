/**
 * 
 */
package me.motallebi.hashtagger;

/**
 * @author mrmotallebi
 *
 */
public class NewsLoadException extends Exception {
	
	public NewsLoadException() {
		super();
	}
	
	public NewsLoadException(String message){
		super(message);
	}
	
	public NewsLoadException(Throwable cause){
		super(cause);
	}

	public NewsLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4486860116957982698L;

}
