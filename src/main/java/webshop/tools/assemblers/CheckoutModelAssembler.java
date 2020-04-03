package webshop.tools.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import model.Checkout;
import webshop.controllers.CheckoutController;
import webshop.controllers.OrderController;

@Component
public class CheckoutModelAssembler implements RepresentationModelAssembler<Checkout, EntityModel<Checkout>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Checkout> toModel(Checkout checkout) {

		// Return met links naar checkouts, self checkout en order
		return new EntityModel<>(checkout,
				linkTo(methodOn(CheckoutController.class).getCheckout(checkout.getId())).withSelfRel(),
				linkTo(methodOn(CheckoutController.class).getAllCheckouts()).withRel("checkouts"),
				linkTo(methodOn(OrderController.class).getOrder(checkout.giveOrder().getId())).withRel("order"));
	}
}
