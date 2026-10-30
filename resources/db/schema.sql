CREATE TABLE CLIENTE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE PRODUTO (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    preco DECIMAL(10, 2) NOT NULL CHECK (preco > 0)
);

CREATE TABLE PEDIDO (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id) REFERENCES CLIENTE (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE ITEM_PEDIDO (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL CHECK (preco_unitario > 0),
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    CONSTRAINT fk_item_pedido
        FOREIGN KEY (pedido_id) REFERENCES PEDIDO (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_item_produto
        FOREIGN KEY (produto_id) REFERENCES PRODUTO (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

INSERT INTO CLIENTE (nome, email) VALUES
    ('Ana Tayna Reis Maciel', 'ana.maciel@email.com'),
    ('Lucas Almeida de Souza', 'lucas.souza@email.com');

INSERT INTO PRODUTO (nome, preco) VALUES
    ('Notebook Gamer Pro', 5499.90),
    ('Mouse Sem Fio Ergonomico', 189.50),
    ('Teclado Mecanico RGB', 349.00);

INSERT INTO PEDIDO (data_pedido, cliente_id) VALUES
    (CURRENT_TIMESTAMP, 1);

INSERT INTO ITEM_PEDIDO (quantidade, preco_unitario, pedido_id, produto_id) VALUES
    (1, 5499.90, 1, 1),
    (2, 189.50, 1, 2);
