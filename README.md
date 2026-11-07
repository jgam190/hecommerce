# HeCommerce — inicialização do ambiente

Este guia descreve como iniciar o ambiente local com Docker e validar o fluxo
Products → Orders → Kafka através do Gateway.

## Pré-requisitos

- Docker com Docker Compose v2 (comando `docker compose`). No Windows, use o
  Docker Desktop com WSL2 e clone o repositório dentro do WSL.
- `curl` para os testes das APIs. No Windows, use o Git Bash ou o WSL (veja a
  nota na seção 2).
- Acesso à internet na primeira execução: as imagens baixam dependências do
  Docker Hub, do Gradle, do Maven Central e do npm.
- Cerca de 6 GB de memória disponíveis para o Docker.
- Portas livres no computador: `8080`, `8081`, `8082`, `8083` e `5173`
  (aplicações) e `5432`, `27017` e `9092` (infraestrutura).

Java e Node.js **não** são necessários: os serviços são compilados dentro das
imagens Docker.

Confira as versões:

```bash
docker --version
docker compose version
```

## 1. Iniciar o ambiente

Clone o repositório e, na raiz dele, construa as imagens e inicie os serviços e
dependências:

```bash
git clone https://github.com/jgam190/hecommerce.git
cd hecommerce
docker compose up --build -d
```

Na primeira execução o build leva vários minutos, pois cada serviço baixa o
Gradle e suas dependências. Confira o estado dos containers:

```bash
docker compose ps
```

Aguarde até que todos apareçam como `healthy`. A inicialização segue esta
ordem: bancos e Kafka, Products, Orders e Auth, Gateway e, por último, o
frontend. Se algum serviço ficar `unhealthy` ou não subir, consulte os logs:

```bash
docker compose logs -f orders
```

Portas disponíveis no computador (acessíveis apenas a partir da própria
máquina):

| Serviço    | Endereço                |
|------------|-------------------------|
| Gateway    | `http://localhost:8080` |
| Products   | `http://localhost:8081` |
| Orders     | `http://localhost:8082` |
| Auth       | `http://localhost:8083` |
| Frontend   | `http://localhost:5173` |
| PostgreSQL | `localhost:5432`        |
| MongoDB    | `localhost:27017`       |
| Kafka      | `localhost:9092`        |

Se o Docker informar que uma porta já está em uso, encerre o programa que a
ocupa ou altere apenas o número do lado esquerdo do mapeamento no
`docker-compose.yml` (por exemplo, `"127.0.0.1:5433:5432"`). Para descobrir
quem usa a porta:

```bash
# Linux e macOS
lsof -i :5432

# Windows (PowerShell ou cmd)
netstat -ano | findstr :5432
```

## 2. Verificar Products através do Gateway

O Gateway recebe as chamadas na porta `8080` e encaminha `/api/products` ao
serviço Products:

```bash
curl -i http://localhost:8080/api/products
```

Uma resposta `200 OK` com uma lista JSON confirma o encaminhamento. Se a lista
estiver vazia, crie um produto de teste:

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  --data-raw '{
    "nome": "Produto para teste integrado",
    "descricao": "Produto criado para validar o fluxo de pedidos",
    "preco": 12.50,
    "estoque": 5,
    "ativo": true,
    "categoria": "Teste",
    "emoji": "📦"
  }'
```

O resultado esperado é `201 Created`. Copie o `id` do produto retornado; ele
será usado no teste de Orders.

> Em comandos com várias linhas, cada `\` precisa ser o último caractere da
> linha, sem espaços depois dele. Também é possível colar todo o comando em uma
> única linha.
>
> No Windows, prefira o Git Bash ou o WSL, onde os comandos funcionam como
> estão. No PowerShell, `curl` é um apelido de outro comando: use `curl.exe`,
> escreva o comando em uma única linha e coloque o JSON em um arquivo, por
> exemplo `curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" --data "@pedido.json"`.

## 3. Verificar Orders

Crie um pedido pelo Gateway, substituindo `ID_DO_PRODUTO` pelo identificador
copiado da resposta de Products:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  --data-raw '{
    "userId": "teste-integracao",
    "items": [
      {
        "productId": "ID_DO_PRODUTO",
        "quantity": 1
      }
    ]
  }'
```

O esperado é `201 Created`, com o pedido, total e status `PENDING` na resposta.
Orders consulta Products para validar o item e obter seu preço; em seguida,
persiste o pedido no PostgreSQL e publica um evento no Kafka.

Consulte os pedidos do usuário de teste:

```bash
curl -i http://localhost:8080/api/orders/user/teste-integracao
```

Para consultar um pedido individual, use o `id` retornado na criação:

```bash
curl -i http://localhost:8080/api/orders/ID_DO_PEDIDO
```

## 4. Verificar o evento Kafka

Confirme que o tópico foi criado quando Orders iniciou:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --list
```

Confira detalhes do tópico:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --describe \
  --topic pedido-criado-topic
```

Em um terminal, inicie um consumidor:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic pedido-criado-topic \
  --from-beginning
```

Em outro terminal, crie um pedido pelo Gateway usando o comando da seção
anterior. O consumidor deve exibir uma mensagem JSON com `orderId`,
`clienteId`, `status` e `valorTotal`. Use `Ctrl+C` para encerrar o consumidor.
Ele lê mensagens existentes e novas; compare `clienteId` e `orderId` para
identificar o pedido deste teste.

## 5. Abrir o frontend

Com os serviços em execução, abra:

```text
http://localhost:5173
```

O frontend consulta a vitrine de produtos por meio do proxy para o Gateway. O
checkout visual ainda não cria pedidos; para testar a criação, use o `curl` da
seção Orders.

## 6. Encerrar o ambiente

Para parar e remover os containers sem apagar os dados persistidos nos volumes:

```bash
docker compose down
```

Para iniciar novamente depois:

```bash
docker compose up -d
```

Depois de atualizar o código (por exemplo, com `git pull`), reconstrua as
imagens ao iniciar:

```bash
docker compose up --build -d
```

Para apagar também os dados (produtos, pedidos e mensagens do Kafka) e começar
do zero:

```bash
docker compose down -v
```

Use esse comando também se alterar a variável `POSTGRES_PASSWORD`: o volume do
PostgreSQL mantém a senha antiga e o Orders deixaria de autenticar.