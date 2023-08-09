package eventify.api_spring.service.evento;

import eventify.api_spring.domain.buffet.Buffet;
import eventify.api_spring.domain.evento.Evento;
import eventify.api_spring.domain.usuario.Usuario;
import eventify.api_spring.dto.evento.EventoCriacaoDto;
import eventify.api_spring.dto.evento.EventoDto;
import eventify.api_spring.dto.orcamento.OrcamentoContratanteDto;
import eventify.api_spring.dto.orcamento.OrcamentoDto;
import eventify.api_spring.dto.orcamento.OrcamentoPropDto;
import eventify.api_spring.exception.http.NoContentException;
import eventify.api_spring.exception.http.NotFoundException;
import eventify.api_spring.mapper.evento.EventoMapper;
import eventify.api_spring.repository.BuffetRepository;
import eventify.api_spring.repository.EventoRepository;
import eventify.api_spring.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class EventoService {
    @Autowired
    private EventoRepository eventoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BuffetRepository buffetRepository;

    public List<EventoDto> exibirTodosEventos() {
        List<Evento> eventos = eventoRepository.findAll();

        if (eventos.isEmpty()) {
            throw new NoContentException("Não há eventos cadastrados");
        }

        return eventos.stream().map(EventoMapper::toDto).toList();
    }

    public Evento criarEvento(@Valid EventoCriacaoDto evento) {
        Optional<Buffet> buffetOpt = buffetRepository.findById(evento.getIdBuffet());
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(evento.getIdUsuario());
        
        if (buffetOpt.isEmpty() || usuarioOpt.isEmpty()) {
            throw new NotFoundException("Buffet ou usuário não encontrado");
        }
        
        Evento eventoCriado = eventoRepository.save(EventoMapper.of(evento, buffetOpt.get(), usuarioOpt.get()));
        
        return eventoCriado;
    }

    public EventoDto eventoPorId(Integer idEvento) {
        Optional<Evento> evento = eventoRepository.findById(idEvento);

        if (evento.isEmpty()) {
            throw new NotFoundException("Evento não encontrado");
        }

        EventoDto eventoDto = EventoMapper.toDto(evento.get());

        return eventoDto;
    }

    public List<OrcamentoPropDto> buscarOrcamentosDoBuffet(Integer idBuffet) {
        Optional<Buffet> buffet = buffetRepository.findById(idBuffet);

        if (buffet.isPresent()) {
            List<OrcamentoPropDto> orcamentos = eventoRepository.findAllOrcamentosByBuffet(idBuffet);
        
            if (orcamentos.isEmpty()) {
                throw new NoContentException("Não há orçamentos para este buffet");
            }

            return orcamentos;
        }

        throw new NotFoundException("Buffet não encontrado");
    }
   
    public List<OrcamentoContratanteDto> pegarOrcamentos(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isPresent()) {
            Query query = entityManager.createNativeQuery(String.format("SELECT caminho, imagem.nome, tipo,  buffet.nome as buffet_nome, descricao, data, preco, status FROM evento JOIN buffet ON id_buffet = buffet.id JOIN imagem ON imagem.id_buffet = buffet.id WHERE id_contratante=%d GROUP BY buffet.id;", idUsuario));
            List<Object[]> resultList = query.getResultList();
            
            List<OrcamentoContratanteDto> orcamentos = new ArrayList<>();
            for (Object[] result : resultList) {
                Object caminho = result[0];
                Object nome = result[1];
                Object tipo = result[2];
                Object buffet_nome = result[3];
                Object descricao = result[4];
                Object data = result[5];
                Object preco = result[6];
                Object status = result[7];
        
                orcamentos.add(new OrcamentoContratanteDto(caminho, nome, tipo, buffet_nome, descricao, data, preco, status, idUsuario));
            }

            if (orcamentos.isEmpty()) {
                throw new NoContentException("Não há orçamentos para este usuário");
            }

            return orcamentos;
        }

        throw new NotFoundException("Usuário não encontrado");
    }

    public List<EventoDto> listarEventosInfo(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }

        List<EventoDto> eventos = eventoRepository.findAllEventosInfo(idUsuario);

        if (eventos.isEmpty()) {
            throw new NoContentException("Não há eventos para este usuário");
        }

        return eventos;
    }

     public List<EventoDto> listarOrcamentos(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }

        List<EventoDto> eventos = eventoRepository.findAllOrcamentos(idUsuario);
    
        if (eventos.isEmpty()) {
            throw new NoContentException("Não há orçamentos para este usuário");
        }

        return eventos;
    }

    public OrcamentoDto buscarOrcamento(Integer idEvento) {
        Optional<Evento> evento = eventoRepository.findById(idEvento);

        if (evento.isEmpty()) {
            throw new NotFoundException("Evento não encontrado");
        }

       OrcamentoDto orcamento = eventoRepository.findOrcamentoById(idEvento);

        if (Objects.isNull(orcamento)) {
            throw new NoContentException("Não há orçamento para este evento");
        }

        return eventoRepository.findOrcamentoById(idEvento);
    }

    public Void mandarOrcamento(Integer idEvento, Double preco) {
        Optional<Evento> evento = eventoRepository.findById(idEvento);

        if (evento.isEmpty()) {
            throw new NotFoundException("Evento não encontrado");
        }

        Evento eventoAtualizado = evento.get();
        eventoAtualizado.setPreco(preco);
        eventoAtualizado.setStatus("3");
        eventoRepository.save(eventoAtualizado);

        return null;
    }

    public Integer verificarOrcamento(Integer idEvento) {
        Optional<Evento> evento = eventoRepository.findById(idEvento);

        if (evento.isEmpty()) {
            throw new NotFoundException("Evento não encontrado");
        }

        return eventoRepository.findStatusByEvento(idEvento);
    }

    public Void pagarOrcamento(Integer idEvento) {
        Optional<Evento> evento = eventoRepository.findById(idEvento);
        
        if (evento.isEmpty()) {
            throw new NotFoundException("Evento não encontrado");
        }

        Evento eventoPago = evento.get();
        eventoPago.setStatus("5");
        eventoRepository.save(eventoPago);

        return null;
    }
}
