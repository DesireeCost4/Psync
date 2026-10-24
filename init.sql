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
                                   razao_social VARCHAR(200),
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
                                  email VARCHAR(150) NOT NULL UNIQUE,
                                  senha VARCHAR(255),
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

-- 6. Agendamentos de consultas/sessões
CREATE TABLE tb_agendamento (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                inicio DATETIME NOT NULL,
                                fim DATETIME NOT NULL,
                                titulo VARCHAR(150) NOT NULL,
                                observacoes VARCHAR(1000),
                                status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
                                cliente_adm_id BIGINT NOT NULL,
                                cliente_final_id BIGINT NOT NULL,
                                CONSTRAINT fk_agendamento_adm FOREIGN KEY (cliente_adm_id)
                                    REFERENCES tb_cliente_adm(id) ON DELETE CASCADE,
                                CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cliente_final_id)
                                    REFERENCES tb_cliente_final(id) ON DELETE CASCADE
);

-- 7. Processos e indicadores jurídicos
CREATE TABLE tb_processo (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             numero VARCHAR(100) NOT NULL,
                             titulo VARCHAR(200) NOT NULL,
                             proximo_prazo DATE,
                             ultima_movimentacao DATE,
                             intimacao_pendente BOOLEAN NOT NULL DEFAULT FALSE,
                             publicacao_pendente BOOLEAN NOT NULL DEFAULT FALSE,
                             honorarios_a_receber DECIMAL(10,2) NOT NULL DEFAULT 0,
                             status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
                             cliente_adm_id BIGINT NOT NULL,
                             cliente_final_id BIGINT NOT NULL,
                             CONSTRAINT fk_processo_adm FOREIGN KEY (cliente_adm_id)
                                 REFERENCES tb_cliente_adm(id) ON DELETE CASCADE,
                             CONSTRAINT fk_processo_cliente FOREIGN KEY (cliente_final_id)
                                 REFERENCES tb_cliente_final(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- CARGA INICIAL DE TESTE (Evita o erro de FK no backend)
-- -----------------------------------------------------

-- Insere o segmento padrão (ID = 1)
INSERT IGNORE INTO tb_segmento_adm (id, nome) VALUES (1, 'PSICOLOGIA');
INSERT IGNORE INTO tb_segmento_adm (nome) VALUES ('ODONTOLOGIA');
INSERT IGNORE INTO tb_segmento_adm (nome) VALUES ('MEDICINA');
INSERT IGNORE INTO tb_segmento_adm (nome) VALUES ('ADVOCACIA');
INSERT IGNORE INTO tb_segmento_adm (nome) VALUES ('OUTRO');

-- Insere os dados cadastrais do Admin Mock (ID = 1)
INSERT IGNORE INTO tb_dados_cadastro (id, nome, cpf_cnpj, telefone, endereco)
VALUES (1, 'Administrador Psync', '00000000000', '83999999999', 'Rua Principal, 123');

-- Insere o Cliente Admin Mock (ID = 1)
-- Login de teste: admin@teste.com / password
INSERT IGNORE INTO tb_cliente_adm (id, email, senha, segmento_id, dados_cadastro_id)
VALUES (1, 'admin@teste.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        1, 1);