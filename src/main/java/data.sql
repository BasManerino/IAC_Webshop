insert into role (id, name, description) values (1, 'admin', 'Administrator');
insert into role (id, name, description) values (2, 'guest', 'Guest');
insert into role (id, name, description) values (3, 'guest', 'Guest');

insert into address (id, street, house_number, city, state, postal_code, country) values (1, 'street', 1, 'city', 'state', '3152NJ', 'country');
insert into address (id, street, house_number, city, state, postal_code, country) values (2, 'street2', 2, 'city2', 'state2', '3152NJ', 'country2');
insert into address (id, street, house_number, city, state, postal_code, country) values (3, 'street3', 3, 'city3', 'state3', '3152NJ', 'country3');
insert into address (id, street, house_number, city, state, postal_code, country) values (4, 'street4', 4, 'city4', 'state4', '3152NJ', 'country4');

insert into account (id, created_on, name, phone, email, address_id, role_id) values (1, '2020-03-27', 'Chris', '+31685236914', 'email@gmail.com', 1, 1);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (2, '2020-03-28', 'Ciline', '+31681245876', 'email2@gmail.com', 2, 2);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (3, '2020-03-29', 'Amir', '+3165875566', 'email3@gmail.com', 3, 2);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (4, '2020-03-30', 'Saly', '+3165212876', 'email4@gmail.com', 4, 3);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (5, '2020-04-01', 'Carla', '+3168985876', 'email5@gmail.com', 1, 3);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (6, '2020-04-02', 'Mira', '+3167895876', 'email6@gmail.com', 2, 3);
insert into account (id, created_on, name, phone, email, address_id, role_id) values (7, '2020-04-03', 'Rik', '+3165223876', 'email7@gmail.com', 3, 3);

insert into order_table (id, date, total_price, account_id) values (1, '2020-03-21', 12.3, 1);
insert into order_table (id, date, total_price, account_id) values (2, '2020-03-22', 13.3, 1);
insert into order_table (id, date, total_price, account_id) values (3, '2020-03-23', 14.3, 1);
insert into order_table (id, date, total_price, account_id) values (4, '2020-03-24', 15.3, 2);
insert into order_table (id, date, total_price, account_id) values (5, '2020-03-25', 16.3, 2);

insert into product (id, name, description, price, image_id) values (1, 'Shoes', 'Black Shoes', 25.99, 'image');
insert into product (id, name, description, price, image_id) values (2, 'Shoes', 'Red Shoes', 26.99, 'image');
insert into product (id, name, description, price, image_id) values (3, 'Shirt', 'Blue Shirt', 27.99, 'image');
insert into product (id, name, description, price, image_id) values (4, 'Shirt', 'Purple Shirt', 28.99, 'image');
insert into product (id, name, description, price, image_id) values (5, 'Jacket', 'Gray Jacket', 29.99, 'image');
insert into product (id, name, description, price, image_id) values (6, 'Jacket', 'Brown Jacket', 30.99, 'image');

insert into product_order (product_id, order_id) values (1,2);
insert into product_order (product_id, order_id) values (2,2);
insert into product_order (product_id, order_id) values (3,2);
insert into product_order (product_id, order_id) values (4,2);
insert into product_order (product_id, order_id) values (5,1);
insert into product_order (product_id, order_id) values (6,1);
insert into product_order (product_id, order_id) values (3,1);

insert into category (id, name, description, image_id) values (1, 'New', 'Gategory for the new products', 'No Image');
insert into category (id, name, description, image_id) values (2, 'Clothes', 'Clothes Products', 'Image');
insert into category (id, name, description, image_id) values (3, 'Shoes', 'Shoes Products', 'Image');

insert into product_category (product_id, category_id) values (1,3);
insert into product_category (product_id, category_id) values (2,3);
insert into product_category (product_id, category_id) values (3,2);
insert into product_category (product_id, category_id) values (4,2);
insert into product_category (product_id, category_id) values (5,2);
insert into product_category (product_id, category_id) values (6,2);

insert into discount (id, price, from_date, until_date, ad_text) values (1, 20, '2020-02-03', '2020-3-03', 'discount ad text1');
insert into discount (id, price, from_date, until_date, ad_text) values (2, 15, '2020-01-18', '2020-3-13', 'discount ad text2');
insert into discount (id, price, from_date, until_date, ad_text) values (3, 10, '2020-01-03', '2020-4-23', 'discount ad text3');

insert into product_discount (product_id, discount_id) values (3, 1);
insert into product_discount (product_id, discount_id) values (2, 1);
insert into product_discount (product_id, discount_id) values (5, 2);
insert into product_discount (product_id, discount_id) values (1, 3);