ALTER TABLE SHOPPING_CART
    ADD COLUMN (SHOP_ID BIGINT);

DELETE
FROM SHOPPING_CART
where USER_ID = 1;

INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 1, 1, 100, 'ok');
INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 4, 2, 200, 'ok');
INSERT INTO SHOPPING_CART(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 5, 2, 300, 'ok');


