package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Order;
import repositories.OrderRepository;
import webshop.services.assemblers.OrderModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Order", produces = "application/hal+json")
public class OrderController {

	private final OrderRepository repository;
	private final OrderModelAssembler assembler;
	private final Converter converter;

	OrderController(OrderRepository repository, OrderModelAssembler assembler, Converter converter) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
	}

	@GetMapping
	public CollectionModel<EntityModel<Order>> getAllOrders() {
		Stream<Order> stream = converter.toStream(repository.findAll());
		List<EntityModel<Order>> orders = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(orders, linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Order> getOrder(@PathVariable Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("order", id));

		return assembler.toModel(order);
	}

	@PostMapping
	ResponseEntity<?> saveOrder(@RequestBody Order newOrder) {
		try {
			Order orderToSave = newOrder;
			orderToSave.setId(null);
			Order savedOrder = repository.save(orderToSave);

			EntityModel<Order> entityModel = new EntityModel<>(savedOrder,
					linkTo(methodOn(OrderController.class).getOrder(savedOrder.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create order: " + newOrder.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateOrder(@RequestBody Order newOrder, @PathVariable Long id) {
		try {
			// This made to prevent make a new order if there's no order with such id
			Order orderTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("order", id));

			Order orderToUpdate = newOrder;
			orderToUpdate.setId(id);
			Order updatedOrder = repository.save(orderToUpdate);

			EntityModel<Order> entityModel = new EntityModel<>(updatedOrder,
					linkTo(methodOn(OrderController.class).getOrder(updatedOrder.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Order with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteOrder(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Order with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Order with id " + id + " is not found or can't be deleted");
		}
	}

}
