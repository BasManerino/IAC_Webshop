package webshop.services.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.Role;
import webshop.controllers.AccountController;
import webshop.controllers.RoleController;

import org.springframework.stereotype.Component;

@Component
public class RoleModelAssembler implements RepresentationModelAssembler<Role, EntityModel<Role>> {

	@Override
	public EntityModel<Role> toModel(Role role) {
		List<Account> accounts = role.giveAccounts();
		List<Link> linksList = new ArrayList<Link>();
		for (Account account : accounts) {
			Link link = linkTo(methodOn(AccountController.class).getAccount(account.getId())).withRel("accounts");
			linksList.add(link);
		}

		return new EntityModel<>(role, linkTo(methodOn(RoleController.class).getRole(role.getId())).withSelfRel(),
				linkTo(methodOn(RoleController.class).getAllRoles()).withRel("roles")).add(linksList);
	}

}
