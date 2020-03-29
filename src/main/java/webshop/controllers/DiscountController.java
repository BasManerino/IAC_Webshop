package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Discount;
import repositories.DiscountRepository;
import webshop.services.assemblers.DiscountModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.DiscountDataChecker;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Discount", produces = "application/hal+json")
public class DiscountController {

	private final DiscountRepository repository;
	private final DiscountModelAssembler assembler;
	private final Converter converter;
	private final DiscountDataChecker dataChecker;

	DiscountController(DiscountRepository repository, DiscountModelAssembler assembler, Converter converter,
			DiscountDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping
	public CollectionModel<EntityModel<Discount>> getAllDiscounts() {
		List<Discount> discountsForAdTextCheck = (List<Discount>) repository.findAll();
		for (int i = 0; i < discountsForAdTextCheck.size(); i++) {
			Discount discount = dataChecker.discountAdTextChecker(discountsForAdTextCheck.get(i));
			discountsForAdTextCheck.set(i, discount);
		}

		Stream<Discount> stream = converter.toStream(discountsForAdTextCheck);
		List<EntityModel<Discount>> discounts = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(discounts,
				linkTo(methodOn(DiscountController.class).getAllDiscounts()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Discount> getDiscount(@PathVariable Long id) {
		Discount discount = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("discount", id));
		discount = dataChecker.discountAdTextChecker(discount);

		return assembler.toModel(discount);
	}

	@PostMapping
	ResponseEntity<?> saveCategory(@RequestBody Discount newDiscount) {
		try {
			Discount discountToSave = newDiscount;
			discountToSave.setId(null);
			Discount savedDiscount = repository.save(discountToSave);

			EntityModel<Discount> entityModel = new EntityModel<>(savedDiscount,
					linkTo(methodOn(DiscountController.class).getDiscount(savedDiscount.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create discount: " + newDiscount.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateDiscount(@RequestBody Discount newDiscount, @PathVariable Long id) {
		try {
			// This made to prevent make a new discount if there's no discount with such id
			Discount discountTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("discount", id));

			Discount discountToUpdate = newDiscount;
			discountToUpdate.setId(id);
			Discount updatedDiscount = repository.save(discountToUpdate);

			EntityModel<Discount> entityModel = new EntityModel<>(updatedDiscount,
					linkTo(methodOn(DiscountController.class).getDiscount(updatedDiscount.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Discount with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Discount with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Discount with id " + id + " is not found or can't be deleted");
		}
	}

}