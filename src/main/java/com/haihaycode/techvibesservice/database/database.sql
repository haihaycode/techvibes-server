INSERT INTO role_entity (name)
VALUES ('ROLE_ADMIN');
INSERT INTO role_entity (name)
VALUES ('ROLE_USER');
INSERT INTO role_entity (name)
VALUES ('ROLE_OTHER');
INSERT INTO role_entity (name)
VALUES ('ROLE_STAFF');

INSERT INTO category_entity (name, description, image)
VALUES ('Electronics', 'Devices and gadgets', 'electronics.jpg'),
       ('Books', 'Wide range of books', 'books.jpg'),
       ('Clothing', 'Fashion and apparel', 'clothing.jpg');


INSERT INTO user_entity (email, password, address, phone, full_name, photo, available, create_date, update_date)
VALUES ('john.doe@example.com', '$2a$12$Lpn4.8Fahl2PoNG39Sl7DOkeI1raWfr3LS7hlMI/3K.BWxwPHNb4m', '123 Elm Street',
        1234567890, 'John Doe', 'john_doe.jpg', true, NOW(),
        NOW()),
       ('jane.smith@example.com', '$2a$12$Lpn4.8Fahl2PoNG39Sl7DOkeI1raWfr3LS7hlMI/3K.BWxwPHNb4m', '456 Oak Avenue',
        2345678901, 'Jane Smith', 'jane_smith.jpg', true,
        NOW(), NOW());
INSERT INTO product_entity (name, image, price, description, description_sort, create_date, update_date, available,
                            category_id)
VALUES ('Smartphone', 'smartphone.jpg', 69900, 'Latest model with high-end features', 'Smartphone with great features',
        NOW(), NOW(), true, 1),
       ('Novel Book', 'novel_book.jpg', 1999, 'A gripping novel', 'Novel that keeps you hooked', NOW(), NOW(), true, 2),
       ('T-shirt', 'tshirt.jpg', 2999, 'Cotton T-shirt', 'Comfortable and stylish T-shirt', NOW(), NOW(), true, 3);
INSERT INTO order_status_entity (status)
VALUES ('Đang xử lý'),
       ('Đang giao hàng'),
       ('Đã giao hàng'),
       ('Đã hủy');
INSERT INTO order_entity (address, full_name, phone, total_price, create_date, update_date, user_id, order_code,
                          order_status_id)
VALUES ('123 Elm Street', 'John Doe', 1234567890, 71999, NOW(), NOW(), 1, 'ORD123456', 1),
       ('456 Oak Avenue', 'Jane Smith', 2999, 2999, NOW(), NOW(), 2, 'ORD654321', 2);

INSERT INTO order_detail_entity (price, quantity, product_id, order_id)
VALUES (69900, 1, 1, 1),
       (1999, 1, 2, 2);
INSERT INTO favorites (user_id, product_id, create_date)
VALUES (1, 1, NOW()),
       (2, 2, NOW());

