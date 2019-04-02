package framework.easytcc.exception;

/**
 * @author Fangfang.Xu
 *
 */
public class NeverBeRecoveryException extends Exception{

	private static final long serialVersionUID = 1L;

	public NeverBeRecoveryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NeverBeRecoveryException(String message) {
		super(message);
	}

	public NeverBeRecoveryException(Throwable cause) {
		super(cause);
	}
}
