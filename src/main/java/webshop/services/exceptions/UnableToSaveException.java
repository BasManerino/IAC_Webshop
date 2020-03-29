package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToSaveException extends RuntimeException {

	public UnableToSaveException(String className, Long id) {
		super("Could not create " + className + " with id number " + id);
	}
}
