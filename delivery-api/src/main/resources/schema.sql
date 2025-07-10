-- Tabela Cliente
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);
    

-- Tabela Restaurante
CREATE TABLE restaurante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    taxa_entrega DECIMAL(19,2) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL
);
    

-- Tabela Produto
CREATE TABLE produto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(19,2) NOT NULL,
    categoria VARCHAR(255) NOT NULL DEFAULT 'Indefinida',
    disponivel BOOLEAN NOT NULL,
    restaurante_id BIGINT
);

-- Tabela Pedido
CREATE TABLE pedido (
    pedido_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    cliente_id BIGINT NOT NULL,
    restaurante_id BIGINT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (restaurante_id) REFERENCES restaurante(id)
);

-- Tabela item_pedido (Pedido <-> Produto)
CREATE TABLE item_pedido (
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (pedido_id, produto_id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(pedido_id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

ALTER TABLE produto ALTER COLUMN disponivel SET DEFAULT TRUE;
