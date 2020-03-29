package webshop.services.assemblers;

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

	@Override
	public EntityModel<Address> toModel(Address address) {
		List<Account> accounts = address.giveAccounts();
		List<Link> linksList = new ArrayList<Link>();
		for (Account account : accounts) {
			Link link = linkTo(methodOn(AccountController.class).getAccount(account.getId())).withRel("accounts");
			linksList.add(link);
		}
		
		return new EntityModel<>(address, linkTo(methodOn(AddressController.class).getAddress(address.getId())).withSelfRel(),
				linkTo(methodOn(AddressController.class).getAllAddresses()).withRel("addresses")).add(linksList);
	}
	
}
