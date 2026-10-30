# Atividade de Arquitetura de Software — Sistema de Gestão de Produtos

Sistema web de gerenciamento de produtos desenvolvido em **Java Puro** (sem frameworks externos como Spring ou Hibernate), aplicando **Arquitetura em Camadas** de padrão corporativo, princípios **SOLID**, **Value Objects imutáveis**, **DTOs** e **Global Error Handler**.

---

## 1. Objetivo da Atividade

Demonstrar na prática como estruturar um software desacoplado, testável e de fácil manutenção, garantindo que:
- **Nenhuma regra de negócio vaze para a interface web ou para o controlador HTTP.**
- **Nenhum objeto de domínio exista em estado inválido na memória** (auto-validação via Value Objects).
- **A Entidade de Domínio (`Produto`) fique protegida**, trafegando dados entre a camada de Apresentação e a camada de Negócio exclusivamente por meio de **DTOs (`record`)**.
- **Todos os arquivos do projeto respeitem o limite estrito de no máximo 100 linhas** e **zero comentários**, com código autodescritivo.

---

## 2. Funcionalidades do Sistema (CRUD Completo)

- **Cadastrar Produto (`POST /produtos/salvar`)**: cria um novo produto validando nome e preço.
- **Listar Produtos (`GET /produtos`)**: exibe todos os produtos cadastrados, contador total do catálogo e sincroniza os dados no `localStorage` do navegador.
- **Editar Produto (`POST /produtos/salvar` com `id`)**: permite alterar tanto o **nome** quanto o **preço** de qualquer produto existente.
- **Remover Produto (`POST /produtos/remover`)**: exclui um produto do catálogo a partir do seu identificador.

---

## 3. Arquitetura em Camadas (`src/com/exemplo/camadas`)

| Pacote | Arquivos | Responsabilidade |
| :--- | :--- | :--- |
| **`view/`** | `produtos.html`, `estilos.css` | Interface gráfica web (HTML5, CSS3 moderno e script de renderização/sincronização com `localStorage`). |
| **`config/`** | `ServidorWebConfig.java` | Configuração do servidor HTTP nativo (`HttpServer` na porta `8080`), entrega de HTML/CSS e redirecionamentos. |
| **`controller/`** | `ProdutoController.java` | Recebe requisições HTTP, extrai parâmetros via DTO, aciona a interface `IProdutoService` e delega falhas ao `GlobalErrorHandler`. |
| **`handler/`** | `GlobalErrorHandler.java` | Manipulador centralizado de exceções: captura erros de validação (`DomainValidationException` / `IllegalArgumentException` -> HTTP 422) e erros inesperados (`Exception` -> HTTP 500), devolvendo mensagens amigáveis para a View. |
| **`dto/`** | `ProdutoRequestDTO.java`, `ProdutoResponseDTO.java`, `ContextoPaginaDTO.java` | Objetos imutáveis (`record`) para transporte de dados entre Controller, View e Service sem expor a entidade `Produto`. |
| **`service/`** | `IProdutoService.java`, `ProdutoService.java` | Camada de Negócio: orquestra o CRUD, verifica existência de IDs e impede nomes duplicados no catálogo. |
| **`mapper/`** | `ProdutoMapper.java` | Converte `ProdutoRequestDTO` em entidade `Produto` e `Produto` em `ProdutoResponseDTO`. |
| **`model/`** | `Produto.java` | Entidade de domínio imutável composta por `id`, `NomeProduto` e `Preco`. |
| **`valueobject/`** | `NomeProduto.java`, `Preco.java` | Value Objects imutáveis que encapsulam as validações de atributos (nome entre 3 e 120 caracteres; preço obrigatório, positivo e com 2 casas decimais). |
| **`repository/`** | `IProdutoRepository.java`, `ProdutoRepository.java` | Camada de Persistência em memória (`ConcurrentSkipListMap` + gerador sequencial `AtomicLong`), isolada atrás da interface `IProdutoRepository`. |
| **`exception/`** | `DomainValidationException.java` | Exceção personalizada para violações de regras de domínio e negócio. |

---

## 4. Princípios SOLID Aplicados

- **SRP (Single Responsibility Principle)**: cada classe possui uma única responsabilidade clara (`ProdutoController` apenas roteia HTTP; `ProdutoMapper` apenas converte objetos; `ProdutoService` apenas aplica regras de negócio do catálogo; `Preco` e `NomeProduto` apenas validam seus próprios valores).
- **OCP (Open/Closed Principle)**: novas implementações de serviço ou persistência podem ser adicionadas implementando `IProdutoService` ou `IProdutoRepository` sem alterar os consumidores.
- **LSP (Liskov Substitution Principle)**: `ProdutoService` e `ProdutoRepository` respeitam integralmente os contratos definidos em suas interfaces.
- **ISP (Interface Segregation Principle)**: interfaces enxutas e coesas (`IProdutoService` e `IProdutoRepository`).
- **DIP (Dependency Inversion Principle)**: `ProdutoController` e `GlobalErrorHandler` dependem da abstração `IProdutoService`; `ProdutoService` depende da abstração `IProdutoRepository`. Todas as dependências são injetadas via construtor na classe principal `App.java`.

---

## 5. Fluxo da Regra de Negócio do Preço (Ponta a Ponta)

1. **Tentativa de Submissão na View (`produtos.html`)**: o usuário preenche um preço negativo (ex.: `-50.00`) ou zero e envia o formulário (`POST /produtos/salvar`).
2. **Recebimento no Controller (`ProdutoController`)**: o controlador lê os parâmetros enviados, encapsula no `ProdutoRequestDTO` e chama `produtoService.cadastrar(dadosRequisicaoProduto)`.
3. **Orquestração e Mapeamento (`ProdutoService` -> `ProdutoMapper`)**: o serviço aciona `produtoMapper.converterParaEntidadeProduto(dadosRequisicao)`, que tenta instanciar `new Preco(dadosRequisicao.preco())`.
4. **Bloqueio no Value Object (`Preco`)**: o construtor de `Preco` executa `validar()`. Ao identificar `candidato.compareTo(BigDecimal.ZERO) < 0`, interrompe imediatamente a criação do objeto lançando `DomainValidationException("O preço do produto não pode ser negativo.")`. A entidade `Produto` sequer chega a ser criada e o `ProdutoRepository` nunca é chamado.
5. **Tratamento Centralizado (`GlobalErrorHandler`)**: o bloco `catch` do `ProdutoController` repassa a exceção ao `globalErrorHandler.capturar(excecaoCapturada)`, que monta um `ContextoPaginaDTO` com status HTTP `422`, preserva a listagem atual de produtos e preenche a mensagem de erro.
6. **Exibição Amigável (`ServidorWebConfig` -> `produtos.html`)**: a página é renderizada exibindo o alerta vermelho com a mensagem `"O preço do produto não pode ser negativo."`, mantendo a tabela de produtos intacta.

---

## 6. Como Compilar e Executar

```powershell
# 1. Compilar todos os arquivos Java para a pasta bin/
$sources = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -encoding UTF-8 -d bin $sources

# 2. Iniciar o servidor HTTP
java -cp bin com.exemplo.camadas.App
```

Acesse no navegador: **`http://localhost:8080/produtos`**
