
CREATE TABLE FUNCIONARIO (
	matricula VARCHAR(20),
	cpf VARCHAR(11),
	nome VARCHAR(100),
	email VARCHAR(100) NOT NULL,
	ddd VARCHAR(2),
	numero VARCHAR(9),
	cargo VARCHAR(20) NOT NULL,
	senha VARCHAR(100) NOT NULL,
	cpf_cadastro CHAR(11) DEFAULT NULL,
	email_verificado BOOLEAN DEFAULT FALSE,
	token_verificacao VARCHAR(255),
	token_expiracao TIMESTAMP,

	CONSTRAINT pk_cpf_funcionario
		PRIMARY KEY (cpf),

	CONSTRAINT unq_funcionario_matricula
		UNIQUE (matricula),
	
	CONSTRAINT chk_funcionario_cargo
		CHECK (cargo IN ('Docente', 'Administrativo')),

	CONSTRAINT chk_funcionario_cpf
		CHECK (cpf ~ '^[0-9]{11}$')
);

ALTER TABLE FUNCIONARIO
	ADD CONSTRAINT fk_funcionario_funcionario
		FOREIGN KEY (cpf_cadastro) REFERENCES FUNCIONARIO(cpf)
			ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE FUNCIONARIO
	ADD CONSTRAINT chk_funcionario_ddd
		CHECK (ddd ~ '^[0-9]{2}$');

ALTER TABLE FUNCIONARIO
	ADD CONSTRAINT chk_funcionario_numero
		CHECK (numero ~ '^[0-9]{8,9}$');

CREATE TABLE DISCIPLINA (
	nome VARCHAR(30),
	turma VARCHAR(20),
	horario TIME(0),
	dias VARCHAR(20),
	cpf_docente VARCHAR(11),

	CONSTRAINT pk_nome_turma_disciplina
		PRIMARY KEY (nome, turma),

	CONSTRAINT fk_disciplina_funcionario
		FOREIGN KEY (cpf_docente) REFERENCES FUNCIONARIO(cpf)
			ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE EQUIPAMENTO(
	prefixo VARCHAR(2),
	numero SMALLSERIAL,
	descricao VARCHAR(100),
	tipo VARCHAR(100),
	cpf_cadastro VARCHAR(11),
	
	CONSTRAINT pk_prefixo_numero_equipamento
		PRIMARY KEY (prefixo, numero),

	CONSTRAINT fk_equipamento_funcionario
		FOREIGN KEY (cpf_cadastro) REFERENCES FUNCIONARIO(cpf)
			ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE VIEW v_quantidade_por_prefixo AS
SELECT 
    prefixo,
    COUNT(*) AS quantidade
FROM 
    EQUIPAMENTO
GROUP BY 
    prefixo;

CREATE TABLE RESERVA (
	id SERIAL,
	nome VARCHAR(50),
	localidade VARCHAR(30),
	horario_inicio TIME,
	horario_fim TIME,
	dia DATE,
	status VARCHAR(30),
	cpf_funcionario VARCHAR(11),

	CONSTRAINT pk_id_reserva
		PRIMARY KEY (id),

	CONSTRAINT fk_reserva_funcionario
		FOREIGN KEY (cpf_funcionario) REFERENCES FUNCIONARIO(cpf)
			ON DELETE SET NULL ON UPDATE CASCADE,

	CONSTRAINT chk_reserva_status 
		CHECK (status IN ('Concluída', 'Pendente', 'Cancelada'))
);

CREATE TABLE UTILIZA (
	id_reserva INTEGER,
	prefixo_equipamento VARCHAR(2),
	numero_equipamento SMALLINT,

	CONSTRAINT pk_id_reserva_equipamento
		PRIMARY KEY (id_reserva, prefixo_equipamento, numero_equipamento),

	CONSTRAINT fk_utiliza_reserva
		FOREIGN KEY (id_reserva) REFERENCES RESERVA(id)
			ON DELETE CASCADE ON UPDATE CASCADE,

	CONSTRAINT fk_utiliza_equipamento
		FOREIGN KEY (prefixo_equipamento, numero_equipamento) REFERENCES EQUIPAMENTO(prefixo, numero)
			ON DELETE CASCADE ON UPDATE CASCADE
);


-- 1. Cadastro do primeiro funcionário (Carlos - Administrador)
INSERT INTO FUNCIONARIO (
    matricula, 
    cpf, 
    nome, 
    email, 
    ddd, 
    numero, 
    cargo, 
    senha, 
    cpf_cadastro, 
    email_verificado, 
    token_verificacao, 
    token_expiracao
) VALUES (
    'M12345', 
    '12345678901', 
    'Carlos Silva', 
    'carlos.silva@escola.com', 
    '11', 
    '999998888', 
    'Administrativo', 
    '$2a$10$x51NJQMK8v/E2u0MFBVuJ.bLquf3kPffyRVuVURsY0SbzNS9jbvYu', -- Senha original: Admin@2026
    NULL,
    TRUE,
    NULL, 
    NULL
);

-- 2. Cadastro da segunda funcionária (Ana - Docente, cadastrada pelo Carlos)
INSERT INTO FUNCIONARIO (
    matricula, 
    cpf, 
    nome, 
    email, 
    ddd, 
    numero, 
    cargo, 
    senha, 
    cpf_cadastro, 
    email_verificado, 
    token_verificacao, 
    token_expiracao
) VALUES (
    'M67890', 
    '98765432100', 
    'Ana Souza', 
    'ana.souza@escola.com', 
    '21', 
    '988887777', 
    'Docente', 
    '$2a$10$NGoIN9TjOg6wyTRO.9eYJOzTyjQGOU/fbp9jx7x2yADmdy8LQr9Rq', -- Senha original: Prof@2026
    '12345678901',
    TRUE,
    NULL, 
    NULL
);

-- 1. Inserção da primeira disciplina
INSERT INTO DISCIPLINA (
    nome, 
    turma, 
    horario, 
    dias, 
    cpf_docente
) VALUES (
    'Português', 
    'INF-2026.1', 
    '08:00:00', 
    'Segunda e Quarta', 
    '98765432100' -- CPF da Ana Souza (Docente)
);

-- 2. Inserção da segunda disciplina
INSERT INTO DISCIPLINA (
    nome, 
    turma, 
    horario, 
    dias, 
    cpf_docente
) VALUES (
    'Redação', 
    'INF-2026.2', 
    '10:00:00', 
    'Terça e Quinta', 
    '98765432100' -- CPF da Ana Souza (Docente)
);

-- 1. Inserção do primeiro equipamento (Projetor)
INSERT INTO EQUIPAMENTO (
    prefixo, 
    descricao,
	tipo,
    cpf_cadastro
) VALUES (
    'PR', 
    'Projetor Epson X41',
	'Projetor',
    '12345678901' -- CPF do Carlos Silva (Administrativo)
);

-- 2. Inserção do segundo equipamento (Notebook)
INSERT INTO EQUIPAMENTO (
    prefixo, 
    descricao,
	tipo,
    cpf_cadastro
) VALUES (
    'NT', 
    'Notebook Dell Latitude',
	'Notebook',
    '12345678901' -- CPF do Carlos Silva (Administrativo)
);

-- 1. Inserção da primeira reserva (Concluída)
INSERT INTO RESERVA (
    nome,
    localidade, 
    horario_inicio, 
    horario_fim, 
    dia, 
    status, 
    cpf_funcionario
) VALUES (
    'Aula de Português',
    'Auditório Principal', 
    '08:00:00', 
    '10:00:00', 
    '2026-08-22', 
    'Concluída', -- Respeita o CHECK ('Concluída', 'Pendente', 'Cancelada')
    '98765432100'  -- CPF da Ana Souza
);

-- 2. Inserção da segunda reserva (Pendente)
INSERT INTO RESERVA (
    nome,
    localidade, 
    horario_inicio, 
    horario_fim, 
    dia, 
    status, 
    cpf_funcionario
) VALUES (
    'Aula de Redação',
    'Sala 01', 
    '10:00:00', 
    '12:00:00', 
    '2026-08-23', 
    'Pendente', 
    '98765432100'  -- CPF da Ana Souza
);

-- 3. Reserva Cancelada (Carlos Silva)
INSERT INTO RESERVA (
    nome,
    localidade, 
    horario_inicio, 
    horario_fim, 
    dia, 
    status, 
    cpf_funcionario
) VALUES (
    'Reunião Pedagógica',
    'Sala de Reuniões', 
    '14:00:00', 
    '16:00:00', 
    '2026-06-24', 
    'Cancelada',
    '12345678901'  -- CPF do Carlos Silva (Administrativo)
);

-- 1. Alocando o Projetor (PR 1) para a Reserva 1
INSERT INTO UTILIZA (
    id_reserva, 
    prefixo_equipamento, 
    numero_equipamento
) VALUES (
    1,
    'PR',
    1 
);

-- 2. Alocando o Notebook (NT 2) para a Reserva 2
INSERT INTO UTILIZA (
    id_reserva, 
    prefixo_equipamento, 
    numero_equipamento
) VALUES (
    2,
    'NT',
    2
);