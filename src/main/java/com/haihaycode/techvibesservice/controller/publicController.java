package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.service.CategoryService;
import com.haihaycode.techvibesservice.service.ProductService;
import com.haihaycode.techvibesservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/api/public")
public class publicController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/account/image/{filename}")
    public ResponseEntity<byte[]> getImageAccount(@PathVariable String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(userService.getImage(filename), headers, HttpStatus.OK);
    }
    @GetMapping("/product/image/{filename}")
    public ResponseEntity<byte[]> getImageProduct(@PathVariable String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(productService.getImage(filename), headers, HttpStatus.OK);
    }
    @GetMapping("/category/image/{filename}")
    public ResponseEntity<byte[]> getImageCategory(@PathVariable String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(categoryService.getImage(filename), headers, HttpStatus.OK);
    }

    @GetMapping("/database")
    public String getDatabase(@RequestParam String key) {
        if (key.equalsIgnoreCase("haihaycode")) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>SQL Inserts</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            margin: 20px;\n" +
                    "        }\n" +
                    "        pre {\n" +
                    "            background-color: #f4f4f4;\n" +
                    "            border: 1px solid #ddd;\n" +
                    "            padding: 10px;\n" +
                    "            overflow: auto;\n" +
                    "        }\n" +
                    "        .sql-block {\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        h3 {\n" +
                    "            background-color: #007bff;\n" +
                    "            color: white;\n" +
                    "            padding: 10px;\n" +
                    "            margin-top: 0;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h3>Insert Roles</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO role_entity (name)\n" +
                    "VALUES ('ROLE_ADMIN');\n" +
                    "INSERT INTO role_entity (name)\n" +
                    "VALUES ('ROLE_USER');\n" +
                    "INSERT INTO role_entity (name)\n" +
                    "VALUES ('ROLE_OTHER');\n" +
                    "INSERT INTO role_entity (name)\n" +
                    "VALUES ('ROLE_STAFF');</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Categories</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO category_entity (name, description, image)\n" +
                    "VALUES ('Electronics', 'Devices and gadgets', 'electronics.jpg'),\n" +
                    "       ('Books', 'Wide range of books', 'books.jpg'),\n" +
                    "       ('Clothing', 'Fashion and apparel', 'clothing.jpg');</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Users</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO user_entity (email, password, address, phone, full_name, photo, available, create_date, update_date)\n" +
                    "VALUES ('john.doe@example.com', '$2a$12$Lpn4.8Fahl2PoNG39Sl7DOkeI1raWfr3LS7hlMI/3K.BWxwPHNb4m', '123 Elm Street',\n" +
                    "        1234567890, 'John Doe', 'john_doe.jpg', true, NOW(), NOW()),\n" +
                    "       ('jane.smith@example.com', '$2a$12$Lpn4.8Fahl2PoNG39Sl7DOkeI1raWfr3LS7hlMI/3K.BWxwPHNb4m', '456 Oak Avenue',\n" +
                    "        2345678901, 'Jane Smith', 'jane_smith.jpg', true, NOW(), NOW());</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Products</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO product_entity (name, image, price, description, description_sort, create_date, update_date, available,\n" +
                    "                            category_id)\n" +
                    "VALUES ('Smartphone', 'smartphone.jpg', 69900, 'Latest model with high-end features', 'Smartphone with great features',\n" +
                    "        NOW(), NOW(), true, 1),\n" +
                    "       ('Novel Book', 'novel_book.jpg', 1999, 'A gripping novel', 'Novel that keeps you hooked', NOW(), NOW(), true, 2),\n" +
                    "       ('T-shirt', 'tshirt.jpg', 2999, 'Cotton T-shirt', 'Comfortable and stylish T-shirt', NOW(), NOW(), true, 3);</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Order Statuses</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO order_status_entity (status)\n" +
                    "VALUES ('Đang xử lý'),\n" +
                    "       ('Đang giao hàng'),\n" +
                    "       ('Đã giao hàng'),\n" +
                    "       ('Đã hủy');</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Orders</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO order_entity (address, full_name, phone, total_price, create_date, update_date, user_id, order_code,\n" +
                    "                          order_status_id)\n" +
                    "VALUES ('123 Elm Street', 'John Doe', 1234567890, 71999, NOW(), NOW(), 1, 'ORD123456', 1),\n" +
                    "       ('456 Oak Avenue', 'Jane Smith', 2999, 2999, NOW(), NOW(), 2, 'ORD654321', 2);</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Order Details</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO order_detail_entity (price, quantity, product_id, order_id)\n" +
                    "VALUES (69900, 1, 1, 1),\n" +
                    "       (1999, 1, 2, 2);</code>\n" +
                    "</pre>\n" +
                    "<h3>Insert Favorites</h3>\n" +
                    "<pre class=\"sql-block\">\n" +
                    "<code>INSERT INTO favorites (user_id, product_id, create_date)\n" +
                    "VALUES (1, 1, NOW()),\n" +
                    "       (2, 2, NOW());</code>\n" +
                    "</pre>\n" +
                    "</body>\n" +
                    "</html>\n";
        }
        return "Invalid key";
    }

    @GetMapping("/roles")
    public String getRoles(@RequestParam String key) {
        if (key.equalsIgnoreCase("haihaycode")) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Quyền Hạn Người Dùng</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            margin: 20px;\n" +
                    "        }\n" +
                    "        h2, h3 {\n" +
                    "            color: #007bff;\n" +
                    "        }\n" +
                    "        ul {\n" +
                    "            list-style-type: none;\n" +
                    "            padding-left: 0;\n" +
                    "        }\n" +
                    "        li {\n" +
                    "            background-color: #f4f4f4;\n" +
                    "            border: 1px solid #ddd;\n" +
                    "            margin-bottom: 10px;\n" +
                    "            padding: 10px;\n" +
                    "        }\n" +
                    "        li h3 {\n" +
                    "            margin-top: 0;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h2>Quyền Hạn Người Dùng</h2>\n" +
                    "<ul>\n" +
                    "    <li>\n" +
                    "        <h3>ROLE_ADMIN</h3>\n" +
                    "        <p> Quản Lý Sản Phẩm: Admin có thể thêm, sửa đổi hoặc xóa sản phẩm trong danh mục.</br>\n" +
                    "   Quản Lý Danh Mục: Admin có thể tạo, sửa đổi và xóa các danh mục sản phẩm.</br>\n" +
                    "   Quản Lý Đơn Hàng: Admin có thể xem tất cả các đơn hàng, thay đổi trạng thái đơn hàng và xử lý các đơn hàng.</br>\n" +
                    "   Quản Lý Người Dùng: Admin có thể xem và quản lý thông tin người dùng, bao gồm việc cấp quyền cho các người dùng khác \n" +
                    "   và xóa tài khoản nếu cần.</br>\n" +
                    "   Quản Lý Quyền: Admin có thể cấp quyền cho các vai trò khác như nhân viên và khách hàng.</br>\n" +
                    "   Xem Báo Cáo: Admin có thể xem báo cáo về doanh thu, sản phẩm bán chạy, và các số liệu liên quan khác.</br></p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <h3>ROLE_USER</h3>\n" +
                    "        <p>Xem Sản Phẩm: Khách hàng có thể duyệt và xem thông tin sản phẩm, bao gồm hình ảnh, mô tả, giá cả và các thông tin\n" +
                    "   khác.</br> \n" +
                    "   Thêm Sản Phẩm vào Giỏ Hàng: Khách hàng có thể thêm sản phẩm vào giỏ hàng và quản lý nội dung giỏ hàng của mình.</br>\n" +
                    "   Đặt Hàng: Khách hàng có thể tạo đơn hàng và theo dõi trạng thái đơn hàng của mình.</br>\n" +
                    "   Quản Lý Tài Khoản: Khách hàng có thể xem và cập nhật thông tin cá nhân, bao gồm địa chỉ và thông tin liên lạc.</br>\n" +
                    "   Danh Sách Yêu Thích: Khách hàng có thể thêm sản phẩm vào danh sách yêu thích và xem danh sách yêu thích của mình.</br></p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <h3>ROLE_OTHER</h3>\n" +
                    "        <p>Quyền hạn khác, có thể được định nghĩa theo yêu cầu cụ thể của hệ thống.</p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <h3>ROLE_STAFF</h3>\n" +
                    "        <p>Nhân viên có thể thêm, sửa đổi hoặc xóa sản phẩm trong danh mục, nhưng có thể không có quyền xóa sản\n" +
                    "                     phẩm hoặc sửa đổi thông tin quan trọng mà admin có thể làm.</br>\n" +
                    "                     Quản Lý Đơn Hàng: Nhân viên có thể xem và xử lý các đơn hàng, bao gồm cập nhật trạng thái đơn hàng và chuẩn bị đơn hàng\n" +
                    "                     để vận chuyển.</br>\n" +
                    "                     Xem Báo Cáo Cơ Bản: Nhân viên có thể xem một số báo cáo cơ bản liên quan đến đơn hàng hoặc sản phẩm, nhưng không có\n" +
                    "                     quyền xem báo cáo tài chính hoặc doanh thu chi tiết.</br>\n" +
                    "                     Hỗ Trợ Khách Hàng: Nhân viên có thể trả lời các câu hỏi và hỗ trợ khách hàng qua các kênh liên lạc như email hoặc chat.</br></p>\n" +
                    "    </li>\n" +
                    "</ul>\n" +
                    "</body>\n" +
                    "</html>\n";
        }
        return "Invalid key";
    }

    @GetMapping("/dependencies")
    @PreAuthorize("permitAll()")
    public String getDependencies(@RequestParam String key) {
        if (key.equalsIgnoreCase("haihaycode")) {
            return "<div class=\"markdown prose w-full break-words dark:prose-invert light\"><p>The <code>mvn dependency:list</code> command has provided a comprehensive list of dependencies for your <code>TechVibesService</code> project. Here are some of the key dependencies and their details:</p><h3>Compile-Time Dependencies:</h3><ol><li><p><strong>Spring Boot Starters:</strong></p><ul><li><code>spring-boot-starter-security</code>: 3.3.2</li><li><code>spring-boot-starter</code>: 3.3.2</li><li><code>spring-boot</code>: 3.3.2</li><li><code>spring-boot-autoconfigure</code>: 3.3.2</li><li><code>spring-boot-starter-logging</code>: 3.3.2</li><li><code>spring-boot-starter-web</code>: 3.3.2</li><li><code>spring-boot-starter-json</code>: 3.3.2</li><li><code>spring-boot-starter-tomcat</code>: 3.3.2</li><li><code>spring-boot-starter-validation</code>: 3.3.2</li><li><code>spring-boot-starter-data-jpa</code>: 3.3.2</li><li><code>spring-boot-starter-aop</code>: 3.3.2</li><li><code>spring-boot-starter-jdbc</code>: 3.3.2</li><li><code>spring-boot-starter-mail</code>: 3.3.2</li></ul></li><li><p><strong>Spring Framework:</strong></p><ul><li><code>spring-aop</code>: 6.1.11</li><li><code>spring-beans</code>: 6.1.11</li><li><code>spring-context</code>: 6.1.11</li><li><code>spring-expression</code>: 6.1.11</li><li><code>spring-web</code>: 6.1.11</li><li><code>spring-webmvc</code>: 6.1.11</li><li><code>spring-orm</code>: 6.1.11</li><li><code>spring-tx</code>: 6.1.11</li><li><code>spring-aspects</code>: 6.1.11</li><li><code>spring-context-support</code>: 6.1.11</li><li><code>spring-jdbc</code>: 6.1.11</li><li><code>spring-core</code>: 6.1.11</li><li><code>spring-jcl</code>: 6.1.11</li><li><code>spring-data-jpa</code>: 3.3.2</li><li><code>spring-data-commons</code>: 3.3.2</li><li><code>spring-security-config</code>: 6.3.1</li><li><code>spring-security-web</code>: 6.3.1</li><li><code>spring-security-core</code>: 6.3.1</li><li><code>spring-security-crypto</code>: 6.3.1</li></ul></li><li><p><strong>Other Libraries:</strong></p><ul><li><code>logback-classic</code>: 1.5.6</li><li><code>logback-core</code>: 1.5.6</li><li><code>log4j-to-slf4j</code>: 2.23.1</li><li><code>log4j-api</code>: 2.23.1</li><li><code>jul-to-slf4j</code>: 2.0.13</li><li><code>slf4j-api</code>: 2.0.13</li><li><code>snakeyaml</code>: 2.2</li><li><code>jackson-datatype-jdk8</code>: 2.17.2</li><li><code>jackson-datatype-jsr310</code>: 2.17.2</li><li><code>jackson-module-parameter-names</code>: 2.17.2</li><li><code>jackson-databind</code>: 2.17.2</li><li><code>jackson-annotations</code>: 2.17.2</li><li><code>jackson-core</code>: 2.17.2</li><li><code>java-jwt</code>: 4.4.0</li><li><code>mysql-connector-j</code>: 8.3.0</li><li><code>lombok</code>: 1.18.34</li><li><code>HikariCP</code>: 5.1.0</li><li><code>hibernate-core</code>: 6.5.2.Final</li><li><code>hibernate-validator</code>: 8.0.1.Final</li><li><code>jakarta.annotation-api</code>: 2.1.1</li><li><code>jakarta.xml.bind-api</code>: 4.0.2</li><li><code>jakarta.activation-api</code>: 2.1.3</li><li><code>jakarta.persistence-api</code>: 3.1.0</li><li><code>jakarta.transaction-api</code>: 2.0.1</li><li><code>jakarta.inject-api</code>: 2.0.1</li><li><code>jakarta.validation-api</code>: 3.0.2</li><li><code>jakarta.mail</code>: 2.0.3</li><li><code>istack-commons-runtime</code>: 4.1.2</li><li><code>xalan</code>: 2.7.2</li><li><code>serializer</code>: 2.7.2</li><li><code>batik-script</code>: 1.13</li><li><code>org.apache.batik</code> modules</li><li><code>jandex</code>: 3.1.2</li><li><code>jboss-logging</code>: 3.5.3.Final</li><li><code>classmate</code>: 1.7.0</li><li><code>SparseBitSet</code>: 1.2</li><li><code>poi-ooxml</code>: 5.0.0</li><li><code>poi</code>: 5.0.0</li><li><code>jcl-over-slf4j</code>: 2.0.13</li><li><code>commons-codec</code>: 1.16.1</li><li><code>commons-collections4</code>: 4.4</li><li><code>commons-math3</code>: 3.6.1</li><li><code>commons-compress</code>: 1.20</li><li><code>curvesapi</code>: 1.06</li><li><code>bcpkix-jdk15on</code>: 1.68</li><li><code>bcprov-jdk15on</code>: 1.68</li><li><code>xmlsec</code>: 2.2.1</li><li><code>woodstox-core</code>: 5.2.1</li><li><code>stax2-api</code>: 4.2</li><li><code>xmlbeans</code>: 4.0.0</li></ul></li></ol><h3>Test Dependencies:</h3><ol><li><strong>JUnit and Testing Libraries:</strong><ul><li><code>spring-boot-starter-test</code>: 3.3.2</li><li><code>spring-boot-test</code>: 3.3.2</li><li><code>spring-boot-test-autoconfigure</code>: 3.3.2</li><li><code>spring-test</code>: 6.1.11</li><li><code>spring-security-test</code>: 6.3.1</li><li><code>junit-jupiter</code>: 5.10.3</li><li><code>junit-jupiter-api</code>: 5.10.3</li><li><code>junit-jupiter-params</code>: 5.10.3</li><li><code>junit-jupiter-engine</code>: 5.10.3</li><li><code>junit-platform-commons</code>: 1.10.3</li><li><code>junit-platform-engine</code>: 1.10.3</li><li><code>apiguardian-api</code>: 1.1.2</li><li><code>opentest4j</code>: 1.3.0</li><li><code>assertj-core</code>: 3.25.3</li><li><code>mockito-core</code>: 5.11.0</li><li><code>mockito-junit-jupiter</code>: 5.11.0</li><li><code>json-path</code>: 2.9.0</li><li><code>jsonassert</code>: 1.5.3</li><li><code>android-json</code>: 0.0.20131108.vaadin1</li><li><code>awaitility</code>: 4.2.1</li><li><code>hamcrest</code>: 2.2</li><li><code>byte-buddy</code>: 1.14.18</li><li><code>byte-buddy-agent</code>: 1.14.18</li><li><code>objenesis</code>: 3.3</li><li><code>xmlunit-core</code>: 2.9.1</li></ul></li></ol><h3>Optional Dependencies:</h3><ol><li><strong>Spring Boot Configuration Processor:</strong><ul><li><code>spring-boot-configuration-processor</code>: 3.3.2</li></ul></li></ol><p>This comprehensive dependency list includes a variety of essential libraries for building a Spring Boot application, handling web security, data persistence with JPA, email services, logging, and testing.</p><p>If you need any specific information about these dependencies or how to manage them, feel free to ask!</p></div>";
        }
        return "Invalid key";
    }


}
