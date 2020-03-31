package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Account;
import model.Cart;
import repositories.AccountRepository;
import repositories.CartRepository;
import webshop.services.assemblers.AccountModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToDeleteException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Account", produces = "application/hal+json")
public class AccountController {

	private final AccountRepository repository;
	private final AccountModelAssembler assembler;
	private final Converter converter;
	private final CartRepository cartRepository;

	AccountController(AccountRepository repository, AccountModelAssembler assembler, Converter converter, CartRepository cartRepository) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.cartRepository = cartRepository;
	}

	@GetMapping
	public CollectionModel<EntityModel<Account>> getAllAccounts() {
		Stream<Account> stream = converter.toStream(repository.findAll());
		List<EntityModel<Account>> accounts = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(accounts,
				linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Account> getAccount(@PathVariable Long id) {
		Account account = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("account", id));

		return assembler.toModel(account);
	}

	@PostMapping
	ResponseEntity<?> saveAccount(@RequestBody Account newAccount) {
		try {
			Cart savedCart = cartRepository.save(new Cart());
			Account accountToSave = newAccount;
			accountToSave.setId(null);
			accountToSave.setCart(savedCart);
			Account savedAccount = repository.save(accountToSave);

			EntityModel<Account> entityModel = new EntityModel<>(savedAccount,
					linkTo(methodOn(AccountController.class).getAccount(savedAccount.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create account: " + newAccount.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateAccount(@RequestBody Account newAccount, @PathVariable long id) {

		try {
			// This made to prevent make a new account if there's no account with such id
			Account accountTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("account", id));

			Account accountToUpdate = newAccount;
			accountToUpdate.setId(id);
			Account updatedAccount = repository.save(accountToUpdate);

			EntityModel<Account> entityModel = new EntityModel<>(updatedAccount,
					linkTo(methodOn(AccountController.class).getAccount(updatedAccount.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Account with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteAccount(@PathVariable Long id) {
		try {
			Account account = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("account", id));
			repository.deleteById(id);
			cartRepository.delete(account.giveCart());

			return ResponseEntity.ok("Account with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Account with id " + id + " is not found or can't be deleted");
		}
	}
}
