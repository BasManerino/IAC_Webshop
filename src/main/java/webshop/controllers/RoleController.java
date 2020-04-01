package webshop.controllers;

import java.net.*;
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
import webshop.services.dataCheckers.RoleDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van RoleController REST controller
//De path moet met /Role beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Role", produces = "application/hal+json")
public class RoleController {

	private final RoleRepository repository;// Voor het communiceren met database
	private final RoleModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final RoleDataChecker dataChecker;// De gegevens van de role te checken

	RoleController(RoleRepository repository, RoleModelAssembler assembler, Converter converter,
			RoleDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle roles van de database als Collection model
	public CollectionModel<EntityModel<Role>> getAllRoles() {
		Stream<Role> stream = converter.toStream(repository.findAll());// Converteren naar stream
		List<EntityModel<Role>> roles = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(roles, linkTo(methodOn(RoleController.class).getAllRoles()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een role opbasis van de megegeven role id
	public EntityModel<Role> getRole(@PathVariable Long id) {
		Role role = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("role", id));

		return assembler.toModel(role);
	}

	@PostMapping // Een role opslaan in de database, hier moet het role als RequestBody gestuurd
	ResponseEntity<?> saveRole(@RequestBody Role newRole) {
		try {
			if (dataChecker.roleChecker(newRole)) {// Check de gegevens van de megegeven role
				Role roleToSave = newRole;
				roleToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande role van dezelfde id te voorkomen

				Role savedRole = repository.save(roleToSave);// sla het role op in de database

				// Maak een entitymodel van het opgeslaagde role met zelflink
				EntityModel<Role> entityModel = new EntityModel<>(savedRole,
						linkTo(methodOn(RoleController.class).getRole(savedRole.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest()
					.body("Unable to create role: " + newRole.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een role wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateRole(@RequestBody Role newRole, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe role gemaakt te worden als die role niet
			// bestaat
			Role roleTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("role", id));
			if (dataChecker.roleChecker(newRole)) { // Check role gegevens
				Role roleToUpdate = newRole;
				roleToUpdate.setId(id);// zet de megegeven id
				Role updatedRole = repository.save(roleToUpdate);// Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde role met zelflink
				EntityModel<Role> entityModel = new EntityModel<>(updatedRole,
						linkTo(methodOn(RoleController.class).getRole(updatedRole.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Role with id " + id + " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een role verwijderen op basis van zijn id die meegestuurd als PathVriable is
	ResponseEntity<?> deleteRole(@PathVariable Long id) {
		try {
			// Check of dit role als bestaat
			Role role = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("role", id));
			repository.deleteById(id); // De role verwijderen

			// return Ok als het role verwijdered is
			return ResponseEntity.ok("Role with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Role with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}
}
