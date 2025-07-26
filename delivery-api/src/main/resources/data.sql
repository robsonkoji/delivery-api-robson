-- Inserindo Clientes
INSERT INTO cliente (nome, email, telefone, endereco) VALUES
('João Silva', 'joao.silva@email.com', '(11) 98765-4321', 'Rua das Flores, 123, São Paulo'),
('Maria Oliveira', 'maria.o@email.com', '(21) 91234-5678', 'Avenida Copacabana, 456, Rio de Janeiro');

-- Inserindo Restaurantes
INSERT INTO restaurante (nome, cnpj, endereco, categoria, telefone, taxa_entrega) VALUES
('Pizzaria do Zé', '12.345.678/0001-99', 'Rua da Pizza, 10, São Paulo', 'Pizzaria', '(11) 90000-0000', 5.00),
('Hamburgueria Top', '98.765.432/0001-11', 'Avenida do Burger, 20, Rio de Janeiro', 'Pizzaria', '(11) 90000-0000', 7.00);

-- Inserindo Produtos para a Pizzaria do Zé (restaurante_id = 1)
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES
('Pizza Margherita', 'Molho, mussarela e manjericão', 45.00, 1),
('Pizza Calabresa', 'Molho, mussarela e calabresa fatiada', 50.00, 1),
('Refrigerante Lata', 'Coca-Cola, Guaraná ou Fanta', 5.00, 1);

-- Inserindo Produtos para a Hamburgueria Top (restaurante_id = 2)
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES
('X-Burger Clássico', 'Pão, carne, queijo, alface e tomate', 30.00, 2),
('X-Bacon Especial', 'Pão, carne, queijo, bacon crocante, cebola caramelizada', 35.50, 2),
('Batata Frita', 'Porção individual de batata frita', 12.00, 2);

-- Inserindo um Pedido do cliente João Silva (cliente_id = 1) no restaurante Pizzaria do Zé (restaurante_id = 1)
INSERT INTO pedido (data_hora, status, valor_total, cliente_id, restaurante_id) VALUES
(CURRENT_TIMESTAMP, 'ENTREGUE', 95.00, 1, 1);

-- Associando produtos ao pedido (pedido_id = 1)
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 1, 45.00, 45.00),
(1, 2, 1, 50.00, 50.00);
