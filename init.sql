-- 1. Segmento de Negócio
CREATE TABLE tb_segmento_adm (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 nome VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Dados de Cadastro Genéricos
CREATE TABLE tb_dados_cadastro (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   nome VARCHAR(150) NOT NULL,
                                   cpf_cnpj VARCHAR(20) UNIQUE,
                                   telefone VARCHAR(20),
                                   endereco VARCHAR(255)
);

-- 3. Cliente ADM (Profissional/Clínica)
CREATE TABLE tb_cliente_adm (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                email VARCHAR(150) NOT NULL UNIQUE,
                                senha VARCHAR(255) NOT NULL,
                                segmento_id BIGINT NOT NULL,
                                dados_cadastro_id BIGINT UNIQUE NOT NULL,
                                CONSTRAINT fk_adm_segmento FOREIGN KEY (segmento_id) REFERENCES tb_segmento_adm(id),
                                CONSTRAINT fk_adm_cadastro FOREIGN KEY (dados_cadastro_id) REFERENCES tb_dados_cadastro(id) ON DELETE CASCADE
);

-- 4. Cliente Final (Paciente/Cliente)
CREATE TABLE tb_cliente_final (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  email VARCHAR(150),
                                  cliente_adm_id BIGINT NOT NULL,
                                  dados_cadastro_id BIGINT UNIQUE NOT NULL,
                                  CONSTRAINT fk_final_adm FOREIGN KEY (cliente_adm_id) REFERENCES tb_cliente_adm(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_final_cadastro FOREIGN KEY (dados_cadastro_id) REFERENCES tb_dados_cadastro(id) ON DELETE CASCADE
);

-- 5. Mensalidade
CREATE TABLE tb_mensalidade (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                valor DECIMAL(10,2) NOT NULL,
                                data_vencimento DATE NOT NULL,
                                status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
                                cliente_adm_id BIGINT,
                                cliente_final_id BIGINT,
                                CONSTRAINT fk_mensalidade_adm FOREIGN KEY (cliente_adm_id) REFERENCES tb_cliente_adm(id) ON DELETE SET NULL,
                                CONSTRAINT fk_mensalidade_final FOREIGN KEY (cliente_final_id) REFERENCES tb_cliente_final(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- CARGA INICIAL DE TESTE (Evita o erro de FK no backend)
-- -----------------------------------------------------

-- Insere o segmento padrão (ID = 1)
INSERT INTO tb_segmento_adm (id, nome) VALUES (1, 'PSICOLOGIA');

-- Insere os dados cadastrais do Admin Mock (ID = 1)
INSERT INTO tb_dados_cadastro (id, nome, cpf_cnpj, telefone, endereco)
VALUES (1, 'Dra. Ana Silva (Admin Teste)', '00000000000', '83999999999', 'Rua Principal, 123');

-- Insere o Cliente Admin Mock (ID = 1)
INSERT INTO tb_cliente_adm (id, email, senha, segmento_id, dados_cadastro_id)
VALUES (1, 'admin@teste.com', '$2a$10$e8p2u1...', 1, 1);