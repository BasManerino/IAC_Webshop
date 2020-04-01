package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToSaveException extends RuntimeException {

	// Een exception voor als het save proces niet gelukt is
	public UnableToSaveException(String className, Long id) {
		super("Could not create " + className + " with id number " + id);
	}
}
