package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Category;
import model.Product;
import repositories.CategoryRepository;
import webshop.services.assemblers.CategoryModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Category", produces = "application/hal+json")
public class CategoryController {

	private final CategoryRepository repository;
	private final CategoryModelAssembler assembler;
	private final Converter converter;

	CategoryController(CategoryRepository repository, CategoryModelAssembler assembler, Converter converter) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
	}

	@GetMapping
	public CollectionModel<EntityModel<Category>> getAllCategories() {
		Stream<Category> stream = converter.toStream(repository.findAll());
		List<EntityModel<Category>> categories = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(categories,
				linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Category> getCategory(@PathVariable Long id) {
		Category category = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("category", id));

		return assembler.toModel(category);
	}

	@PostMapping
	ResponseEntity<?> saveCategory(@RequestBody Category newCategory) {

		try {
			Category categoryToSave = newCategory;
			categoryToSave.setId(null);
			Category savedCategory = repository.save(categoryToSave);

			EntityModel<Category> entityModel = new EntityModel<>(savedCategory,
					linkTo(methodOn(CategoryController.class).getCategory(savedCategory.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create category: " + newCategory.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateCategory(@RequestBody Category newCategory, @PathVariable Long id) {
		try {
			// This made to prevent make a new category if there's no category with such id
			Category categoryTest = repository.findById(id)
					.orElseThrow(() -> new UnableToUpdateException("category", id));

			Category categoryToUpdate = newCategory;
			categoryToUpdate.setId(id);
			Category updatedCategory = repository.save(categoryToUpdate);

			EntityModel<Category> entityModel = new EntityModel<>(updatedCategory,
					linkTo(methodOn(CategoryController.class).getCategory(updatedCategory.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Category with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Category with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Category with id " + id + " is not found or can't be deleted");
		}
	}
}