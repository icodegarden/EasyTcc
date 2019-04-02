package framework.easytcc.exception;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class TccException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TccException(String message, Throwable cause) {
		super(message, cause);
	}

	public TccException(String message) {
		super(message);
	}

	public TccException(Throwable cause) {
		super(cause);
	}
}
