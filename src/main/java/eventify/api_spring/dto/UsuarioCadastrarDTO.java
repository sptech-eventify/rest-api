package eventify.api_spring.dto;

public record UsuarioCadastrarDTO(
        String nome, String email, String senha
        , String cpf, Integer tipoUsuario
) {

}
