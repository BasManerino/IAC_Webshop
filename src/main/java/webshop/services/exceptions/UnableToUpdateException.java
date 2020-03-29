package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToUpdateException extends RuntimeException {

	public UnableToUpdateException(String className, Long id) {
		super("Could not update " + className + " with id number " + id);
	}
}
