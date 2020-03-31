package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToDeleteException extends RuntimeException {

	public UnableToDeleteException(String className, Long id) {
		super("Could not delete " + className + " with id number " + id);
	}
}
