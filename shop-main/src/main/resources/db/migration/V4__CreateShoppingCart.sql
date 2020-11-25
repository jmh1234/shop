CREATE TABLE SHOPPING_CART
(
    ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID    BIGINT,
    GOODS_ID   BIGINT,
    NUMBER     INT,
    STATUS     VARCHAR(16),
    CREATED_AT TIMESTAMP DEFAULT NOW(),
    UPDATED_AT TIMESTAMP DEFAULT NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 1, 100, 'ok');
INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 4, 200, 'ok');
INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 5, 300, 'ok');