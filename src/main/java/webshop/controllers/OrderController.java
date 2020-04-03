package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Order;
import repositories.OrderRepository;
import webshop.tools.assemblers.OrderModelAssembler;
import webshop.tools.converters.Converter;
import webshop.tools.datacheckers.OrderDataChecker;
import webshop.tools.exceptions.*;

@RestController // Het maken van OrderController REST controller
//De path moet met /Order beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Order", produces = "application/hal+json")
public class OrderController {

	private final OrderRepository repository;// Voor het communiceren met database
	private final OrderModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final OrderDataChecker dataChecker;// De gegevens van het order te checken

	OrderController(OrderRepository repository, OrderModelAssembler assembler, Converter converter,
			OrderDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle orders van de database als Collection model
	public CollectionModel<EntityModel<Order>> getAllOrders() {
		Stream<Order> stream = converter.toStream(repository.findAll());// Converteren naar stream
		List<EntityModel<Order>> orders = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(orders, linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een order opbasis van de megegeven order id
	public EntityModel<Order> getOrder(@PathVariable Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("order", id));

		return assembler.toModel(order);
	}

	@PostMapping // Een order opslaan in de database, hier moet het order als RequestBody
					// gestuurd
	ResponseEntity<?> saveOrder(@RequestBody Order newOrder) {
		try {
			if (dataChecker.orderChecker(newOrder)) {// Check de gegevens van de megegeven order
				Order orderToSave = newOrder;
				orderToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande order van dezelfde id te voorkomen

				Order savedOrder = repository.save(orderToSave);// sla het order op in de database

				// Maak een entitymodel van het opgeslaagde order met zelflink
				EntityModel<Order> entityModel = new EntityModel<>(savedOrder,
						linkTo(methodOn(OrderController.class).getOrder(savedOrder.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest()
					.body("Unable to create order: " + newOrder.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een order wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateOrder(@RequestBody Order newOrder, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe role gemaakt te worden als die role niet
			// bestaat
			Order orderTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("order", id));
			if (dataChecker.orderChecker(newOrder)) {// Check order gegevens
				Order orderToUpdate = newOrder;
				orderToUpdate.setId(id);// zet de megegeven id
				Order updatedOrder = repository.save(orderToUpdate);// Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde role met zelflink
				EntityModel<Order> entityModel = new EntityModel<>(updatedOrder,
						linkTo(methodOn(OrderController.class).getOrder(updatedOrder.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Order with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een order verwijderen op basis van zijn id die meegestuurd als PathVriable is
	ResponseEntity<?> deleteOrder(@PathVariable Long id) {
		try {
			// Check of dit order als bestaat
			Order order = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("order", id));
			repository.deleteById(id);// Het order verwijderen

			// return Ok als het order verwijdered is
			return ResponseEntity.ok("Order with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Order with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}

}
