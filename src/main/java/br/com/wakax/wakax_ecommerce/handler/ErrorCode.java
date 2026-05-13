package br.com.wakax.wakax_ecommerce.handler;

public enum ErrorCode {
  PEDIDO_NAO_ENCONTRADO("pedido.nao.encontrado"),
  RASTREAMENTO_NAO_ENCONTRADO("rastreamento.nao.encontrado"),
  RASTREAMENTO_JA_EXISTE("rastreamento.ja.existe"),
  ACESSO_NEGADO("acesso.negado"),
  PRODUTO_NAO_ENCONTRADO("produto.nao.encontrado"),
  PRODUTO_DUPLICADO("produto.duplicado"),
  PRODUTO_INDISPONIVEL("produto.indisponivel"),
  PESO_LIQUIDO_MAIOR_QUE_BRUTO("peso.liquido.maior.que.bruto"),
  PRODUTO_INATIVO("produto.inativo"),
  STATUS_PRODUTO_INVALIDO("status.produto.invalido"),
  FORNECEDOR_NAO_ENCONTRADO("fornecedor.nao.encontrado"),
  FORNECEDOR_DUPLICADO("fornecedor.duplicado"),
  CLIENTE_NAO_ENCONTRADO("cliente.nao.encontrado"),
  CARRINHO_NAO_ENCONTRADO("carrinho.nao.encontrado"),
  CARRINHO_ATIVO_NAO_EXISTE("carrinho.ativo.nao_existe"),
  CARRINHO_NAO_ATIVO("carrinho.nao.ativo"),
  ITEM_CARRINHO_NAO_ENCONTRADO("item-carrinho.nao.encontrado"),
  ITEM_CARRINHO_QUANTIDADE_MENOR_QUE_UM("item-carrinho.quantidade.menor.que.um"),
  PAGAMENTO_NAO_ENCONTRADO("pagamento.nao.encontrado"),
  PEDIDO_JA_POSSUI_PAGAMENTO("pedido.ja.possui.pagamento"),
  PAGAMENTO_JA_CONFIRMADO("pagamento.ja.confirmado"),
  FORMA_PAGAMENTO_NAO_SUPORTADA("forma.pagamento.nao.suportada"),
  ESTOQUE_NAO_ENCONTRADO("estoque.nao.encontrado"),
  ESTOQUE_JA_EXISTE("estoque.ja.existe"),
  QUANTIDADE_INSUFICIENTE_ESTOQUE("quantidade.insuficiente.estoque"),
  QUANTIDADE_INVALIDA("quantidade.invalida"),
  CUSTO_INVALIDO("custo.invalido"),
  ESTOQUE_INVALIDO("estoque.invalido"),
  PEDIDO_NAO_POSSUI_PAGAMENTO("pedido.nao.possui.pagamento"),
  CLIENTE_NAO_E_DONO_DO_PEDIDO("cliente.nao.e.dono.do.pedido"),
  PEDIDO_NAO_POSSUI_RASTREIO("pedido.nao.possui.rastreio"),
  STATUS_PAGAMENTO_INVALIDO("status.de.pagamento.invalido"),
  PAGAMENTO_JA_PROCESSADO("pagamento.ja.processado"),
  EMAIL_INFORMADO_NAO_ENCONTRADO("email.informado.nao.encontrado"),
  TELEFONE_INFORMADO_NAO_ENCONTRADO("telefone.informado.nao.encontrado"),
  LIMITE_DE_TENTATIVAS_EXCEDIDO("limite.de.tentativas.excedido"),
  PAGAMENTO_JA_PROCESSADO_COM_SUCESSO("pagamento.ja.processado.com.sucesso"),
  PAGAMENTO_NAO_PODE_SER_REPROCESSADO("pagamento.nao.pode.ser.reprocessado"),
  CLIENTE_JA_ATIVO("cliente.ja.ativo"),
  CLIENTE_JA_INATIVO("cliente.ja.inativo"),
  CLIENTE_INATIVO("cliente.inativo"),
  TRANSICAO_STATUS_INVALIDA("transicao.status.invalida"),
  MOTIVO_ESTORNO_OBRIGATORIO("motivo.estorno.obrigatorio"),
  PAGAMENTO_NAO_PODE_SER_ESTORNADO("pagamento.nao.pode.ser.estornado"),
  PRODUTO_JA_ATIVO("produto.ja.ativo"),
  PRODUTO_JA_INATIVO("produto.ja.inativo"),
  CARRINHO_NAO_PERTENCE_AO_CLIENTE_AUTENTICADO("carrinho.nao.pertence.ao.cliente.autenticado"),
  RELATORIO_DATA_OBRIGATORIA("relatorio.data.obrigatoria"),
  RELATORIO_DATA_INVALIDA("relatorio.data.invalida"),
  CRITERIO_BUSCA_OBRIGATORIO("criterio.busca.obrigatorio"),
  PRECO_NAO_ENCONTRADO("preco.nao.encontrado"),
  PRECO_INVALIDO("preco.invalido");

  private final String code;

  ErrorCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
