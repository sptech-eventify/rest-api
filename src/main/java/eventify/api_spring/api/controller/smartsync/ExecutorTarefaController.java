package eventify.api_spring.api.controller.smartsync;

import eventify.api_spring.domain.smartsync.ExecutorTarefa;
import eventify.api_spring.domain.usuario.Funcionario;
import eventify.api_spring.domain.usuario.Usuario;
import eventify.api_spring.dto.smartsync.ExecutorDto;
import eventify.api_spring.dto.smartsync.ExecutorTarefaCriacaoDto;
import eventify.api_spring.exception.http.NotFoundException;
import eventify.api_spring.repository.FuncionarioRepository;
import eventify.api_spring.repository.UsuarioRepository;
import eventify.api_spring.service.smartsync.ExecutorTarefaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

import java.net.URI;
import java.util.List;

@SecurityRequirement(name = "requiredAuth")
@RestController
@RequestMapping("/executor-tarefas")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = {"Access-Control-Expose-Headers", "Access-Token", "Uid"})
public class ExecutorTarefaController {

    @Autowired
    private ExecutorTarefaService executorTarefaService;

    @GetMapping
    public ResponseEntity<List<ExecutorDto>> exibirTodosExecutoresTarefas() {
        List<ExecutorDto> executores = executorTarefaService.exibirTodosExecutoresTarefas();

        return ok(executores);
    }

    @GetMapping("/{idTarefa}")
    public ResponseEntity<List<ExecutorDto>> executoresPorIdTarefa(@PathVariable Integer idTarefa) {
        List<ExecutorDto> executores = executorTarefaService.executoresPorIdTarefa(idTarefa);

        return ok(executores);
    }

    @GetMapping("/executores-disponiveis/{idBuffet}")
    public ResponseEntity<List<ExecutorDto>> executoresDisponiveis(@PathVariable("idBuffet") Integer idBuffet) {
        List<ExecutorDto> executores = executorTarefaService.executoresDisponiveis(idBuffet);

        return ok(executores);
    }

    @PostMapping
    public ResponseEntity<ExecutorDto> adicionarExecutorTarefa(@RequestBody ExecutorTarefaCriacaoDto novoExecutor) {
        ExecutorDto executorTarefaSalvo = executorTarefaService.adicionarExecutorTarefa(novoExecutor);
        URI location = URI.create(String.format("/executor-tarefas/%d", executorTarefaSalvo.getId()));
        
        return created(location).body(executorTarefaSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExecutorTarefaCriacaoDto> atualizarExecutorTarefa(@PathVariable Integer id, @Valid @RequestBody ExecutorTarefaCriacaoDto executorTarefaAtualizado) {
        ExecutorTarefaCriacaoDto executorTarefaAtualizadoSalvo = executorTarefaService.atualizarExecutorTarefa(id, executorTarefaAtualizado);

        return ok(executorTarefaAtualizadoSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerExecutorTarefa(@PathVariable Integer id) {
        executorTarefaService.removerExecutorTarefa(id);

        return noContent().build();
    }
}