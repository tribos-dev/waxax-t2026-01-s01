CREATE TABLE historico_atualizacao_produto (
    id UUID PRIMARY KEY,
    produto_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_historico_atualizacao_produto_produto FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE,
    CONSTRAINT fk_historico_atualizacao_produto_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_historico_atualizacao_produto_produto ON historico_atualizacao_produto(produto_id);
CREATE INDEX idx_historico_atualizacao_produto_usuario ON historico_atualizacao_produto(usuario_id);
CREATE INDEX idx_historico_atualizacao_produto_data ON historico_atualizacao_produto(data_hora);
