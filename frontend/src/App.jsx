import { useState, useEffect } from "react";
import "./App.css";

const API_BASE = "/api";
const SESSION_KEY = "hecommerce_session";

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
  const [notificacoes, setNotificacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [sessao, setSessao] = useState(null);
  const [authErro, setAuthErro] = useState("");
  const [authCarregando, setAuthCarregando] = useState(false);

  useEffect(() => {
    const token = sessionStorage.getItem(SESSION_KEY);
    if (!token) return;
    fetch(`${API_BASE}/auth/me`, { headers: { Authorization: `Bearer ${token}` } })
      .then(async (res) => {
        if (!res.ok) throw new Error("Sessão expirada");
        setSessao({ token, usuario: await res.json() });
      })
      .catch(() => sessionStorage.removeItem(SESSION_KEY));
  }, []);

  const clienteId = "CLI-001";

  // Busca inicial de produtos
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

  // Polling para carregar notificações a cada 5 segundos
  useEffect(() => {
    function buscarNotificacoes() {
      fetch(`${API_BASE}/notifications/cliente/${clienteId}`)
          .then((res) => {
            if (res.ok) return res.json();
            return [];
          })
          .then((data) => setNotificacoes(data))
          .catch(() => {});
    }

    buscarNotificacoes();
    const intervalo = setInterval(buscarNotificacoes, 5000);
    return () => clearInterval(intervalo);
  }, [clienteId]);

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

  const notificacoesNaoLidas = notificacoes.filter((n) => !n.lida).length;

  function adicionarCarrinho(produto) {
    setCarrinho((atual) => [...atual, produto]);
  }

  function marcarComoLida(id) {
    fetch(`${API_BASE}/notifications/${id}/read`, { method: "PATCH" })
        .then(() => {
          setNotificacoes((atuais) =>
              atuais.map((n) => (n.id === id ? { ...n, lida: true } : n))
          );
        })
        .catch(() => {});
  }

  async function enviarFormulario(evento) {
    evento.preventDefault();
    setAuthErro("");
    setAuthCarregando(true);
    const formulario = new FormData(evento.currentTarget);
    const cadastro = tela === "cadastro";
    const body = {
      email: formulario.get("email"),
      senha: formulario.get("senha"),
      ...(cadastro ? { nome: formulario.get("nome") } : {}),
    };
    try {
      const resposta = await fetch(`${API_BASE}/auth/${cadastro ? "register" : "login"}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      const dados = await resposta.json();
      if (!resposta.ok) throw new Error(dados.erro || "Não foi possível autenticar.");
      sessionStorage.setItem(SESSION_KEY, dados.token);
      setSessao(dados);
      setTela("");
    } catch (err) {
      setAuthErro(err.message);
    } finally {
      setAuthCarregando(false);
    }
  }

  async function sair() {
    const token = sessao?.token;
    setSessao(null);
    sessionStorage.removeItem(SESSION_KEY);
    setTela("");
    if (token) {
      try {
        await fetch(`${API_BASE}/auth/logout`, {
          method: "POST", headers: { Authorization: `Bearer ${token}` },
        });
      } catch { /* O token já foi descartado no navegador. */ }
    }
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
            {sessao ? (
              <>
                <button className="botao-entrar" onClick={() => setTela("perfil")}>
                  {sessao.usuario.nome} {sessao.usuario.role === "ADMIN" ? "★" : ""}
                </button>
                {sessao.usuario.role === "ADMIN" && (
                  <button className="botao-conta" onClick={() => setTela("admin")}>Administração</button>
                )}
                <button className="botao-sair" onClick={sair}>Sair</button>
              </>
            ) : (
              <>
                <button className="botao-entrar" onClick={() => { setAuthErro(""); setTela("login"); }}>Entrar</button>
                <button className="botao-conta" onClick={() => { setAuthErro(""); setTela("cadastro"); }}>Criar conta</button>
              </>
            )}

            <button
                className="botao-notificacoes"
                type="button"
                aria-label="Notificações"
                title="Notificações"
                onClick={() => setTela("notificacoes")}
                style={{ position: "relative" }}
            >
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="22"
                  height="22"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
              >
                <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
                <path d="M10 21h4" />
              </svg>
              {notificacoesNaoLidas > 0 && (
                  <span
                      style={{
                        position: "absolute",
                        top: "-4px",
                        right: "-4px",
                        backgroundColor: "#ef4444",
                        color: "#ffffff",
                        borderRadius: "50%",
                        padding: "2px 6px",
                        fontSize: "10px",
                        fontWeight: "bold",
                        lineHeight: 1,
                      }}
                  >
                {notificacoesNaoLidas}
              </span>
              )}
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
                        <input id="email-login" name="email" type="email" autoComplete="email" placeholder="seu@email.com" required />
                        <label htmlFor="senha-login">Senha</label>
                        <input id="senha-login" name="senha" type="password" autoComplete="current-password" placeholder="Sua senha" required />
                        {authErro && <p className="erro-produtos" role="alert">{authErro}</p>}
                        <button className="botao-adicionar" type="submit" disabled={authCarregando}>{authCarregando ? "Entrando..." : "Entrar"}</button>
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
                        <input id="nome-cadastro" name="nome" type="text" autoComplete="name" placeholder="Seu nome" required />
                        <label htmlFor="email-cadastro">E-mail</label>
                        <input id="email-cadastro" name="email" type="email" autoComplete="email" placeholder="seu@email.com" required />
                        <label htmlFor="senha-cadastro">Senha</label>
                        <input id="senha-cadastro" name="senha" type="password" autoComplete="new-password" minLength="8" placeholder="Mínimo de 8 caracteres" required />
                        {authErro && <p className="erro-produtos" role="alert">{authErro}</p>}
                        <button className="botao-adicionar" type="submit" disabled={authCarregando}>{authCarregando ? "Criando..." : "Criar minha conta"}</button>
                      </form>
                      <p className="alternar-tela">
                        Já tem conta?{" "}
                        <button onClick={() => setTela("login")}>Entrar</button>
                      </p>
                    </>
                )}

                {tela === "perfil" && sessao && (
                  <>
                    <span className="subtitulo">MINHA CONTA</span>
                    <h2>{sessao.usuario.nome}</h2>
                    <p>{sessao.usuario.email}</p>
                    <p>Perfil: <strong>{sessao.usuario.role === "ADMIN" ? "Administrador" : "Cliente"}</strong></p>
                    <button className="botao-adicionar" onClick={sair}>Sair da conta</button>
                  </>
                )}

                {tela === "admin" && sessao?.usuario.role === "ADMIN" && (
                  <>
                    <span className="subtitulo">PAINEL ADMINISTRATIVO</span>
                    <h2>Gerenciar loja</h2>
                    <p>Você tem permissão para cadastrar, editar, desativar e restaurar produtos.</p>
                    <p>O formulário de gestão de produtos será integrado nesta área. A API já verifica as permissões do administrador.</p>
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

                {tela === "notificacoes" && (
                    <>
                      <span className="subtitulo">SUAS ATIVIDADES</span>
                      <h2>Notificações</h2>
                      {notificacoes.length === 0 ? (
                          <p>Nenhuma notificação recebida até o momento.</p>
                      ) : (
                          <div className="itens-carrinho">
                            {notificacoes.map((item) => (
                                <div
                                    key={item.id}
                                    onClick={() => !item.lida && marcarComoLida(item.id)}
                                    style={{
                                      display: "flex",
                                      flexDirection: "column",
                                      alignItems: "flex-start",
                                      gap: "4px",
                                      padding: "12px",
                                      borderRadius: "8px",
                                      marginBottom: "8px",
                                      cursor: item.lida ? "default" : "pointer",
                                      backgroundColor: item.lida
                                          ? "rgba(255, 255, 255, 0.05)"
                                          : "rgba(59, 130, 246, 0.15)",
                                      borderLeft: item.lida
                                          ? "3px solid transparent"
                                          : "3px solid #3b82f6",
                                    }}
                                >
                                  <div
                                      style={{
                                        display: "flex",
                                        justify: "space-between",
                                        width: "100%",
                                      }}
                                  >
                                    <strong>Pedido #{item.orderId}</strong>
                                    <small style={{ opacity: 0.7 }}>
                                      {item.timestamp
                                          ? new Date(item.timestamp).toLocaleTimeString("pt-BR", {
                                            hour: "2-digit",
                                            minute: "2-digit",
                                          })
                                          : ""}
                                    </small>
                                  </div>
                                  <span>Status: <strong>{item.status}</strong></span>
                                  <span>Valor: <strong>{formatCurrency(item.valorTotal)}</strong></span>
                                  {!item.lida && (
                                      <small style={{ color: "#3b82f6", fontWeight: "bold", marginTop: "4px" }}>
                                        • Clique para marcar como lida
                                      </small>
                                  )}
                                </div>
                            ))}
                          </div>
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
