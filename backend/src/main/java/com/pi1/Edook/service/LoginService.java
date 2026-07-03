package com.pi1.Edook.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pi1.Edook.dto.LoginDto;
import com.pi1.Edook.dto.LoginResponseDto;
import com.pi1.Edook.exception.BusinessException;
import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;

@Service
public class LoginService {

    private final FuncionarioRepository repository;
    private final BCryptPasswordEncoder encoder;

    //construtor
    public LoginService(FuncionarioRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public LoginResponseDto logar(LoginDto dto) {
        //buscando o funcionario a partir do cpf e email, primeiro por cpf
        Funcionario funcionario = repository.findByCpf(dto.getIdentificador());

        //agora tento buscar pelo email, caso o usuario tenha tentando se logar pelo email
        if(funcionario == null){
            funcionario = repository.findByEmail(dto.getIdentificador());
        }

        //se nao existir tal funcionario com tal cpf ou email, então da erro
        if (funcionario == null) {
            throw new BusinessException(
                    "Usuário ou senha incorretos",
                    HttpStatus.UNAUTHORIZED // Código HTTP 401 (Não Autorizado)
            );
        }

        //verificando se o email do usuario já foi verificado
        if (!funcionario.isEmailVerificado()) {
            throw new BusinessException(
                    "Por favor, confirme seu e-mail antes de realizar o login",
                    HttpStatus.BAD_REQUEST // Código HTTP 400
            );
        }

        /*esse passo já encontrou o usuario e o email esta verificado, então aqui verifica se a senha que o usuario
        * colocou é de fato a senha do usuario */
        boolean senhaCombina = encoder.matches(dto.getSenha(), funcionario.getSenha());

        // para caso a senha nao esteja certo
        if (!senhaCombina) {
            throw new BusinessException(
                    "Usuário ou senha incorretos",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // aqui a senha bateu com a senha do banco de dados e envia um dto de resposta bem sucedida para o controller
        return new LoginResponseDto(
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getCargo(),
                "LOGIN_EFETUADO_COM_SUCESSO"
        );
    }
}