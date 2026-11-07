# HeCommerce — inicialização e testes

Este guia descreve como compilar e testar os serviços, iniciar o ambiente local e
validar o fluxo Products → Orders → Kafka através do Gateway.

## Pré-requisitos

- Docker com Docker Compose.
- Java 21 para os builds Gradle.
- Node.js e npm para testar o frontend fora do Docker.
- `curl` para os testes das APIs.

Confira as versões:

```bash
java -version
docker --version
docker compose version
node --version
npm --version
```

## 1. Iniciar o ambiente

Na raiz do repositório, construa as imagens e inicie os serviços e dependências:

```bash
cd /workspaces/hecommerce
docker compose up --build -d
```

Confira o estado dos containers:

```bash
docker compose ps
```


## 3. Verificar Products através do Gateway

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

## 4. Verificar Orders

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

## 5. Verificar o evento Kafka

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

## 6. Abrir o frontend

Com os serviços em execução, abra:

```text
http://localhost:5173
```

O frontend consulta a vitrine de produtos por meio do proxy para o Gateway. O
checkout visual ainda não cria pedidos; para testar a criação, use o `curl` da
seção Orders.

## 7. Encerrar o ambiente

Para parar e remover os containers sem apagar os dados persistidos nos volumes:

```bash
docker compose down
```

Para iniciar novamente depois:

```bash
docker compose up -d
```
