# Atividade de Arquitetura de Software — Arquitetura em Camadas e Modelagem ORM (Hibernate/JPA)

Sistema em **Java** estruturado em **Arquitetura em Camadas** com **Princípios SOLID**, **Value Objects**, **DTOs** e **Mapeamento Objeto-Relacional (ORM / Hibernate)** para o cenário de **Gerenciamento de Pedidos Online** (`Cliente`, `Pedido`, `ItemPedido` e `Produto`).

---

## 1. Modelagem de Persistência ORM (`model/` e `repository/`)

| Entidade Java | Tabela SQL | Relacionamentos ORM |
| :--- | :--- | :--- |
| **`Cliente`** | `CLIENTE` | `@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)` -> `List<Pedido>` |
| **`Pedido`** | `PEDIDO` | `@ManyToOne @JoinColumn(name = "cliente_id")` -> `Cliente`<br>`@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)` -> `List<ItemPedido>` |
| **`ItemPedido`** | `ITEM_PEDIDO` | `@ManyToOne @JoinColumn(name = "pedido_id")` -> `Pedido`<br>`@ManyToOne @JoinColumn(name = "produto_id")` -> `Produto` |
| **`Produto`** | `PRODUTO` | Entidade referenciada pelos itens de pedido, composta por `NomeProduto` e `Preco` |

---

## 2. Operação CRUD com `EntityManager` (`PedidoOrmRepository.java`)

- **`criarPedido(Pedido pedido)`**: abre transação (`begin()`), persiste o grafo completo de `Pedido` e `ItemPedido` em uma única chamada `entityManager.persist(pedido)` graças ao `CascadeType.ALL` e confirma (`commit()`), com `rollback()` automático em caso de falha.
- **`carregarPedidoPorId(Long id)`**: recupera o pedido e seus relacionamentos diretamente via `entityManager.find(Pedido.class, id)`.

---

## 3. Como Compilar e Executar

```powershell
$sources = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -encoding UTF-8 -d bin $sources
java -cp bin com.exemplo.camadas.App
```
