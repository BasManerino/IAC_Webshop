package webshop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
    //================================================================================
    // Tests voor AccountController
    //================================================================================

	//Een test om alle accounts te GETten. (200 OK)
	@Test
	public void getAllAccount() throws Exception {
		this.mockMvc.perform(get("/Account")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand account te GETten. (200 OK)
	@Test
	public void getAccount() throws Exception {
		this.mockMvc.perform(get("/Account/1")).andDo(print()).andExpect(status().isOk());
		this.mockMvc.perform(get("/Account/" + 10)).andDo(print()).andExpect(status().isNotFound());
	}

	//Een test om een niet-bestaand account te GETten. (404 Not Found)
	@Test
	public void getAccountNotFound() throws Exception {
		this.mockMvc.perform(get("/Account/" + 10)).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een nieuw account te maken. (201 Created)
	@Test
	public void saveAccount() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
		//Dit is een test JSON body voor een account.
		String account = "{\r\n" + 
				"    \"created_on\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"name\": \"Chris\",\r\n" + 
				"    \"phone\": \"+31685236914\",\r\n" + 
				"    \"email\": \"email@gmail.com\",\r\n" + 
				"    \"address\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    },\r\n" + 
				"    \"role\":{\r\n" + 
				"    	\"id\": 2\r\n" + 
				"    }\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Account/").contentType(MediaType.APPLICATION_JSON).content(account)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand account te wijzigen. (201 Created)
	@Test
	public void updateAccount() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
		//Dit is een test JSON body voor een account.
		String account = "{\r\n" + 
				"    \"created_on\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"name\": \"Chris\",\r\n" + 
				"    \"phone\": \"+31685236914\",\r\n" + 
				"    \"email\": \"email@gmail.com\",\r\n" + 
				"    \"address\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    },\r\n" + 
				"    \"role\":{\r\n" + 
				"    	\"id\": 2\r\n" + 
				"    }\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Account/1").contentType(MediaType.APPLICATION_JSON).content(account)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand account te wijzigen. (400 Bad Request)
	@Test
	public void updateAccountBadRequest() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();
		//Dit is een test JSON body voor een account.
		String account = "{\r\n" + 
				"    \"created_on\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"name\": \"Chris\",\r\n" + 
				"    \"phone\": \"+31685236914\",\r\n" + 
				"    \"email\": \"email@gmail.com\",\r\n" + 
				"    \"address\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    },\r\n" + 
				"    \"role\":{\r\n" + 
				"    	\"id\": 2\r\n" + 
				"    }\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Account/10").contentType(MediaType.APPLICATION_JSON).content(account)).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeld account te DELETEn. (200 OK)
	@Test
	public void deleteAccount() throws Exception {
		this.mockMvc.perform(delete("/Account/6")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld account te DELETEn. (400 Bad Request)
	@Test
	public void deleteAccountBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Account/10")).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor AddressController
    //================================================================================

	//Een test om alle adressen te GETten. (200 OK)
	@Test
	public void getAllAddresses() throws Exception {
		this.mockMvc.perform(get("/Address")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand adres te GETten. (200 OK)
	@Test
	public void getAddress() throws Exception {
		this.mockMvc.perform(get("/Address/4")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand adres te GETten. (404 Not Found)
	@Test
	public void getAddressNotFound() throws Exception {
		this.mockMvc.perform(get("/Address/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een nieuw adres te maken. (201 Created)
	@Test
	public void saveAddress() throws Exception {
		//Dit is een test JSON body voor een adres.
		String adres = "{\r\n" + 
				"    \"street\": \"street\",\r\n" + 
				"    \"house_number\": \"1\",\r\n" + 
				"    \"city\": \"city\",\r\n" + 
				"    \"state\": \"state\",\r\n" +
				"    \"postal_code\": \"3152NJ\",\r\n" +
				"    \"country\": \"country\"\r\n" +
				"}";
		
		this.mockMvc.perform(post("/Address/").contentType(MediaType.APPLICATION_JSON).content(adres)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand adres te wijzigen. (201 Created)
	@Test
	public void updateAddress() throws Exception {
		//Dit is een test JSON body voor een adres.
		String adres = "{\r\n" + 
				"    \"street\": \"street\",\r\n" + 
				"    \"house_number\": \"1\",\r\n" + 
				"    \"city\": \"city\",\r\n" + 
				"    \"state\": \"state\",\r\n" +
				"    \"postal_code\": \"3152NJ\",\r\n" +
				"    \"country\": \"country\"\r\n" +
				"}";
		
		this.mockMvc.perform(put("/Address/1").contentType(MediaType.APPLICATION_JSON).content(adres)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand adres te wijzigen. (400 Bad Request)
	@Test
	public void updateAddressBadRequest() throws Exception {
		//Dit is een test JSON body voor een adres.
		String adres = "{\r\n" + 
				"    \"street\": \"street\",\r\n" + 
				"    \"house_number\": \"1\",\r\n" + 
				"    \"city\": \"city\",\r\n" + 
				"    \"state\": \"state\",\r\n" +
				"    \"postal_code\": \"3152NJ\",\r\n" +
				"    \"country\": \"country\"\r\n" +
				"}";
		
		this.mockMvc.perform(put("/Address/10").contentType(MediaType.APPLICATION_JSON).content(adres)).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor CartController
    //================================================================================

	//Een test om alle winkelwagens te GETten. (200 OK)
	@Test
	public void getAllCarts() throws Exception {
		this.mockMvc.perform(get("/Cart")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand winkelwagen te GETten. (200 OK)
	@Test
	public void getCart() throws Exception {
		this.mockMvc.perform(get("/Cart/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand winkelwagen te GETten. (404 Not Found)
	@Test
	public void getCartNotFound() throws Exception {
		this.mockMvc.perform(get("/Cart/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een bestaand en niet-gekoppeled winkelwagen te DELETEn. (200 OK)
	@Test
	public void addProductFromCart() throws Exception {
		this.mockMvc.perform(put("/Cart/1/Product/1")).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand of product toe te voegen aan een (niet-bestaande) winkelwagen. (400 Bad Request)
	@Test
	public void addProductFromCartBadRequest() throws Exception {
		this.mockMvc.perform(put("/Cart/10/Product/10")).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeled winkelwagen te DELETEn. (201 Created)
	@Test
	public void deleteProductFromCart() throws Exception {
		this.mockMvc.perform(delete("/Cart/1/Product/1")).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand of product toe te voegen aan een (niet-bestaande) winkelwagen. (400 Bad Request)
	@Test
	public void deleteProductFromCartBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Cart/10/Product/10")).andDo(print()).andExpect(status().isBadRequest());
	}
	
	
    //================================================================================
    // Tests voor CategoryController
    //================================================================================

	//Een test om alle categories te GETten. (200 OK)
	@Test
	public void getAllCategories() throws Exception {
		this.mockMvc.perform(get("/Category")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand categorie te GETten. (200 OK)
	@Test
	public void getCategory() throws Exception {
		this.mockMvc.perform(get("/Category/3")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand categorie te GETten. (404 Not Found)
	@Test
	public void getCategoryNotFound() throws Exception {
		this.mockMvc.perform(get("/Category/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een new categorie te maken. (201 Created)
	@Test
	public void saveCategory() throws Exception {
		//Dit is een test JSON body voor een account.
		String categorie = "{\r\n" + 
				"    \"name\": \"test\",\r\n" + 
				"    \"description\": \"test\",\r\n" + 
				"    \"imageId\": \"No Image\"\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Category/").contentType(MediaType.APPLICATION_JSON).content(categorie)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand categorie te wijzigen. (201 Created)
	@Test
	public void updateCategory() throws Exception {
		//Dit is een test JSON body voor een account.
		String categorie = "{\r\n" + 
				"    \"name\": \"test\",\r\n" + 
				"    \"description\": \"test\",\r\n" + 
				"    \"imageId\": \"No Image\"\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Category/3").contentType(MediaType.APPLICATION_JSON).content(categorie)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand categorie te wijzigen. (400 Bad Request)
	@Test
	public void updateCategoryBadRequest() throws Exception {
		//Dit is een test JSON body voor een account.
		String categorie = "{\r\n" + 
				"    \"name\": \"test\",\r\n" + 
				"    \"description\": \"test\",\r\n" + 
				"    \"imageId\": \"No Image\"\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Category/10").contentType(MediaType.APPLICATION_JSON).content(categorie)).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeled categorie te DELETEn. (200 OK)
	@Test
	public void deleteCategory() throws Exception {
		this.mockMvc.perform(delete("/Category/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld categorie te DELETEn. (400 Bad Request)
	@Test
	public void deleteCategoryBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Category/3")).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor CheckoutController
    //================================================================================

	//Een test om alle checkouts te GETten. (200 OK)
	@Test
	public void getAllCheckouts() throws Exception {
		this.mockMvc.perform(get("/Checkout")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand checkout te GETten. (200 OK)
	@Test
	public void getCheckout() throws Exception {
		this.mockMvc.perform(get("/Checkout/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand checkout te GETten. (404 Not Found)
	@Test
	public void getCheckoutNotFound() throws Exception {
		this.mockMvc.perform(get("/Checkout/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een new categorie te maken. (201 Created)
	@Test
	public void saveCheckout() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();
		//Dit is een test JSON body voor een account.
		String checkout = "{\r\n" + 
				"    \"pay_method\": \"iDeal\",\r\n" + 
				"    \"pay_date\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"order\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    }\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Checkout").contentType(MediaType.APPLICATION_JSON).content(checkout)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een correcte checkout te valideren. (200 OK)
	@Test
	public void validate() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();
		//Dit is een test JSON body voor een account.
		String checkout = "{\r\n" + 
				"    \"pay_method\": \"iDeal\",\r\n" + 
				"    \"pay_date\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"order\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    }\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Checkout/Check").contentType(MediaType.APPLICATION_JSON).content(checkout)).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand en niet-gekoppeled checkout te DELETEn. (200 OK)
	@Test
	public void deleteCheckout() throws Exception {
		this.mockMvc.perform(delete("/Checkout/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld checkout te DELETEn. (400 Bad Request)
	@Test
	public void deleteCheckoutBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Checkout/3")).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor DiscountController
    //================================================================================

	//Een test om alle discounts te GETten. (200 OK)
	@Test
	public void getAllDiscounts() throws Exception {
		this.mockMvc.perform(get("/Discount")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand discount te GETten. (200 OK)
	@Test
	public void getDiscount() throws Exception {
		this.mockMvc.perform(get("/Discount/" + 1)).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand discount te GETten. (404 Not Found)
	@Test
	public void getDiscountNotFound() throws Exception {
		this.mockMvc.perform(get("/Discount/" + 10)).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een bestaand en niet-gekoppeled discount te DELETEn. (200 OK)
	@Test
	public void deleteDiscount() throws Exception {
		this.mockMvc.perform(delete("/Account/" + 3)).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld discount te DELETEn. (400 Bad Request)
	@Test
	public void deleteDiscountBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Account/" + 1)).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor OrderController
    //================================================================================

	//Een test om alle orders te GETten. (200 OK)
	@Test
	public void getAllOrders() throws Exception {
		this.mockMvc.perform(get("/Order")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand order te GETten. (200 OK)
	@Test
	public void getOrder() throws Exception {
		this.mockMvc.perform(get("/Order/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand order te GETten. (404 Not Found)
	@Test
	public void getOrderNotFound() throws Exception {
		this.mockMvc.perform(get("/Order/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een new order te maken. (201 Created)
	@Test
	public void saveOrder_table() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
		//Dit is een test JSON body voor een order.
		String order = "{\r\n" + 
				"    \"date\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"total_price\": 25.99,\r\n" + 
				"    \"account\": {\r\n" + 
				"    	\"id\": 1\r\n" + 
				"    }, \"products\":[\r\n" + 
				"    	{\r\n" + 
				"    		\"id\": 1\r\n" + 
				"    	}\r\n" + 
				"    ]\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Order/").contentType(MediaType.APPLICATION_JSON).content(order)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand order te wijzigen. (201 Created)
	@Test
	public void updateOrder_tableOK() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();
		//Dit is een test JSON body voor een order.
        String order = "{\r\n" + 
        		"    \"date\": \"" + dtf.format(now) + "\",\r\n" + 
        		"    \"total_price\": 25.99,\r\n" + 
        		"    \"account\": {\r\n" + 
        		"    	\"id\": 1\r\n" + 
        		"    },\r\n" + 
        		"    \"products\":[\r\n" + 
        		"    	{\r\n" + 
        		"    		\"id\": 1\r\n" + 
        		"    	}\r\n" + 
        		"    ]\r\n" + 
        		"}";
		
		this.mockMvc.perform(put("/Order/1").contentType(MediaType.APPLICATION_JSON).content(order)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand order te wijzigen. (400 Bad Request)
	@Test
	public void updateOrderBadRequest() throws Exception {
		//De huidige datum.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();
		//Dit is een test JSON body voor een order.
        String order = "{\r\n" + 
				"    \"date\": \"" + dtf.format(now) + "\",\r\n" + 
				"    \"total_price\": 12.3,\r\n" + 
				"    \"account\": {\r\n" + 
				"    	\"id\": 4\r\n" + 
				"    },\r\n" + 
				"    \"products\": [\r\n" + 
				"    	{\r\n" + 
				"	        \"id\": 1\r\n" + 
				"	    },\r\n" + 
				"	    {\r\n" + 
				"	        \"id\": 2\r\n" + 
				"	    }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Order/1").contentType(MediaType.APPLICATION_JSON).content(order)).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeled order te DELETEn. (200 OK)
	@Test
	public void deleteOrder() throws Exception {
		this.mockMvc.perform(delete("/Order/3")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld order te DELETEn. (400 Bad Request)
	@Test
	public void deleteOrderBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Order/10")).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor ProductController
    //================================================================================

	//Een test om alle producten te GETten. (200 OK)
	@Test
	public void getAllProducten() throws Exception {
		this.mockMvc.perform(get("/Product")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand product te GETten. (200 OK)
	@Test
	public void getProduct() throws Exception {
		this.mockMvc.perform(get("/Product/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand product te GETten. (404 Not Found)
	@Test
	public void getProductNotFound() throws Exception {
		this.mockMvc.perform(get("/Product/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een new product te maken. (201 Created)
	@Test
	public void saveProduct() throws Exception {
		//Dit is een test JSON body voor een product.
		String product = "{\r\n" + 
				"    \"name\": \"Shoes1\",\r\n" + 
				"    \"description\": \"Black Shoes1\",\r\n" + 
				"    \"normal_price\": 35.99,\r\n" + 
				"    \"discount_price\": \"Geen\",\r\n" + 
				"    \"available\": true,\r\n" + 
				"    \"imageId\": \"image\",\r\n" + 
				"    \"categories\":[{\r\n" + 
				"    	\"id\": 3\r\n" + 
				"    }]\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Product").contentType(MediaType.APPLICATION_JSON).content(product)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand product te wijzigen. (201 Created)
	@Test
	public void updateProductCreated() throws Exception {
		//Dit is een test JSON body voor een product.
		String product = "{\r\n" + 
				"    \"name\": \"Shoes1update\",\r\n" + 
				"    \"description\": \"Black Shoes1update\",\r\n" + 
				"    \"normal_price\": 5.99,\r\n" + 
				"    \"discount_price\": \"Geen\",\r\n" + 
				"    \"available\": true,\r\n" + 
				"    \"imageId\": \"image\",\r\n" + 
				"    \"categories\":[{\r\n" + 
				"    	\"id\": 3\r\n" + 
				"    }]\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Product/1").contentType(MediaType.APPLICATION_JSON).content(product)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand product te wijzigen. (400 Bad Request)
	@Test
	public void updateProductBadRequest() throws Exception {
		//Dit is een test JSON body voor een product.
		String product = "{\r\n" + 
				"    \"name\": \"Shoes1update\",\r\n" + 
				"    \"description\": \"Black Shoes1update\",\r\n" + 
				"    \"normal_price\": 5.99,\r\n" + 
				"    \"discount_price\": \"Geen\",\r\n" + 
				"    \"available\": true,\r\n" + 
				"    \"imageId\": \"image\",\r\n" + 
				"    \"categories\":[{\r\n" + 
				"    	\"id\": 3\r\n" + 
				"    }]\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Product/10").contentType(MediaType.APPLICATION_JSON).content(product)).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeled product te DELETEn. (200 OK)
	@Test
	public void deleteProduct() throws Exception {
		this.mockMvc.perform(delete("/Product/7")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld product te DELETEn. (400 Bad Request)
	@Test
	public void deleteProductBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Product/7")).andDo(print()).andExpect(status().isBadRequest());
	}
	
    //================================================================================
    // Tests voor RoleController
    //================================================================================

	//Een test om alle roles te GETten. (200 OK)
	@Test
	public void getAllRoles() throws Exception {
		this.mockMvc.perform(get("/Role")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een bestaand role te GETten. (200 OK)
	@Test
	public void getRole() throws Exception {
		this.mockMvc.perform(get("/Role/1")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand role te GETten. (404 Not Found)
	@Test
	public void getRoleNotFound() throws Exception {
		this.mockMvc.perform(get("/Role/10")).andDo(print()).andExpect(status().isNotFound());
	}
	
	//Een test om een new role te maken. (201 Created)
	@Test
	public void saveRole() throws Exception {
		//Dit is een test JSON body voor een role.
		String role = "{\r\n" + 
				"	\"name\": \"ad1\",\r\n" + 
				"    \"description\": \"Admin1\"\r\n" + 
				"}";
		
		this.mockMvc.perform(post("/Role").contentType(MediaType.APPLICATION_JSON).content(role)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een bestaand role te wijzigen. (201 Created)
	@Test
	public void updateRoleCreated() throws Exception {
		//Dit is een test JSON body voor een role.
		String role = "{\r\n" + 
				"	\"name\": \"ad1\",\r\n" + 
				"    \"description\": \"Admin1\"\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Role/1").contentType(MediaType.APPLICATION_JSON).content(role)).andDo(print()).andExpect(status().isCreated());
	}
	
	//Een test om een niet-bestaand role te wijzigen. (400 Bad Request)
	@Test
	public void updateRoleBadRequest() throws Exception {
		//Dit is een test JSON body voor een role.
		String role = "{\r\n" + 
				"	\"name\": \"ad1\",\r\n" + 
				"    \"description\": \"Admin1\"\r\n" + 
				"}";
		
		this.mockMvc.perform(put("/Role/10").contentType(MediaType.APPLICATION_JSON).content(role)).andDo(print()).andExpect(status().isBadRequest());
	}
	
	//Een test om een bestaand en niet-gekoppeled role te DELETEn. (200 OK)
	@Test
	public void deleteRole() throws Exception {
		this.mockMvc.perform(delete("/Role/4")).andDo(print()).andExpect(status().isOk());
	}
	
	//Een test om een niet-bestaand of gekoppeld role te DELETEn. (400 Bad Request)
	@Test
	public void deleteRoleBadRequest() throws Exception {
		this.mockMvc.perform(delete("/Role/10")).andDo(print()).andExpect(status().isBadRequest());
	}
}