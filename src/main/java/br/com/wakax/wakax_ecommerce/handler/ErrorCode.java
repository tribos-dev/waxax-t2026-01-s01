package br.com.wakax.wakax_ecommerce.handler;

public enum ErrorCode {
  PEDIDO_NAO_ENCONTRADO("pedido.nao.encontrado"),
  PEDIDO_NAO_POSSUI_PAGAMENTO("pedido.nao.possui.pagamento"),
  PEDIDO_JA_POSSUI_PAGAMENTO("pedido.ja.possui.pagamento"),
  PEDIDO_NAO_POSSUI_RASTREIO("pedido.nao.possui.rastreio"),

  RASTREAMENTO_NAO_ENCONTRADO("rastreamento.nao.encontrado"),
  RASTREAMENTO_JA_EXISTE("rastreamento.ja.existe"),

  PRODUTO_NAO_ENCONTRADO("produto.nao.encontrado"),
  PRODUTO_DUPLICADO("produto.duplicado"),

  FORNECEDOR_NAO_ENCONTRADO("fornecedor.nao.encontrado"),
  FORNECEDOR_DUPLICADO("fornecedor.duplicado"),

  CLIENTE_NAO_ENCONTRADO("cliente.nao.encontrado"),
  CLIENTE_JA_ATIVO("cliente.ja.ativo"),
  CLIENTE_JA_INATIVO("cliente.ja.inativo"),
  CLIENTE_INATIVO("cliente.inativo"),
  CLIENTE_NAO_E_DONO_DO_PEDIDO("cliente.nao.e.dono.do.pedido"),

  CARRINHO_NAO_ENCONTRADO("carrinho.nao.encontrado"),
  CARRINHO_ATIVO_NAO_EXISTE("carrinho.ativo.nao.existe"),
  CARRINHO_NAO_ATIVO("carrinho.nao.ativo"),

  ITEM_CARRINHO_NAO_ENCONTRADO("item.carrinho.nao.encontrado"),
  ITEM_CARRINHO_QUANTIDADE_MENOR_QUE_UM("item.carrinho.quantidade.menor.que.um"),

  PAGAMENTO_NAO_ENCONTRADO("pagamento.nao.encontrado"),
  PAGAMENTO_JA_CONFIRMADO("pagamento.ja.confirmado"),
  PAGAMENTO_JA_PROCESSADO("pagamento.ja.processado"),
  PAGAMENTO_JA_PROCESSADO_COM_SUCESSO("pagamento.ja.processado.com.sucesso"),
  PAGAMENTO_NAO_PODE_SER_REPROCESSADO("pagamento.nao.pode.ser.reprocessado"),
  FORMA_PAGAMENTO_NAO_SUPORTADA("forma.pagamento.nao.suportada"),
  STATUS_PAGAMENTO_INVALIDO("status.pagamento.invalido"),

  ESTOQUE_NAO_ENCONTRADO("estoque.nao.encontrado"),
  ESTOQUE_JA_EXISTE("estoque.ja.existe"),
  ESTOQUE_INVALIDO("estoque.invalido"),
  QUANTIDADE_INSUFICIENTE_ESTOQUE("quantidade.insuficiente.estoque"),
  QUANTIDADE_INVALIDA("quantidade.invalida"),
  CUSTO_INVALIDO("custo.invalido"),

  EMAIL_INFORMADO_NAO_ENCONTRADO("email.informado.nao.encontrado"),
  TELEFONE_INFORMADO_NAO_ENCONTRADO("telefone.informado.nao.encontrado"),
  LIMITE_DE_TENTATIVAS_EXCEDIDO("limite.de.tentativas.excedido"),
  ACESSO_NEGADO("acesso.negado"),

  RELATORIO_DATA_OBRIGATORIA("relatorio.data.obrigatoria"),
  RELATORIO_DATA_INVALIDA("relatorio.data.invalida");

  private final String code;

  ErrorCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
