package eventify.api_spring.dto.usuario;

import eventify.api_spring.domain.Usuario;
import eventify.api_spring.service.usuario.dto.UsuarioTokenDto;

import java.time.LocalDateTime;

public class UsuarioMapper {
    public static Usuario of(UsuarioCadastrarDTO usuarioCadastrarDTO) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioCadastrarDTO.getEmail());
        usuario.setNome(usuarioCadastrarDTO.getNome());
        usuario.setCpf(usuarioCadastrarDTO.getCpf());
        usuario.setSenha(usuarioCadastrarDTO.getSenha());
        usuario.setAtivo(usuarioCadastrarDTO.getAtivo());
        usuario.setBanido(usuarioCadastrarDTO.getBanido());
        usuario.setDataCriacao(LocalDateTime.now());
        return usuario;
    }

    public static UsuarioTokenDto of(Usuario usuario, String token) {
        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setUserId(usuario.getId());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setToken(token);

        return usuarioTokenDto;
    }
}