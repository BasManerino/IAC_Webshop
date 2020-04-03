package webshop.tools.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import model.Account;
import model.Address;
import webshop.controllers.AccountController;
import webshop.controllers.AddressController;

@Component
public class AddressModelAssembler implements RepresentationModelAssembler<Address, EntityModel<Address>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Address> toModel(Address address) {
		List<Account> accounts = address.giveAccounts();// Get alle accounts van het address
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar accounts
		for (Account account : accounts) {// Links aanmaken voor accounts en toevoegen aan de lijst
			Link link = linkTo(methodOn(AccountController.class).getAccount(account.getId())).withRel("accounts");
			linksList.add(link);
		}

		// Return met links naar addresses, self address en accounts
		return new EntityModel<>(address,
				linkTo(methodOn(AddressController.class).getAddress(address.getId())).withSelfRel(),
				linkTo(methodOn(AddressController.class).getAllAddresses()).withRel("addresses")).add(linksList);
	}

}
