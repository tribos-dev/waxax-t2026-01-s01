ALTER TABLE pedido ADD COLUMN data_ultima_atualizacao TIMESTAMP;
UPDATE pedido SET data_ultima_atualizacao = NOW() WHERE data_ultima_atualizacao IS NULL;
ALTER TABLE pedido ALTER COLUMN data_ultima_atualizacao SET NOT NULL;