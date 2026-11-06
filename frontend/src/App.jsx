
import { useState, useEffect } from "react";
import "./App.css";

const API_BASE = "/api";

function formatCurrency(value) {
  return Number(value).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function App() {
  const [busca, setBusca] = useState("");
  const [tela, setTela] = useState("");
  const [carrinho, setCarrinho] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    fetch(`${API_BASE}/products`)
      .then((res) => {
        if (!res.ok) throw new Error("Erro ao carregar produtos");
        return res.json();
      })
      .then((data) => setProdutos(data))
      .catch((err) => {
        setErro(err.message);
        setProdutos([]);
      })
      .finally(() => setCarregando(false));
  }, []);

  const produtosFiltrados = produtos.filter((produto) =>
    produto.nome.toLowerCase().includes(busca.toLowerCase())
  );

  const itensAgrupados = carrinho.reduce((itens, produto) => {
    const itemExistente = itens.find((item) => item.id === produto.id);
    if (itemExistente) {
      itemExistente.quantidade += 1;
    } else {
      itens.push({ ...produto, quantidade: 1 });
    }
    return itens;
  }, []);

  const totalCarrinho = itensAgrupados.reduce(
    (total, item) => total + Number(item.preco) * item.quantidade,
    0
  );

  function adicionarCarrinho(produto) {
    setCarrinho((atual) => [...atual, produto]);
  }

  function enviarFormulario(evento) {
    evento.preventDefault();
    alert("Interface pronta! A autenticação será integrada ao backend Spring Boot.");
    setTela("");
  }

  return (
    <div className="site">
      <header className="cabecalho">
        <a className="logo" href="#inicio">
          He<span>Commerce</span>
        </a>

        <div className="campo-busca">
          <input
            type="search"
            placeholder="Buscar produtos..."
            value={busca}
            onChange={(evento) => setBusca(evento.target.value)}
          />
          <button aria-label="Buscar">⌕</button>
        </div>

        <nav className="acoes">
          <button className="botao-entrar" onClick={() => setTela("login")}>
            Entrar
          </button>
          <button className="botao-conta" onClick={() => setTela("cadastro")}>
            Criar conta
          </button>
          <button className="botao-carrinho" onClick={() => setTela("carrinho")}>
            🛒 <span>{carrinho.length}</span>
          </button>
        </nav>
      </header>

      <main id="inicio">
        <section className="banner">
          <div className="banner-conteudo">
            <span className="etiqueta">BEM-VINDO AO HECOMMERCE</span>
            <h1>Suas compras em um só lugar!</h1>
            <p>
              Encontre produtos, compare opções e descubra tudo o que você
              precisa em um só lugar.
            </p>
            <a className="botao-banner" href="#produtos">
              Explorar produtos <span>→</span>
            </a>
          </div>
          <div className="banner-ilustracao" aria-hidden="true">
            <div className="circulo circulo-grande"></div>
            <div className="caixa-produto">💻</div>
            <div className="caixa-menor">📦</div>
          </div>
        </section>

        <section className="beneficios">
          <div><span>🚚</span><div><strong>Entrega prática</strong><small>Comodidade para comprar</small></div></div>
          <div><span>🔒</span><div><strong>Compra segura</strong><small>Seus dados protegidos</small></div></div>
          <div><span>⭐</span><div><strong>Produtos selecionados</strong><small>Opções para você</small></div></div>
        </section>

        <section className="vitrine" id="produtos">
          <div className="vitrine-titulo">
            <div>
              <span className="subtitulo">NOSSA SELEÇÃO</span>
              <h2>Produtos em destaque</h2>
              <p>Confira alguns dos produtos disponíveis.</p>
            </div>
            <span className="contador">{produtosFiltrados.length} produtos</span>
          </div>

          {carregando ? (
            <p className="estado-produtos">Carregando produtos...</p>
          ) : erro ? (
            <p className="erro-produtos" role="alert">{erro}</p>
          ) : (
            <div className="grade-produtos">
              {produtosFiltrados.map((produto) => (
              <article className="produto" key={produto.id}>
                <div className="produto-imagem">
                  <span className="produto-etiqueta">Destaque</span>
                  <span className="produto-emoji">{produto.emoji}</span>
                </div>
                <div className="produto-info">
                  <span className="produto-categoria">{produto.categoria}</span>
                  <h3>{produto.nome}</h3>
                  <strong className="preco">{formatCurrency(produto.preco)}</strong>
                  <button
                    className="botao-adicionar"
                    onClick={() => adicionarCarrinho(produto)}
                  >
                    + Adicionar ao carrinho
                  </button>
                </div>
              </article>
              ))}
            </div>
          )}

          {!carregando && !erro && produtosFiltrados.length === 0 && (
            <p className="sem-produtos">
              Nenhum produto encontrado para "{busca}".
            </p>
          )}
        </section>
      </main>

      <footer className="rodape">
        <a className="logo logo-rodape" href="#inicio">
          He<span>Commerce</span>
        </a>
        <p>Sua experiência de compra, mais simples.</p>
        <small>© 2026 HeCommerce. Projeto em desenvolvimento.</small>
      </footer>

      {tela && (
        <div className="modal-fundo" onClick={() => setTela("")}>
          <section
            className="modal"
            role="dialog"
            aria-modal="true"
            onClick={(evento) => evento.stopPropagation()}
          >
            <button
              className="fechar-modal"
              onClick={() => setTela("")}
              aria-label="Fechar"
            >
              ×
            </button>

            {tela === "login" && (
              <>
                <span className="subtitulo">BEM-VINDO DE VOLTA</span>
                <h2>Entrar na conta</h2>
                <p>Acesse sua conta para continuar.</p>
                <form onSubmit={enviarFormulario}>
                  <label htmlFor="email-login">E-mail</label>
                  <input id="email-login" type="email" placeholder="seu@email.com" required />
                  <label htmlFor="senha-login">Senha</label>
                  <input id="senha-login" type="password" placeholder="Sua senha" required />
                  <button className="botao-adicionar" type="submit">Entrar</button>
                </form>
                <p className="alternar-tela">
                  Ainda não tem conta?{" "}
                  <button onClick={() => setTela("cadastro")}>Criar conta</button>
                </p>
              </>
            )}

            {tela === "cadastro" && (
              <>
                <span className="subtitulo">COMECE AGORA</span>
                <h2>Criar conta</h2>
                <p>Preencha seus dados para se cadastrar.</p>
                <form onSubmit={enviarFormulario}>
                  <label htmlFor="nome-cadastro">Nome completo</label>
                  <input id="nome-cadastro" type="text" placeholder="Seu nome" required />
                  <label htmlFor="email-cadastro">E-mail</label>
                  <input id="email-cadastro" type="email" placeholder="seu@email.com" required />
                  <label htmlFor="senha-cadastro">Senha</label>
                  <input id="senha-cadastro" type="password" minLength="6" placeholder="Mínimo de 6 caracteres" required />
                  <button className="botao-adicionar" type="submit">Criar minha conta</button>
                </form>
                <p className="alternar-tela">
                  Já tem conta?{" "}
                  <button onClick={() => setTela("login")}>Entrar</button>
                </p>
              </>
            )}

            {tela === "carrinho" && (
              <>
                <span className="subtitulo">SUA COMPRA</span>
                <h2>Meu carrinho</h2>
                {carrinho.length === 0 ? (
                  <p>Seu carrinho está vazio.</p>
                ) : (
                  <>
                    <div className="itens-carrinho">
                      {itensAgrupados.map((item) => (
                        <div className="item-carrinho" key={item.id}>
                          <span>{item.emoji} {item.nome} × {item.quantidade}</span>
                          <strong>{formatCurrency(Number(item.preco) * item.quantidade)}</strong>
                        </div>
                      ))}
                    </div>
                    <div className="total-carrinho">
                      <strong>Total</strong>
                      <strong>{formatCurrency(totalCarrinho)}</strong>
                    </div>
                    <button
                      className="botao-adicionar"
                      onClick={() => alert("O checkout será implementado na próxima etapa.")}
                    >
                      Continuar compra
                    </button>
                    <button className="limpar-carrinho" onClick={() => setCarrinho([])}>
                      Limpar carrinho
                    </button>
                  </>
                )}
              </>
            )}

          </section>
        </div>
      )}
    </div>
  );
}

export default App;
