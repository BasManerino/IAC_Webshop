package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Category;
import repositories.CategoryRepository;
import webshop.services.assemblers.CategoryModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.CategoryDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van CategoryController REST controller
//De path moet met /Category beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Category", produces = "application/hal+json")
public class CategoryController {

	private final CategoryRepository repository;// Voor het communiceren met database
	private final CategoryModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final CategoryDataChecker dataChecker;// De gegevens van het category te checken

	CategoryController(CategoryRepository repository, CategoryModelAssembler assembler, Converter converter,
			CategoryDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle categories van de database als Collection model
	public CollectionModel<EntityModel<Category>> getAllCategories() {
		Stream<Category> stream = converter.toStream(repository.findAll()); // Converteren naar stream
		List<EntityModel<Category>> categories = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(categories,
				linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een category opbasis van de megegeven category id
	public EntityModel<Category> getCategory(@PathVariable Long id) {
		Category category = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("category", id));

		return assembler.toModel(category);
	}

	@PostMapping // Een category opslaan in de database, hier moet het category als RequestBody
					// gestuurd
	ResponseEntity<?> saveCategory(@RequestBody Category newCategory) {
		try {
			if (dataChecker.categoryChecker(newCategory)) {// Check de gegevens van de megegeven category
				Category categoryToSave = newCategory;
				categoryToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande category van dezelfde id te voorkomen

				Category savedCategory = repository.save(categoryToSave); // sla het category op in de database

				// Maak een entitymodel van het opgeslaagde category met zelflink
				EntityModel<Category> entityModel = new EntityModel<>(savedCategory,
						linkTo(methodOn(CategoryController.class).getCategory(savedCategory.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Unable to create category: " + newCategory.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een category wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateCategory(@RequestBody Category newCategory, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe address gemaakt te worden als die address
			// niet bestaat
			Category categoryTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("category", id));
			if (dataChecker.categoryChecker(newCategory)) { // Check address gegevens
				Category categoryToUpdate = newCategory;
				categoryToUpdate.setId(id); // zet de megegeven id
				Category updatedCategory = repository.save(categoryToUpdate); // Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde category met zelflink
				EntityModel<Category> entityModel = new EntityModel<>(updatedCategory,
						linkTo(methodOn(CategoryController.class).getCategory(updatedCategory.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Category with id " + id + " is not found or can't be updated"
					+ "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een category verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		try {
			// Check of dit category als bestaat
			Category category = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("category", id));
			repository.deleteById(id); // Het category verwijderen

			// return Ok als het category verwijdered is
			return ResponseEntity.ok("Category with id: " + id + " is deleted");
		} catch (RuntimeException e) {// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Category with id " + id + " is not found or can't be deleted" + "\nPlease check the id");
		}
	}
}