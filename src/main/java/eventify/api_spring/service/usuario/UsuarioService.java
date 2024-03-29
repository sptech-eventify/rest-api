package eventify.api_spring.service.usuario;

import eventify.api_spring.api.configuration.security.jwt.GerenciadorTokenJwt;
import eventify.api_spring.domain.usuario.Funcionario;
import eventify.api_spring.domain.usuario.NivelAcesso;
import eventify.api_spring.domain.usuario.Usuario;
import eventify.api_spring.dto.usuario.SmartSyncUsuarioTokenDto;
import eventify.api_spring.dto.usuario.UsuarioCadastrarDto;
import eventify.api_spring.dto.usuario.UsuarioDevolverDto;
import eventify.api_spring.dto.usuario.UsuarioInfoDto;
import eventify.api_spring.dto.usuario.UsuarioLoginDto;
import eventify.api_spring.exception.http.*;
import eventify.api_spring.mapper.usuario.UsuarioMapper;
import eventify.api_spring.mapper.usuario.FuncionarioMapper;
import eventify.api_spring.dto.usuario.UsuarioTokenDto;
import eventify.api_spring.repository.FuncionarioRepository;
import eventify.api_spring.repository.UsuarioRepository;
import eventify.api_spring.service.smartsync.NivelAcessoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private NivelAcessoService nivelAcessoService;

    public List<UsuarioDevolverDto> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        if (usuarios.isEmpty()) {
            throw new NoContentException("Não há usuários cadastrados");
        }

        return usuarios.stream().map(
            usuario -> new UsuarioDevolverDto(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCpf())
        ).toList();
    }

    public UsuarioInfoDto exibir(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if(usuario.isPresent()){
            return new UsuarioInfoDto(usuario.get().getNome(), usuario.get().getEmail(), usuario.get().getCpf());
        }

        throw new NotFoundException("Usuário não encontrado");
    }

    public UsuarioDevolverDto cadastrar(UsuarioCadastrarDto usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());

        if (usuarioExistente.isPresent()) {
            throw new BadRequestException("Email já cadastrado");
        } else if (usuario.getSenha().length() < 8) {
            throw new BadRequestException("Senha deve ter no mínimo 8 caracteres");
        } else if (usuario.getNome().length() < 3) {
            throw new BadRequestException("Nome deve ter no mínimo 3 caracteres");
        } else if (usuario.getTipoUsuario() == null) {
            throw new BadRequestException("Tipo de usuário não pode ser nulo");
        } else if (usuario.getTipoUsuario() != 1 &&  usuario.getTipoUsuario() != 2) {
            throw new BadRequestException("Tipo de usuário inválido");
        }

        Usuario novoUsuario = UsuarioMapper.of(usuario);

        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setIsAtivo(true);
        this.usuarioRepository.save(novoUsuario);

        return UsuarioMapper.toDevolverDto(novoUsuario);
    }

    public Boolean banir(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        
        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }

        Usuario usuarioAtualizado = usuario.get();
        usuarioAtualizado.setIsBanido(true);
        usuarioRepository.save(usuarioAtualizado);

        return true;
    }

    public void desativar(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }
        Usuario usuarioAtualizado = usuario.get();
        usuarioAtualizado.setIsAtivo(false);
        usuarioRepository.save(usuarioAtualizado);

    }

    public Void atualizar(Integer idUsuario, Usuario novoUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        UsuarioCadastrarDto usuarioAtualizado = new UsuarioCadastrarDto();
        
        if (usuario.isPresent()) {
            usuarioAtualizado.setNome(novoUsuario.getNome());
            usuarioAtualizado.setEmail(novoUsuario.getEmail());
            usuarioAtualizado.setCpf(novoUsuario.getCpf());
            usuarioAtualizado.setSenha(novoUsuario.getSenha());
            usuarioAtualizado.setTipoUsuario(novoUsuario.getTipoUsuario());
        } else {
            throw new NotFoundException("Usuário não encontrado");
        }

        usuarioRepository.save(usuario.get());
        return null;
    }

    public UsuarioTokenDto autenticar(UsuarioLoginDto usuarioLoginDto) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioLoginDto.getEmail());

        if (usuario.isPresent()) {
            if (usuario.get().getIsBanido()) {
                throw new UnauthorizedException("Usuário banido");
            } else {
                usuario.get().setIsAtivo(true);
                usuario.get().setIsBanido(false);
                usuario.get().setUltimoLogin(LocalDateTime.now());
                usuarioRepository.save(usuario.get());
            }
        }

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuarioLoginDto.getEmail(), usuarioLoginDto.getSenha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado =
                usuarioRepository.findByEmail(usuarioLoginDto.getEmail())
                        .orElseThrow(
                                () -> new ResponseStatusException(404, "Email do usuário não cadastrado", null)
                        );

        SecurityContextHolder.getContext().setAuthentication(authentication);


        final String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    public SmartSyncUsuarioTokenDto autenticarSmartSync(UsuarioLoginDto usuarioLoginDto) {
        Optional<Funcionario> funcionario = funcionarioRepository.findByEmail(usuarioLoginDto.getEmail());
        Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioLoginDto.getEmail());

        Usuario usuarioFuncionario = usuarioRepository.findByEmail("funcionario@eventify.com").orElse(null);

        Integer tipo = 0; 

        if (funcionario.isPresent()) {
            usuarioFuncionario.setId(funcionario.get().getId());
            usuarioFuncionario.setNome(funcionario.get().getNome());
            usuarioFuncionario.setEmail(funcionario.get().getEmail());
            usuarioFuncionario.setCpf(funcionario.get().getCpf());
            
            if (!funcionario.get().getIsVisivel()) {
                throw new UnauthorizedException("Usuário não permitido!");
            }

            tipo = 1;
        } else if (usuario.isPresent()) {
            if (!usuario.get().getIsAtivo()) {
                throw new UnauthorizedException("Usuário não permitido!");
            } else if (usuario.get().getTipoUsuario() == 1) {
                throw new UnauthorizedException("Usuário não permitido!");
            }

            tipo = usuario.get().getTipoUsuario();
        } else {
            throw new UnauthorizedException("Usuário não permitido!");
        }

        if (tipo == 1) {
            Funcionario funcionarioAutenticado =
                    funcionarioRepository.findByEmail(usuarioLoginDto.getEmail())
                            .orElseThrow(
                                    () -> new ResponseStatusException(404, "Email do usuário não cadastrado", null)
                            );

            UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken("funcionario@eventify.com", "123456");

            Authentication authentication = this.authenticationManager.authenticate(credentials);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String token = gerenciadorTokenJwt.generateToken(authentication);

            return FuncionarioMapper.ofFuncionario(funcionarioAutenticado, token);
        } else if (tipo == 2 || tipo == 3) {
            Usuario usuarioAutenticado =
                    usuarioRepository.findByEmail(usuarioLoginDto.getEmail())
                            .orElseThrow(
                                    () -> new ResponseStatusException(404, "Email do usuário não cadastrado", null)
                            );

            UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
            usuarioLoginDto.getEmail(), usuarioLoginDto.getSenha());

            Authentication authentication = this.authenticationManager.authenticate(credentials);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String token = gerenciadorTokenJwt.generateToken(authentication);

            NivelAcesso nivelAcessoProprietario = nivelAcessoService.findById(5);

            return UsuarioMapper.ofUsuario(usuarioAutenticado, token, nivelAcessoProprietario);
        } else {
            System.out.println("USUÁRIO NÃO PERMITIDO: LINHA: 184");
            throw new UnauthorizedException("Usuário não permitido!");
        }
    }

    public Void desbanir(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        
        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }
        
        Usuario usuarioAtualizado = usuario.get();
        usuarioAtualizado.setIsBanido(false);
        usuarioRepository.save(usuarioAtualizado);
        
        return null;
    }

    public Boolean logof(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }

        usuario.get().setIsAtivo(false);
        usuarioRepository.save(usuario.get());
        
        return true;
    }

}
