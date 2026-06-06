
CREATE TABLE FUNCIONARIO (
	matricula VARCHAR(20),
	cpf CHAR(11),
	nome VARCHAR(100),
	email VARCHAR(100) NOT NULL,
	ddd CHAR(2),
	numero VARCHAR(9),
	cargo VARCHAR(20) NOT NULL,
	senha VARCHAR(100) NOT NULL,
	cpf_cadastro CHAR(11) DEFAULT NULL,
	email_verificado BOOLEAN DEFAULT FALSE,
	token_verificacao VARCHAR(255),

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
	cpf_docente CHAR(11),

	CONSTRAINT pk_nome_turma_disciplina
		PRIMARY KEY (nome, turma),

	CONSTRAINT fk_disciplina_funcionario
		FOREIGN KEY (cpf_docente) REFERENCES FUNCIONARIO(cpf)
			ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE EQUIPAMENTO(
	prefixo CHAR(2),
	numero SMALLSERIAL,
	descricao VARCHAR(100),
	cpf_cadastro CHAR(11),
	
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
	nome VARCHAR(20),
	localidade VARCHAR(30),
	horario_inicio TIME,
	horario_fim TIME,
	dia DATE,
	status VARCHAR(30),
	cpf_funcionario CHAR(11),

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
	prefixo_equipamento CHAR(2),
	numero_equipamento SMALLINT,

	CONSTRAINT pk_id_reserva_equipamento
		PRIMARY KEY (id_reserva, prefixo_equipamento, numero_equipamento),

	CONSTRAINT fk_utiliza_reserva
		FOREIGN KEY (id_reserva) REFERENCES RESERVA(id)
			ON DELETE CASCADE ON UPDATE CASCADE,

	CONSTRAINT fk_utiliza_equipamento
		FOREIGN KEY (prefixo_equipamento, numero_equipamento) REFERENCES EQUIPAMENTO(prefixo, numero)
			ON DELETE SET NULL ON UPDATE CASCADE
);
