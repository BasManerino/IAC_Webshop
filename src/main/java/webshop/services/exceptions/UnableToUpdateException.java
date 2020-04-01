package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToUpdateException extends RuntimeException {

	// Een exception voor als het update proces niet gelukt is
	public UnableToUpdateException(String className, Long id) {
		super("Could not update " + className + " with id number " + id);
	}
}
