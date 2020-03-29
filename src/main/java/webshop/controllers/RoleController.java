package webshop.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Role;
import repositories.RoleRepository;
import webshop.services.assemblers.RoleModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Role", produces = "application/hal+json")
public class RoleController {

	private final RoleRepository repository;
	private final RoleModelAssembler assembler;
	private final Converter converter;

	RoleController(RoleRepository repository, RoleModelAssembler assembler, Converter converter) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
	}

	@GetMapping
	public CollectionModel<EntityModel<Role>> getAllRoles() {
		Stream<Role> stream = converter.toStream(repository.findAll());
		List<EntityModel<Role>> roles = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(roles, linkTo(methodOn(RoleController.class).getAllRoles()).withSelfRel());
	}
	
	@GetMapping("/{id}")
	public EntityModel<Role> getRole(@PathVariable Long id) {
		Role role = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("role", id));

		return assembler.toModel(role);
	}

	@PostMapping
	ResponseEntity<?> saveRole(@RequestBody Role newRole) {
		try {
			Role roleToSave = newRole;
			roleToSave.setId(null);
			Role savedRole = repository.save(roleToSave);

			EntityModel<Role> entityModel = new EntityModel<>(savedRole,
					linkTo(methodOn(RoleController.class).getRole(savedRole.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create role: " + newRole.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateRole(@RequestBody Role newRole, @PathVariable Long id) {
		try {
			//This made to prevent make a new role if there's no role with such id
			Role roleTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("role", id));
			
			Role roleToUpdate = newRole;
			roleToUpdate.setId(id);
			Role updatedRole = repository.save(roleToUpdate);

			EntityModel<Role> entityModel = new EntityModel<>(updatedRole,
					linkTo(methodOn(RoleController.class).getRole(updatedRole.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Role with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteRole(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Role with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Role with id " + id + " is not found or can't be deleted");
		}
	}
}
