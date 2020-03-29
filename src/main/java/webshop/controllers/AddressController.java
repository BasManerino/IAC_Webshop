package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Address;
import repositories.AddressRepository;
import webshop.services.assemblers.AddressModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Address", produces = "application/hal+json")
public class AddressController {

	private final AddressRepository repository;
	private final AddressModelAssembler assembler;
	private final Converter converter;

	AddressController(AddressRepository repository, AddressModelAssembler assembler, Converter converter) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
	}

	@GetMapping
	public CollectionModel<EntityModel<Address>> getAllAddresses() {
		Stream<Address> stream = converter.toStream(repository.findAll());
		List<EntityModel<Address>> addresses = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(addresses,
				linkTo(methodOn(AddressController.class).getAllAddresses()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Address> getAddress(@PathVariable Long id) {
		Address address = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("address", id));

		return assembler.toModel(address);
	}

	@PostMapping
	ResponseEntity<?> saveAddress(@RequestBody Address newAddress) {
		try {
			Address addressToSave = newAddress;
			addressToSave.setId(null);
			Address savedAddress = repository.save(addressToSave);

			EntityModel<Address> entityModel = new EntityModel<>(savedAddress,
					linkTo(methodOn(AddressController.class).getAddress(savedAddress.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create address: " + newAddress.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateAddress(@RequestBody Address newAddress, @PathVariable Long id) {
		try {
			// This made to prevent make a new address if there's no address with such id
			Address addressTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("address", id));

			Address addressToUpdate = newAddress;
			addressToUpdate.setId(id);
			Address updatedAddress = repository.save(addressToUpdate);

			EntityModel<Address> entityModel = new EntityModel<>(updatedAddress,
					linkTo(methodOn(AddressController.class).getAddress(updatedAddress.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Address with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteAddress(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Address with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Address with id " + id + " is not found or can't be deleted");
		}
	}
}
