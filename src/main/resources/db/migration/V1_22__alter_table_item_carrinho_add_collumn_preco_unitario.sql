ALTER TABLE item_carrinho
ADD COLUMN preco_unitario DECIMAL(19,2);

UPDATE item_carrinho
SET preco_unitario = 0;

ALTER TABLE item_carrinho
ALTER COLUMN preco_unitario SET NOT NULL;
