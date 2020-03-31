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
import model.Order;
import webshop.controllers.AccountController;
import webshop.controllers.AddressController;
import webshop.controllers.CartController;
import webshop.controllers.OrderController;
import webshop.controllers.RoleController;

@Component
public class AccountModelAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {

	@Override
	public EntityModel<Account> toModel(Account account) {
		List<Order> orders = account.giveOrders();
		List<Link> linksList = new ArrayList<Link>();
		for (Order order : orders) {
			Link link = linkTo(methodOn(OrderController.class).getOrder(order.getId())).withRel("orders");
			linksList.add(link);
		}
		
		return new EntityModel<>(account, linkTo(methodOn(AccountController.class).getAccount(account.getId())).withSelfRel(),
				linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("accounts"),
				linkTo(methodOn(AddressController.class).getAddress(account.giveAddress().getId())).withRel("address"),
				linkTo(methodOn(RoleController.class).getRole(account.giveRole().getId())).withRel("role"),
				linkTo(methodOn(CartController.class).getCart(account.giveCart().getId())).withRel("cart")).add(linksList);
	}
	
}
