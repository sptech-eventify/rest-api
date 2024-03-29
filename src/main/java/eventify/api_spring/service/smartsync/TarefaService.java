package eventify.api_spring.service.smartsync;

import eventify.api_spring.domain.smartsync.Comentario;
import eventify.api_spring.domain.smartsync.ExecutorTarefa;
import eventify.api_spring.domain.smartsync.Tarefa;
import eventify.api_spring.domain.usuario.Funcionario;
import eventify.api_spring.domain.usuario.Usuario;
import eventify.api_spring.dto.smartsync.BucketTarefaDto;
import eventify.api_spring.dto.smartsync.ComentarioRespostaDto;
import eventify.api_spring.dto.smartsync.ExecutorDto;
import eventify.api_spring.dto.smartsync.SecaoDto;
import eventify.api_spring.dto.smartsync.SecaoBucketDto;
import eventify.api_spring.dto.smartsync.TarefaDto;
import eventify.api_spring.dto.smartsync.TarefaRespostaDto;
import eventify.api_spring.dto.smartsync.TarefaSecaoDto;
import eventify.api_spring.exception.http.NoContentException;
import eventify.api_spring.exception.http.NotFoundException;
import eventify.api_spring.exception.http.UnprocessableEntityException;
import eventify.api_spring.mapper.smartsync.TarefaMapper;
import eventify.api_spring.repository.BucketRepository;
import eventify.api_spring.repository.ComentarioRepository;
import eventify.api_spring.repository.ExecutorTarefaRepository;
import eventify.api_spring.repository.FuncionarioRepository;
import eventify.api_spring.repository.TarefaRepository;
import eventify.api_spring.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private BucketRepository bucketRepository;

    @Autowired
    private ExecutorTarefaRepository executorTarefaRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    public List<TarefaRespostaDto> exibirTodasTarefas() {
        List<Tarefa> tarefas = tarefaRepository.findAll();

        if (tarefas.isEmpty()) {
            throw new NoContentException("Não há tarefas cadastradas");
        }

        List<TarefaRespostaDto> tarefasRespostaDto = new ArrayList<>();
        for (Tarefa tarefa : tarefas) {
            TarefaRespostaDto tarefaRespostaDto = new TarefaRespostaDto();

            tarefaRespostaDto.setId(tarefa.getId());
            tarefaRespostaDto.setNome(tarefa.getNome());
            tarefaRespostaDto.setDescricao(tarefa.getDescricao());
            tarefaRespostaDto.setFibonacci(tarefa.getFibonacci());
            tarefaRespostaDto.setStatus(tarefa.getStatus());
            tarefaRespostaDto.setHorasEstimada(tarefa.getHorasEstimada());
            tarefaRespostaDto.setDataEstimada(tarefa.getDataEstimada());
            tarefaRespostaDto.setDataCriacao(tarefa.getDataCriacao());
            tarefaRespostaDto.setDataConclusao(tarefa.getDataConclusao());

            if (tarefa.getTarefaPai() != null) {
                tarefaRespostaDto.setIdTarefaPai(tarefa.getTarefaPai().getId());
            } else {
                tarefaRespostaDto.setIdTarefaPai(null);
            }

            if (tarefa.getBucket() != null) {
                tarefaRespostaDto.setIdBucket(tarefa.getBucket().getId());
            } else {
                tarefaRespostaDto.setIdBucket(null);
            }

            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(tarefa.getId());
            tarefaRespostaDto.setResponsaveis(executores);

            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(tarefa.getId());
            tarefaRespostaDto.setComentarios(comentarios);

            tarefasRespostaDto.add(tarefaRespostaDto);
        }

        return tarefasRespostaDto;
    }

    public List<TarefaRespostaDto> exibirTodasTarefasPorUsuarioId(Integer idUsuario, Boolean isFuncionario) {
        List<Object[]> tarefas = new ArrayList<>();

        if (isFuncionario) {
            Funcionario funcionario = funcionarioRepository.findById(idUsuario).orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));

            Query query = entityManager.createNativeQuery("SELECT * FROM vw_tarefas_por_usuario WHERE id_funcionario = :idFuncionario AND is_visivel = 1");
            query.setParameter("idFuncionario", funcionario.getId());
            tarefas = query.getResultList();
        } else {
            Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

            Query query = entityManager.createNativeQuery("SELECT * FROM vw_tarefas_por_usuario WHERE id_usuario = :idUsuario AND is_visivel = 1");
            query.setParameter("idUsuario", usuario.getId());
            tarefas = query.getResultList();
        }

        if (tarefas.isEmpty()) {
            throw new NoContentException("Não há tarefas cadastradas");
        }

        List<TarefaRespostaDto> tarefasRespostaDto = new ArrayList<>();

        for (Object[] tarefa : tarefas) {
            TarefaRespostaDto tarefaRespostaDto = new TarefaRespostaDto();

            tarefaRespostaDto.setId((Integer) tarefa[2]);
            tarefaRespostaDto.setNome((String) tarefa[3]);
            tarefaRespostaDto.setDescricao((String) tarefa[4]);
            tarefaRespostaDto.setFibonacci((Integer) tarefa[5]);
            tarefaRespostaDto.setStatus((Integer) tarefa[6]);
            tarefaRespostaDto.setHorasEstimada((Integer) tarefa[7]);
            tarefaRespostaDto.setDataEstimada(((Date) tarefa[8]).toLocalDate());
            tarefaRespostaDto.setDataCriacao(((Timestamp) tarefa[9]).toLocalDateTime().toLocalDate());

            if (tarefa[10] != null) {
                tarefaRespostaDto.setDataConclusao(((Timestamp) tarefa[10]).toLocalDateTime());
            } else {
                tarefaRespostaDto.setDataConclusao(null);
            }

            
            if (tarefa[12] != null) {
                tarefaRespostaDto.setIdTarefaPai((Integer) tarefa[12]);
            } else {
                tarefaRespostaDto.setIdTarefaPai(null);
            }

            if (tarefa[13] != null) {
                tarefaRespostaDto.setIdBucket((Integer) tarefa[13]);
            } else {
                tarefaRespostaDto.setIdBucket(null);
            }

            tarefaRespostaDto.setIdServico((Integer) tarefa[14]);

            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId((Integer) tarefa[2]);
            tarefaRespostaDto.setResponsaveis(executores);

            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId((Integer) tarefa[2]);
            tarefaRespostaDto.setComentarios(comentarios);

            tarefasRespostaDto.add(tarefaRespostaDto);
        }

        return tarefasRespostaDto;
    }

    public TarefaRespostaDto exibirTarefaPorId(Integer id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));
        TarefaRespostaDto tarefaDto = new TarefaRespostaDto();

        tarefaDto.setId(tarefa.getId());
        tarefaDto.setNome(tarefa.getNome());
        tarefaDto.setDescricao(tarefa.getDescricao());
        tarefaDto.setFibonacci(tarefa.getFibonacci());
        tarefaDto.setStatus(tarefa.getStatus());
        tarefaDto.setHorasEstimada(tarefa.getHorasEstimada());
        tarefaDto.setDataEstimada(tarefa.getDataEstimada());
        tarefaDto.setDataCriacao(tarefa.getDataCriacao());
        tarefaDto.setDataConclusao(tarefa.getDataConclusao());
        tarefaDto.setIsVisivel(tarefa.getIsVisivel());
        
        if (tarefa.getTarefaPai() != null) {
            tarefaDto.setIdTarefaPai(tarefa.getTarefaPai().getId());
        } else {
            tarefaDto.setIdTarefaPai(null);
        }

        if (tarefa.getBucket() != null) {
            tarefaDto.setIdBucket(tarefa.getBucket().getId());
        } else {
            tarefaDto.setIdBucket(null);
        }

        List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(tarefa.getId());
        tarefaDto.setResponsaveis(executores);

        List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(tarefa.getId());
        tarefaDto.setComentarios(comentarios);

        return tarefaDto;
    }

    public TarefaRespostaDto exibirSubtarefaPorId(Integer id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));
        TarefaRespostaDto tarefaDto = new TarefaRespostaDto();

        tarefaDto.setId(tarefa.getId());
        tarefaDto.setNome(tarefa.getNome());
        tarefaDto.setDescricao(tarefa.getDescricao());
        tarefaDto.setFibonacci(tarefa.getFibonacci());
        tarefaDto.setStatus(tarefa.getStatus());
        tarefaDto.setHorasEstimada(tarefa.getHorasEstimada());
        tarefaDto.setDataEstimada(tarefa.getDataEstimada());
        tarefaDto.setDataCriacao(tarefa.getDataCriacao());
        tarefaDto.setDataConclusao(tarefa.getDataConclusao());
        tarefaDto.setIsVisivel(tarefa.getIsVisivel());
        
        if (tarefa.getTarefaPai() != null) {
            tarefaDto.setIdTarefaPai(tarefa.getTarefaPai().getId());
        } else {
            throw new UnprocessableEntityException("Tarefa não é uma subtarefa");
        }

        if (tarefa.getBucket() != null) {
            tarefaDto.setIdBucket(tarefa.getBucket().getId());
        } else {
            tarefaDto.setIdBucket(null);
        }

        List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(tarefa.getId());
        tarefaDto.setResponsaveis(executores);

        List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(tarefa.getId());
        tarefaDto.setComentarios(comentarios);

        return tarefaDto;
    }

    public List<TarefaRespostaDto> exibirSubtarefaPorIdTarefa(Integer idTarefa) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));
        List<Tarefa> tarefas = tarefaRepository.findAllByTarefaPai(tarefa);

        if (tarefas.isEmpty()) {
            throw new NoContentException("Não há subtarefas cadastradas");
        }

        List<TarefaRespostaDto> tarefasDto = new ArrayList<>();
        for (Tarefa subtarefa : tarefas) {
            TarefaRespostaDto tarefaDto = new TarefaRespostaDto();

            tarefaDto.setId(subtarefa.getId());
            tarefaDto.setNome(subtarefa.getNome());
            tarefaDto.setDescricao(subtarefa.getDescricao());
            tarefaDto.setFibonacci(subtarefa.getFibonacci());
            tarefaDto.setStatus(subtarefa.getStatus());
            tarefaDto.setHorasEstimada(subtarefa.getHorasEstimada());
            tarefaDto.setDataEstimada(subtarefa.getDataEstimada());
            tarefaDto.setDataCriacao(subtarefa.getDataCriacao());
            tarefaDto.setDataConclusao(subtarefa.getDataConclusao());
            tarefaDto.setIsVisivel(subtarefa.getIsVisivel());

            if (tarefaDto.getIsVisivel() == false) {
                continue;
            }
            
            if (subtarefa.getTarefaPai() != null) {
                tarefaDto.setIdTarefaPai(subtarefa.getTarefaPai().getId());
            } else {
                tarefaDto.setIdTarefaPai(null);
            }

            if (subtarefa.getBucket() != null) {
                tarefaDto.setIdBucket(subtarefa.getBucket().getId());
            } else {
                tarefaDto.setIdBucket(null);
            }

            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(subtarefa.getId());
            tarefaDto.setResponsaveis(executores);

            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(subtarefa.getId());
            tarefaDto.setComentarios(comentarios);

            tarefasDto.add(tarefaDto);
        }

        if (tarefasDto.isEmpty()) {
            throw new NoContentException("Não há subtarefas cadastradas");
        }

        return tarefasDto;  
    }

    public List<TarefaRespostaDto> exibirTodasTarefasPorBucketId(Integer idBucket) {
        List<Tarefa> tarefas = tarefaRepository.findAllByBucketIdAndIsVisivelFalse(idBucket);

        if (tarefas.isEmpty()) {
            throw new NoContentException("Não há tarefas cadastradas");
        }

        List<TarefaRespostaDto> tarefasDto = new ArrayList<>();
        for (Tarefa tarefa : tarefas) {
            TarefaRespostaDto tarefaDto = new TarefaRespostaDto();

            tarefaDto.setId(tarefa.getId());
            tarefaDto.setNome(tarefa.getNome());
            tarefaDto.setDescricao(tarefa.getDescricao());
            tarefaDto.setFibonacci(tarefa.getFibonacci());
            tarefaDto.setStatus(tarefa.getStatus());
            tarefaDto.setHorasEstimada(tarefa.getHorasEstimada());
            tarefaDto.setDataEstimada(tarefa.getDataEstimada());
            tarefaDto.setDataCriacao(tarefa.getDataCriacao());
            tarefaDto.setDataConclusao(tarefa.getDataConclusao());
            tarefaDto.setIsVisivel(tarefa.getIsVisivel());
            
            if (tarefa.getTarefaPai() != null) {
                tarefaDto.setIdTarefaPai(tarefa.getTarefaPai().getId());
            } else {
                tarefaDto.setIdTarefaPai(null);
            }

            if (tarefa.getBucket() != null) {
                tarefaDto.setIdBucket(tarefa.getBucket().getId());
            } else {
                tarefaDto.setIdBucket(null);
            }

            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(tarefa.getId());
            tarefaDto.setResponsaveis(executores);

            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(tarefa.getId());
            tarefaDto.setComentarios(comentarios);

            tarefasDto.add(tarefaDto);
        }

        return tarefasDto;
    }

    public List<ExecutorDto> exibirTodosExecutoresPorTarefaId(Integer idTarefa) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));

        List<ExecutorTarefa> executores = executorTarefaRepository.findAllByTarefa(tarefa);

        List<ExecutorDto> executoresDto = new ArrayList<>();
        for (ExecutorTarefa executor : executores) {
            ExecutorDto executorDto = new ExecutorDto(); 
            
            if (executor.getFuncionario() != null) {
                executorDto.setNome(executor.getFuncionario().getNome());
                executorDto.setUrlFoto(executor.getFuncionario().getImagem());
                executorDto.setTempoExecutado(executor.getTempoExecutado());
                executorDto.setIdFuncionario(idTarefa);
            } else {
                executorDto.setNome(executor.getUsuario().getNome());
                executorDto.setUrlFoto(executor.getUsuario().getImagem());
                executorDto.setTempoExecutado(executor.getTempoExecutado());
                executorDto.setIdUsuario(executor.getUsuario().getId());
            }

            executoresDto.add(executorDto);
        }

        return executoresDto;
    }

    public List<ComentarioRespostaDto> exibirTodosComentariosPorTarefaId(Integer idTarefa) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));

        List<ComentarioRespostaDto> comentariosDto = new ArrayList<>();
        List<Comentario> comentarios = comentarioRepository.findAllByTarefaAndIsVisivel(tarefa, true);

        for (Comentario comentario : comentarios) {
            ComentarioRespostaDto comentarioDto = new ComentarioRespostaDto();

            comentarioDto.setId(comentario.getId());
            comentarioDto.setMensagem(comentario.getMensagem());
            comentarioDto.setDataCriacao(comentario.getDataCriacao());
            comentarioDto.setIsVisivel(comentario.getIsVisivel());

            ComentarioRespostaDto.Remetente remetente = comentarioDto.new Remetente();
            if (comentario.getFuncionario() != null) {
                remetente.setId(comentario.getFuncionario().getId());
                remetente.setNome(comentario.getFuncionario().getNome());
                remetente.setFoto(comentario.getFuncionario().getImagem());
                remetente.setIsFuncionario(true);
            } else {
                remetente.setId(comentario.getUsuario().getId());
                remetente.setNome(comentario.getUsuario().getNome());
                remetente.setFoto(comentario.getUsuario().getImagem());
                remetente.setIsFuncionario(false);
            }

            comentarioDto.setRemetente(remetente);

            comentariosDto.add(comentarioDto);
        }

        return comentariosDto;
    }

    public List<TarefaSecaoDto> exibirTodasTarefasPorSecao(Integer idBuffet, Integer idEvento) {
        Query query = entityManager.createNativeQuery("SELECT * FROM vw_eventos_por_secao WHERE id_buffet = :idBuffet AND id_evento = :idEvento AND is_visivel = 1");
        query.setParameter("idBuffet", idBuffet);
        query.setParameter("idEvento", idEvento);
        List<Object[]> tarefas = query.getResultList();

        List<TarefaSecaoDto> tarefasDto = new ArrayList<>();
        for (Object[] tarefa : tarefas) {
            Integer idBuffetDto = (Integer) tarefa[0];
            Integer idBuffetServico = (Integer) tarefa[1];
            Integer idEventoDto = (Integer) tarefa[2];
            Integer id = (Integer) tarefa[3];
            String nome = (String) tarefa[4];
            String descricao = (String) tarefa[5];
            Integer fibonacci = (Integer) tarefa[6];
            Integer status = (Integer) tarefa[7];
            Integer horasEstimada = (Integer) tarefa[8];
            Date dataEstimada = (Date) tarefa[9];
            Timestamp dataCriacao = (Timestamp) tarefa[10];
            Timestamp dataConclusao = (Timestamp) tarefa[11];
            Byte isVisivel = (Byte) tarefa[12];
            Integer idTarefa = (Integer) tarefa[13];
            Integer idBucket = (Integer) tarefa[14];

            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(id);
            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(id);

            tarefasDto.add(new TarefaSecaoDto(idBuffetDto, idBuffetServico, idEventoDto, id, nome, descricao, fibonacci, status, horasEstimada, dataEstimada, dataCriacao, dataConclusao, isVisivel, idTarefa, idBucket, comentarios, executores));
        }

        if (tarefasDto.isEmpty()) {
            throw new NoContentException("Não há tarefas cadastradas");
        }

        tarefasDto.stream().forEach(task -> {
            List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(task.getId());
            task.setResponsaveis(executores);
        });

        tarefasDto.stream().forEach(task -> {
            List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(task.getId());
            task.setComentarios(comentarios);
        });

        return tarefasDto;
    }

    public SecaoBucketDto exibirTodasTarefasPorSecaoIndividual(Integer idBuffet, Integer idEvento, Integer idSecao) {
        Query querySecao = entityManager.createNativeQuery("SELECT bs.id, s.descricao FROM buffet_servico bs JOIN servico s ON s.id = bs.id_servico WHERE bs.id_buffet = :idBuffet AND bs.id = :idSecao ");
        querySecao.setParameter("idBuffet", idBuffet);
        querySecao.setParameter("idSecao", idSecao);
        Object[] secao = (Object[]) querySecao.getSingleResult();
        Integer secaoId = (Integer) secao[0];
        String secaoNome = (String) secao[1];

        SecaoBucketDto secaoDto = new SecaoBucketDto();
        secaoDto.setIdSecao(secaoId);
        secaoDto.setNomeSecao(secaoNome);
        secaoDto.setBuckets(new ArrayList<>());

        Query queryBuckets = entityManager.createNativeQuery("SELECT bck.id, bck.nome FROM bucket bck WHERE bck.id_buffet_servico = :secaoId AND bck.id_evento = :idEvento AND is_visivel = 1");
        queryBuckets.setParameter("secaoId", secaoId);
        queryBuckets.setParameter("idEvento", idEvento);

        
        List<Object[]> buckets = queryBuckets.getResultList();
        for (Object[] bucket : buckets) {
            Integer idBucket = (Integer) bucket[0];
            String nomeBucket = (String) bucket[1];

            Query queryTarefas = entityManager.createNativeQuery("SELECT * FROM vw_eventos_por_secao WHERE id_buffet = :idBuffet AND "
            + "id_evento = :idEvento AND id_buffet_servico = :idSecao AND id_bucket = :idBucket AND is_visivel = 1 AND id_tarefa IS NULL");
            queryTarefas.setParameter("idBuffet", idBuffet);
            queryTarefas.setParameter("idEvento", idEvento);
            queryTarefas.setParameter("idSecao", idSecao);
            queryTarefas.setParameter("idBucket", idBucket);
            List<Object[]> tarefas = queryTarefas.getResultList();

            List<TarefaDto> tarefasDto = new ArrayList<>();
            for (Object[] tarefa : tarefas) {
                Integer id = (Integer) tarefa[3];
                String nome = (String) tarefa[4];
                String descricao = (String) tarefa[5];
                Integer fibonacci = (Integer) tarefa[6];
                Integer status = (Integer) tarefa[7];
                Integer horasEstimada = (Integer) tarefa[8];

                Date dataEstimada = (Date) tarefa[9];

                LocalDate dataEstimadaLocalDate = null;
                if (dataEstimada != null) {
                    dataEstimadaLocalDate = dataEstimada.toLocalDate();
                }
                
                Timestamp dataCriacao = (Timestamp) tarefa[10];

                LocalDate dataCriacaoLocalDate = null;
                if (dataCriacao != null) {
                    dataCriacaoLocalDate = dataCriacao.toLocalDateTime().toLocalDate();
                }

                Timestamp dataConclusao = (Timestamp) tarefa[11];

                LocalDateTime dataConclusaoLocalDateTime = null;
                if (dataConclusao != null) {
                    dataConclusaoLocalDateTime = dataConclusao.toLocalDateTime();
                }

                Byte isVisivel = (Byte) tarefa[12];
                Boolean isVisivelBoolean = isVisivel == 1 ? true : false;

                Integer idTarefa = (Integer) tarefa[13];
                Integer idBucketDto = (Integer) tarefa[14];
                Integer idServico = (Integer) tarefa[15];

                List<ComentarioRespostaDto> comentarios = exibirTodosComentariosPorTarefaId(id);
                List<ExecutorDto> executores = exibirTodosExecutoresPorTarefaId(id);

                tarefasDto.add(new TarefaDto(id, nome, descricao, fibonacci, status, horasEstimada, dataEstimadaLocalDate, dataConclusaoLocalDateTime, dataCriacaoLocalDate, isVisivelBoolean, idTarefa, idBucketDto, idServico, comentarios, executores));
            }

            BucketTarefaDto bucketDto = new BucketTarefaDto();
            bucketDto.setIdBucket(idBucket);
            bucketDto.setNomeBucket(nomeBucket);
            bucketDto.setTarefas(tarefasDto);

            secaoDto.getBuckets().add(bucketDto);
        }

        return secaoDto;
    }

    public TarefaDto criarTarefa(TarefaDto tarefaCriacao) {
        Tarefa tarefa = new Tarefa();

        if (tarefaCriacao.getIdTarefaPai() != null) {
            if (tarefaRepository.findById(tarefaCriacao.getIdTarefaPai()).isEmpty()) {
                throw new NotFoundException("Tarefa pai não encontrada");
            } else {
                Tarefa tarefaPai = tarefaRepository.findById(tarefaCriacao.getIdTarefaPai()).get();
                tarefa.setTarefaPai(tarefaPai);
            }
        } else {
            tarefa.setTarefaPai(null);
        }

        tarefa.setNome(tarefaCriacao.getNome());
        tarefa.setDescricao(tarefaCriacao.getDescricao());
        tarefa.setFibonacci(tarefaCriacao.getFibonacci());
        tarefa.setStatus(tarefaCriacao.getStatus());
        tarefa.setHorasEstimada(tarefaCriacao.getHorasEstimada());
        tarefa.setDataEstimada(tarefaCriacao.getDataEstimada());
        tarefa.setDataCriacao(tarefaCriacao.getDataCriacao());
        tarefa.setDataConclusao(tarefaCriacao.getDataConclusao());
        tarefa.setIsVisivel(tarefaCriacao.getIsVisivel());

        if (tarefaCriacao.getIdBucket() != null) {
            tarefa.setBucket(bucketRepository.findById(tarefaCriacao.getIdBucket()).orElseThrow(() -> new NotFoundException("Bucket não encontrado")));
        } else {
            if (tarefa.getTarefaPai() != null) {
                tarefa.setBucket(tarefa.getTarefaPai().getBucket());
            } else {
                throw new UnprocessableEntityException("Impossível encontrar bucket. Tarefa pai e bucket não informados");
            }
        }

        tarefaRepository.save(tarefa);

        TarefaDto tarefaDto = TarefaMapper.toDto(tarefa);

        return tarefaDto;
    }

    public TarefaDto atualizarTarefa(Integer id, TarefaDto tarefaAtualizada) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));

        tarefa.setNome(tarefaAtualizada.getNome());
        tarefa.setDescricao(tarefaAtualizada.getDescricao());
        tarefa.setFibonacci(tarefaAtualizada.getFibonacci());
        tarefa.setStatus(tarefaAtualizada.getStatus());
        tarefa.setHorasEstimada(tarefaAtualizada.getHorasEstimada());
        tarefa.setDataEstimada(tarefaAtualizada.getDataEstimada());
        tarefa.setDataCriacao(tarefaAtualizada.getDataCriacao());
        tarefa.setDataConclusao(tarefaAtualizada.getDataConclusao());
        tarefa.setIsVisivel(tarefaAtualizada.getIsVisivel());

        if (tarefaAtualizada.getIdTarefaPai() != null) {
            Tarefa tarefaPai = tarefaRepository.findById(tarefaAtualizada.getIdTarefaPai()).orElseThrow(() -> new NotFoundException("Tarefa pai não encontrada"));
            tarefa.setTarefaPai(tarefaPai);
        } else {
            tarefa.setTarefaPai(null);
        }

        if (tarefaAtualizada.getIdBucket() != null) {
            tarefa.setBucket(bucketRepository.findById(tarefaAtualizada.getIdBucket()).orElseThrow(() -> new NotFoundException("Bucket não encontrado")));
        } else {
            tarefa.setBucket(null);
        }

        if (tarefa.getDataConclusao() != null) {
            tarefa.setStatus(3);
            tarefa.setIsVisivel(false);
        }

        tarefaRepository.save(tarefa);

        TarefaDto tarefaDto = TarefaMapper.toDto(tarefa);

        return tarefaDto;
    }

    public void deletarTarefa(Integer id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NoContentException("Tarefa não encontrada"));
        List<Tarefa> subtarefas = tarefaRepository.findAllByTarefaPai(tarefa);

        for (Tarefa subtarefa : subtarefas) {
            subtarefa.setIsVisivel(false);
            tarefaRepository.save(subtarefa);
        }

        tarefa.setIsVisivel(false);

        tarefaRepository.save(tarefa);
    }

    public List<SecaoDto> exibirDadosDaSecao(Integer idBuffet, Integer idEvento) {
        Query query = entityManager.createNativeQuery("SELECT DISTINCT nome_servico, id_servico  FROM vw_secoes WHERE id_buffet = :idBuffet AND id_evento = :idEvento");
        query.setParameter("idBuffet", idBuffet);
        query.setParameter("idEvento", idEvento);
        List<Object[]> secoes = query.getResultList();

        List<SecaoDto> secoesDto = new ArrayList<>();
        for (Object[] secao : secoes) {
            Integer idServico = (Integer) secao[1];
            String nomeServico = (String) secao[0];
           
            secoesDto.add(new SecaoDto(idServico, nomeServico));
        }

        if (secoesDto.isEmpty()) {
            throw new NoContentException("Não há seções disponíveis");
        }

        return secoesDto;
    }
}