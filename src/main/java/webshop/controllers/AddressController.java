package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Address;
import repositories.AddressRepository;
import webshop.services.assemblers.AddressModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.AddressDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van AddressController REST controller
//De path moet met /Address beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Address", produces = "application/hal+json")
public class AddressController {

	private final AddressRepository repository; // Voor het communiceren met database
	private final AddressModelAssembler assembler; // Om links te maken van de related objecten
	private final Converter converter; // Om het behalde info als stream te maken
	private final AddressDataChecker dataChecker; // De gegevens van het address te checken

	AddressController(AddressRepository repository, AddressModelAssembler assembler, Converter converter,
			AddressDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle addresses van de database als Collection model
	public CollectionModel<EntityModel<Address>> getAllAddresses() {
		Stream<Address> stream = converter.toStream(repository.findAll()); // Converteren naar stream
		List<EntityModel<Address>> addresses = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(addresses,
				linkTo(methodOn(AddressController.class).getAllAddresses()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een address opbasis van de megegeven address id
	public EntityModel<Address> getAddress(@PathVariable Long id) {
		Address address = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("address", id));

		return assembler.toModel(address);
	}

	@PostMapping // Een address opslaan in de database, hier moet het address als RequestBody
					// gestuurd
	ResponseEntity<?> saveAddress(@RequestBody Address newAddress) {
		try {
			if (dataChecker.addressChecker(newAddress)) { // Check de gegevens van de megegeven address
				Address addressToSave = newAddress;
				addressToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande address van dezelfde id te voorkomen
				
				Address savedAddress = repository.save(addressToSave); // sla het address op in de database

				// Maak een entitymodel van het opgeslaagde address met zelflink
				EntityModel<Address> entityModel = new EntityModel<>(savedAddress,
						linkTo(methodOn(AddressController.class).getAddress(savedAddress.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Unable to create address: " + newAddress.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een address wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateAddress(@RequestBody Address newAddress, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe address gemaakt te worden als die address
			// niet bestaat
			Address addressTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("address", id));
			if (dataChecker.addressChecker(newAddress)) { // Check address gegevens
				Address addressToUpdate = newAddress;
				addressToUpdate.setId(id); // zet de megegeven id
				Address updatedAddress = repository.save(addressToUpdate); // Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde address met zelflink
				EntityModel<Address> entityModel = new EntityModel<>(updatedAddress,
						linkTo(methodOn(AddressController.class).getAddress(updatedAddress.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Address with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een address verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteAddress(@PathVariable Long id) {
		try {
			// Check of dit address als bestaat
			Address address = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("address", id));
			repository.deleteById(id); // Het address verwijderen

			// return Ok als het address verwijdered is
			return ResponseEntity.ok("Address with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Address with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}
}
