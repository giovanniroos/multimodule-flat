package za.dsc.grp.lib.common.jee.util.ejb;

public class RepositoryLookupException extends Exception
{

	private static final long serialVersionUID = 1L;

	public RepositoryLookupException() {

		super();
	}

	public RepositoryLookupException(String message, Throwable cause) {

		super(message, cause);
	}

	public RepositoryLookupException(String message) {

		super(message);
	}

	public RepositoryLookupException(Throwable cause) {

		super(cause);
	}

	/**
	 * resolves the root cause of this exception
	 * 
	 * @return
	 */
	public Throwable getRootCause() {

		Throwable cause = this;
		do {
			cause = cause.getCause();
		} while (cause.getCause() != null);
		return cause;
	}

}
