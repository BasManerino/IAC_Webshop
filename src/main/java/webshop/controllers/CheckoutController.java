package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.List;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.*;
import repositories.CheckoutRepository;
import webshop.services.assemblers.CheckoutModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.CheckoutDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van CheckoutController REST controller
//De path moet met /Checkout beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Checkout", produces = "application/hal+json")
public class CheckoutController {

	private final CheckoutRepository repository;// Voor het communiceren met database
	private final CheckoutModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final CheckoutDataChecker dataChecker;// De gegevens van het checkout te checken

	CheckoutController(CheckoutRepository repository, CheckoutModelAssembler assembler, Converter converter,
			CheckoutDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle checkouts van de database als Collection model
	public CollectionModel<EntityModel<Checkout>> getAllCheckouts() {
		Stream<Checkout> stream = converter.toStream(repository.findAll());// Converteren naar stream
		List<EntityModel<Checkout>> checkouts = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(checkouts,
				linkTo(methodOn(CheckoutController.class).getAllCheckouts()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een checkout opbasis van de megegeven checkout id
	public EntityModel<Checkout> getCheckout(@PathVariable Long id) {
		Checkout checkout = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("checkout", id));

		return assembler.toModel(checkout);
	}

	@PostMapping // Een checkout opslaan in de database, hier moet de checkout als RequestBody
					// gestuurd
	ResponseEntity<?> saveCheckout(@RequestBody Checkout newCheckout) {
		try {
			List<String> checkErrors = dataChecker.checkoutChecker(newCheckout); // Check de gegevens van de megegeven
																					// checkout
			if (checkErrors.isEmpty()) { // Als de errorslijst leeg is, dan zijn alle gegevens klopt
				Checkout checkoutToSave = newCheckout;
				checkoutToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande checkout van dezelfde id te voorkomen

				Checkout savedCheckout = repository.save(checkoutToSave);// sla de checkout op in de database

				// Maak een entitymodel van het opgeslaagde address met zelflink
				EntityModel<Checkout> entityModel = new EntityModel<>(savedCheckout,
						linkTo(methodOn(CheckoutController.class).getCheckout(savedCheckout.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// Als er gegevens in het lijst is
				String errors = "";
				for (String error : checkErrors) { // Zet alle errors in een string
					errors += error + ", ";
				}
				errors = errors.substring(0, errors.length() - 2); // Verwijder de laatste ,

				// return badrequest met de errors
				return ResponseEntity.badRequest().body("The folowing informations are not correct:\n" + errors);
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Unable to create checkout: " + newCheckout.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // De checkout wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateCheckout(@RequestBody Checkout newCheckout, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe checkout gemaakt te worden als die checkout
			// niet bestaat
			Checkout checkouTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("checkout", id));
			List<String> checkErrors = dataChecker.checkoutChecker(newCheckout); // Check checkout gegevens
			if (checkErrors.isEmpty()) {// Als de errorslijst leeg is, dan zijn alle gegevens klopt
				Checkout checkoutToUpdate = newCheckout;
				checkoutToUpdate.setId(id);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande checkout van dezelfde id te voorkomen

				Checkout updatedCheckout = repository.save(checkoutToUpdate);// sla de checkout op in de database

				// Maak een entitymodel van het opgeslaagde address met zelflink
				EntityModel<Checkout> entityModel = new EntityModel<>(updatedCheckout,
						linkTo(methodOn(CheckoutController.class).getCheckout(updatedCheckout.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// Als er gegevens in het lijst is
				String errors = "";
				for (String error : checkErrors) {// Zet alle errors in een string
					errors += error + ", ";
				}
				errors = errors.substring(0, errors.length() - 2);// Verwijder de laatste ,

				// return badrequest met de errors
				return ResponseEntity.badRequest().body("The folowing informations are not correct:\n" + errors);
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Checkout with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een checkout verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteCheckout(@PathVariable Long id) {
		try {
			// Check of deze checkout als bestaat
			Checkout checkout = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("address", id));

			repository.deleteById(id);// De checkout verwijderen

			// return Ok als de checkout verwijdered is
			return ResponseEntity.ok("Checkout with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Checkout with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}

	@PutMapping("/Check") // De gegevens can de afrekening checken
	ResponseEntity<?> checkoutCheck(@RequestBody Checkout checkout) {
		List<String> checkErrors = dataChecker.checkoutChecker(checkout);

		if (checkErrors.isEmpty()) { // Check the errors list
			return ResponseEntity.ok("All data are valid"); // Return Ok als alle gegevens kloppen
		} else {
			String errors = "";
			for (String error : checkErrors) {
				errors += error + ", ";
			} // Maak een lijst met de errors en return het terug
			errors = errors.substring(0, errors.length() - 2);
			return ResponseEntity.badRequest().body("The folowing informations are not correct:\n" + errors);
		}

	}

}