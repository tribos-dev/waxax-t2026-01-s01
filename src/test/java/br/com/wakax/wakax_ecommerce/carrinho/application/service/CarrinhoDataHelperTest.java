package br.com.wakax.wakax_ecommerce.carrinho.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.ItemCarrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.StatusCarrinho;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import br.com.wakax.wakax_ecommerce.produto.domain.StatusProduto;
import br.com.wakax.wakax_ecommerce.produto.domain.TipoPreco;

class CarrinhoDataHelperTest {

  @Test
  void deveCriarProdutoComSucesso() {
    Produto produto = CarrinhoDataHelper.criaProduto();

    assertNotNull(produto);
    assertEquals(UUID.fromString("06a7f8a7-3b28-44d4-9467-5f1e3153ce1a"), produto.getId());
    assertEquals("Placa de Vídeo RTX 9090", produto.getDescricao());
    assertEquals(StatusProduto.ATIVO, produto.getStatus());
    assertEquals(new BigDecimal("0.850"), produto.getPesoLiquido());
    assertEquals(new BigDecimal("1.200"), produto.getPesoBruto());
    assertEquals("A mais potente do mercado.", produto.getDescricaoComplementar());
    assertEquals("HARDWARE", produto.getGrupo());
    assertEquals("UN", produto.getUnidade());
    assertEquals(10, produto.getEstoqueMinimo());
    assertEquals(100, produto.getEstoqueMaximo());
    assertFalse(produto.getPrecos().isEmpty());
    assertEquals(1, produto.getPrecos().size());

    Preco preco = produto.getPrecos().get(0);
    assertNotNull(preco);
    assertEquals(TipoPreco.PADRAO, preco.getTipo());
    assertEquals(new BigDecimal("5500.00"), preco.getValor());
    assertEquals(produto, preco.getProduto());
  }

  @Test
  void deveCriarSegundoProdutoValidoComSucesso() {
    Produto produto = CarrinhoDataHelper.criaSegundoProdutoValido();

    assertNotNull(produto);
    assertEquals(UUID.fromString("e6f21a43-0545-4202-848d-639ff3a093b4"), produto.getId());
    assertEquals("Processador Atomic i99", produto.getDescricao());
    assertEquals(StatusProduto.ATIVO, produto.getStatus());
    assertEquals(new BigDecimal("0.150"), produto.getPesoLiquido());
    assertEquals(new BigDecimal("0.300"), produto.getPesoBruto());
    assertEquals("HARDWARE", produto.getGrupo());
    assertEquals("UN", produto.getUnidade());
    assertFalse(produto.getPrecos().isEmpty());
    assertEquals(1, produto.getPrecos().size());

    Preco preco = produto.getPrecos().get(0);
    assertEquals(TipoPreco.PADRAO, preco.getTipo());
    assertEquals(new BigDecimal("4500.50"), preco.getValor());
    assertEquals(produto, preco.getProduto());
  }

  @Test
  void deveCriarPessoaValidaComSucesso() {
    var pessoa = CarrinhoDataHelper.criaPessoaValida();

    assertNotNull(pessoa);
    assertEquals(UUID.fromString("c2c2a2b0-8c23-4e38-a2b8-a2b8e3a2c2c2"), pessoa.getId());
    assertEquals("João da Silva", pessoa.getNome());
    assertEquals("123.456.789-00", pessoa.getCpfCnpj());
    assertEquals(1, pessoa.getEmails().size());
    assertEquals("joao.silva@email.com", pessoa.getEmails().get(0));
    assertEquals(1, pessoa.getTelefones().size());
    assertEquals("11987654321", pessoa.getTelefones().get(0));
    assertEquals(StatusPessoa.ATIVO, pessoa.getStatus());
  }

  @Test
  void deveCriarClienteComSucesso() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();

    assertNotNull(cliente);
    assertEquals(UUID.fromString("a1a1a1a1-b2b2-c3c3-d4d4-e5e5e5e5e5e5"), cliente.getId());
    assertNotNull(cliente.getPessoa());
    assertEquals("João da Silva", cliente.getPessoa().getNome());
    assertNotNull(cliente.getDataCriacao());
    assertNotNull(cliente.getDataEdicao());
  }

  @Test
  void deveCriarItemCarrinhoRequestComSucesso() {
    UUID idProduto = UUID.randomUUID();
    ItemCarrinhoRequest request = CarrinhoDataHelper.criaItemCarrinhoRequest(idProduto);

    assertNotNull(request);
    assertEquals(idProduto, request.getIdProduto());
    assertEquals(2, request.getQuantidade());
  }

  @Test
  void deveCriarItemCarrinhoComSucesso() {
    ItemCarrinho item = CarrinhoDataHelper.criaItemCarrinho();

    assertNotNull(item);
    assertNotNull(item.getCarrinho());
    assertNotNull(item.getProduto());
    assertEquals(2, item.getQuantidade());
    assertNotNull(item.getPrecoUnitario());
    assertEquals("Placa de Vídeo RTX 9090", item.getProduto().getDescricao());
  }

  @Test
  void deveCriarCarrinhoAtivoVazioComSucesso() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoVazio(cliente);

    assertNotNull(carrinho);
    assertEquals(UUID.fromString("3b7c8a6f-3c5d-4a1e-8f9a-2b7c8a6f3c5d"), carrinho.getId());
    assertEquals(cliente, carrinho.getCliente());
    assertEquals(StatusCarrinho.ATIVO, carrinho.getStatusCarrinho());
    assertNotNull(carrinho.getDataCriacao());
    assertTrue(carrinho.getItensCarrinho().isEmpty());
  }

  @Test
  void deveCriarCarrinhoFinalizadoDeOntemComSucesso() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoFinalizadoDeOntem(cliente);

    assertNotNull(carrinho);
    assertEquals(UUID.fromString("3b7c8a6f-3c5d-4a1e-8f9a-2b7c8a6f3c5d"), carrinho.getId());
    assertEquals(cliente, carrinho.getCliente());
    assertEquals(StatusCarrinho.FINALIZADO, carrinho.getStatusCarrinho());
    assertNotNull(carrinho.getDataCriacao());
    assertTrue(carrinho.getItensCarrinho().isEmpty());
  }

  @Test
  void deveCriarCarrinhoAtivoComUmItemComSucesso() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);

    assertNotNull(carrinho);
    assertEquals(UUID.fromString("3b7c8a6f-3c5d-4a1e-8f9a-2b7c8a6f3c5d"), carrinho.getId());
    assertEquals(cliente, carrinho.getCliente());
    assertEquals(StatusCarrinho.ATIVO, carrinho.getStatusCarrinho());
    assertEquals(1, carrinho.getItensCarrinho().size());

    ItemCarrinho item = carrinho.getItensCarrinho().get(0);
    assertEquals(UUID.fromString("e5d4c3b2-a1f0-9e8d-7c6b-5a4f3e2d1c0b"), item.getId());
    assertEquals(2, item.getQuantidade());
    assertEquals("Placa de Vídeo RTX 9090", item.getProduto().getDescricao());
    assertEquals(new BigDecimal("5500.00"), item.getPrecoUnitario());
  }

  @Test
  void deveCriarCarrinhoFinalizadoComUmItemComSucesso() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoFinalizadoComUmItem(cliente);

    assertNotNull(carrinho);
    assertEquals(UUID.fromString("3b7c8a6f-3c5d-4a1e-8f9a-2b7c8a6f3c5d"), carrinho.getId());
    assertEquals(cliente, carrinho.getCliente());
    assertEquals(StatusCarrinho.FINALIZADO, carrinho.getStatusCarrinho());
    assertEquals(1, carrinho.getItensCarrinho().size());

    ItemCarrinho item = carrinho.getItensCarrinho().get(0);
    assertEquals(UUID.fromString("e5d4c3b2-a1f0-9e8d-7c6b-5a4f3e2d1c0b"), item.getId());
    assertEquals(2, item.getQuantidade());
    assertEquals("Placa de Vídeo RTX 9090", item.getProduto().getDescricao());
    assertEquals(new BigDecimal("5500.00"), item.getPrecoUnitario());
  }
}
