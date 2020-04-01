package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import model.Checkout;
import repositories.CheckoutRepository;
import webshop.services.assemblers.CheckoutModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.CheckoutDataChecker;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Checkout", produces = "application/hal+json")
public class CheckoutController {

	private final CheckoutRepository repository;
	private final CheckoutModelAssembler assembler;
	private final Converter converter;
	private final CheckoutDataChecker dataChecker;

	CheckoutController(CheckoutRepository repository, CheckoutModelAssembler assembler, Converter converter,
			CheckoutDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping
	public CollectionModel<EntityModel<Checkout>> getAllCheckouts() {
		Stream<Checkout> stream = converter.toStream(repository.findAll());
		List<EntityModel<Checkout>> checkouts = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(checkouts,
				linkTo(methodOn(CheckoutController.class).getAllCheckouts()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Checkout> getCheckout(@PathVariable Long id) {
		Checkout checkout = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("checkout", id));

		return assembler.toModel(checkout);
	}

	@PostMapping
	ResponseEntity<?> saveCheckout(@RequestBody Checkout newCheckout) {
		try {
			Checkout checkoutToSave = newCheckout;
			checkoutToSave.setId(null);
			Checkout savedCheckout = repository.save(checkoutToSave);

			EntityModel<Checkout> entityModel = new EntityModel<>(savedCheckout,
					linkTo(methodOn(CheckoutController.class).getCheckout(savedCheckout.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create checkout: " + newCheckout.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateCheckout(@RequestBody Checkout newCheckout, @PathVariable Long id) {
		try {
			// This made to prevent make a new order if there's no order with such id
			Checkout checkouTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("checkout", id));

			Checkout checkoutToUpdate = newCheckout;
			checkoutToUpdate.setId(id);
			Checkout updatedCheckout = repository.save(checkoutToUpdate);

			EntityModel<Checkout> entityModel = new EntityModel<>(updatedCheckout,
					linkTo(methodOn(CheckoutController.class).getCheckout(updatedCheckout.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Checkout with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteCheckout(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Checkout with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Checkout with id " + id + " is not found or can't be deleted");
		}
	}

	@PutMapping("/Check")
	ResponseEntity<?> checkCheckout(@RequestBody Checkout checkout) {
		List<String> checkErrors = dataChecker.checkoutChecker(checkout);

		if (checkErrors.isEmpty()) {
			return ResponseEntity.ok("All data are valid");
		} else {
			String errors = "";
			for (String error : checkErrors) {
				errors += error + ", ";
			}
			errors = errors.substring(0, errors.length() - 2);
			return ResponseEntity.badRequest().body("The folowing data are not correct:\n" + errors);
		}

	}

}