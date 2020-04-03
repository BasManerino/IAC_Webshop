package webshop.tools.datacheckers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import model.Account;
import model.Checkout;
import model.Order;
import model.Product;

@Component // Deze klasse is verantwoordelijk voor checks
public class CheckoutDataChecker {

	private final ProductDataChecker productChecker;
	private final DiscountDataChecker discountChecker;
	private final AccountDataChecker accountDataChecker;
	private final OrderDataChecker orderDataChecker;

	CheckoutDataChecker(ProductDataChecker productChecker, AccountDataChecker accountDataChecker,
			DiscountDataChecker discountChecker, OrderDataChecker orderDataChecker) {
		this.productChecker = productChecker;
		this.discountChecker = discountChecker;
		this.accountDataChecker = accountDataChecker;
		this.orderDataChecker = orderDataChecker;
	}

	// Check alle gegevens van de checkout en relateerde order, products en account
	// Return als string list met de foute gegevens
	public List<String> checkoutChecker(Checkout checkout) {
		List<String> errorsFound = new ArrayList<String>();
		List<String> paymentMethods = new ArrayList<String>(); // Het lijst met alle payment methoden
		paymentMethods.add("Cash");
		paymentMethods.add("Creditcard");
		paymentMethods.add("Visacard");
		paymentMethods.add("iDeal");
		paymentMethods.add("Paypal");
		Long offerCode = checkout.getOffer_code();
		Order orderToCheck = orderDataChecker.findOrder(checkout.giveOrder());
		Account accountToCheck = orderToCheck.giveAccount();
		List<Product> productsToCheck = orderToCheck.giveProducts();
		boolean allProductsAvailable = false;
		if (productsToCheck != null) {
			allProductsAvailable = productChecker.availablityCheckerToBoolean(productsToCheck);
		}

		if (!allProductsAvailable) {
			errorsFound.add("Products");
		}
		if (offerCode != null && productsToCheck != null) {
			if (!discountChecker.checkProductsOfferCode(productsToCheck, offerCode)) {
				errorsFound.add("OfferCode");
			}
		}
		if (checkout.getPay_date() != null) {
			LocalDate today = LocalDate.now();
			LocalDate pDay = LocalDate.ofInstant(checkout.getPay_date().toInstant(), ZoneId.systemDefault()); ;
			if (!pDay.equals(today)) {
				errorsFound.add("PaymentDate");
			}
		}
		if (checkout.getPay_date() == null) {
			errorsFound.add("PaymentDate");
		}
		if (!paymentMethods.contains(checkout.getPay_method())) {
			errorsFound.add("PaymentMethod");
		}
		if (accountToCheck == null || !accountDataChecker.findAccountAndCheck(accountToCheck)) {
			errorsFound.add("Account details");
		}

		return errorsFound;
	}
}
