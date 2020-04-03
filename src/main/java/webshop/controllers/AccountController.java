package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.*;
import repositories.AccountRepository;
import repositories.CartRepository;
import webshop.tools.assemblers.AccountModelAssembler;
import webshop.tools.converters.Converter;
import webshop.tools.datacheckers.AccountDataChecker;
import webshop.tools.exceptions.*;

@RestController // Het maken van AccountController REST controller
//De path moet met /Account beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Account", produces = "application/hal+json")
public class AccountController {

	private final AccountRepository repository; // Voor het communiceren met database
	private final AccountModelAssembler assembler; // Om links te maken van de related objecten
	private final Converter converter; // Om het behalde info als stream te maken
	private final CartRepository cartRepository; // Te communiceren met cart db
	private final AccountDataChecker dataChecker; // De gegevens van het account te checken

	AccountController(AccountRepository repository, AccountModelAssembler assembler, Converter converter,
			CartRepository cartRepository, AccountDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.cartRepository = cartRepository;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle accounts van de database als Collection model
	public CollectionModel<EntityModel<Account>> getAllAccounts() {
		Stream<Account> stream = converter.toStream(repository.findAll()); // Converteren naar stream
		List<EntityModel<Account>> accounts = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(accounts,
				linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een account opbasis van de megegeven account id
	public EntityModel<Account> getAccount(@PathVariable Long id) {
		Account account = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("account", id));

		return assembler.toModel(account);
	}

	@PostMapping // Een account opslaan in de database, hier moet het account als RequestBody
					// gestuurd
	ResponseEntity<?> saveAccount(@RequestBody Account newAccount) {
		try {
			if (dataChecker.accountChecker(newAccount)) { // Check de gegevens van de megegeven account
				Cart savedCart = cartRepository.save(new Cart()); // Maak een cart voor dit account
				Account accountToSave = newAccount;
				accountToSave.setId(null); // Maak het id null om in de database autogegenereerde id te krijgen en om de
											// conflicten van een bestaande account van dezelfde id te voorkomen
				accountToSave.setCart(savedCart); // set de gemaakte cart in het account
				Account savedAccount = repository.save(accountToSave); // sla het account op in de database

				// Maak een entitymodel van het opgeslaagde account met zelflink
				EntityModel<Account> entityModel = new EntityModel<>(savedAccount,
						linkTo(methodOn(AccountController.class).getAccount(savedAccount.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Unable to create account: " + newAccount.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een account wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateAccount(@RequestBody Account newAccount, @PathVariable long id) {
		try {
			// Om te voorkomen dat er een nieuwe account gemaakt te worden als die account
			// niet bestaat
			Account accountTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("account", id));
			if (dataChecker.accountChecker(newAccount)) { // Check account gegevens

				Account accountToUpdate = newAccount;
				accountToUpdate.setId(id); // zet de megegeven id
				Account updatedAccount = repository.save(accountToUpdate); // Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde account met zelflink
				EntityModel<Account> entityModel = new EntityModel<>(updatedAccount,
						linkTo(methodOn(AccountController.class).getAccount(updatedAccount.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {
				// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Account with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@DeleteMapping("/{id}") // Een account verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteAccount(@PathVariable Long id) {
		try {
			// Check of dit account als bestaat
			Account account = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("account", id));
			repository.deleteById(id); // Het account verwijderen
			cartRepository.delete(account.giveCart()); // De cart van het account ook verwijderen

			// return Ok als het account verwijdered is
			return ResponseEntity.ok("Account with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Account with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}
}
