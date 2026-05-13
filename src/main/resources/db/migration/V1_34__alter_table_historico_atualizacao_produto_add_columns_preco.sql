ALTER TABLE historico_atualizacao_produto ADD COLUMN tipo_preco VARCHAR(20);
ALTER TABLE historico_atualizacao_produto ADD COLUMN valor_anterior NUMERIC(15,2);
ALTER TABLE historico_atualizacao_produto ADD COLUMN valor_novo NUMERIC(15,2);
ALTER TABLE historico_atualizacao_produto ADD COLUMN motivo VARCHAR(255);
