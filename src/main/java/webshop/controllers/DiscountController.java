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
import webshop.services.exceptions.*;

@RestController // Het maken van DiscountController REST controller
//De path moet met /Discount beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Discount", produces = "application/hal+json")
public class DiscountController {

	private final DiscountRepository repository;// Voor het communiceren met database
	private final DiscountModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final DiscountDataChecker dataChecker;// De gegevens van de discount te checken

	DiscountController(DiscountRepository repository, DiscountModelAssembler assembler, Converter converter,
			DiscountDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle discounts van de database als Collection model
	public CollectionModel<EntityModel<Discount>> getAllDiscounts() {
		// Alle discount van de database halen
		List<Discount> discountsForAdTextCheck = (List<Discount>) repository.findAll();
		for (int i = 0; i < discountsForAdTextCheck.size(); i++) {// Wijzig de adTexts op basis van de geldigheid van de
																	// discounts
			Discount discount = dataChecker.discountAdTextChecker(discountsForAdTextCheck.get(i));
			discountsForAdTextCheck.set(i, discount);
		}

		Stream<Discount> stream = converter.toStream(discountsForAdTextCheck);// Converteren naar stream
		List<EntityModel<Discount>> discounts = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(discounts,
				linkTo(methodOn(DiscountController.class).getAllDiscounts()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een discount opbasis van de megegeven discount id
	public EntityModel<Discount> getDiscount(@PathVariable Long id) {
		Discount discount = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("discount", id));
		discount = dataChecker.discountAdTextChecker(discount);// pas de adText aad

		return assembler.toModel(discount);
	}

	@PostMapping // Een discount opslaan in de database, hier moet de discount als RequestBody
					// gestuurd
	ResponseEntity<?> saveDiscount(@RequestBody Discount newDiscount) {
		try {
			if (dataChecker.discountChecker(newDiscount)) {// Check de gegevens van de megegeven discount
				Discount discountToSave = newDiscount;
				discountToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande discount van dezelfde id te voorkomen

				Discount savedDiscount = repository.save(discountToSave);// sla het discount op in de database

				// Maak een entitymodel van het opgeslaagde discount met zelflink
				EntityModel<Discount> entityModel = new EntityModel<>(savedDiscount,
						linkTo(methodOn(DiscountController.class).getDiscount(savedDiscount.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Unable to create discount: " + newDiscount.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een discount wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateDiscount(@RequestBody Discount newDiscount, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe discount gemaakt te worden als die discount
			// niet bestaat
			Discount discountTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("discount", id));
			if (dataChecker.discountChecker(newDiscount)) {// Check discount gegevens
				Discount discountToUpdate = newDiscount;
				discountToUpdate.setId(id);// zet de megegeven id
				Discount updatedDiscount = repository.save(discountToUpdate);// Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde discount met zelflink
				EntityModel<Discount> entityModel = new EntityModel<>(updatedDiscount,
						linkTo(methodOn(DiscountController.class).getDiscount(updatedDiscount.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Discount with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een discount verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteDiscount(@PathVariable Long id) {
		try {
			// Check of deze discount als bestaat
			Discount discount = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("discount", id));
			repository.deleteById(id);// De discount verwijderen

			// return Ok als de discount verwijdered is
			return ResponseEntity.ok("Discount with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Discount with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}

}