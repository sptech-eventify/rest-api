package eventify.api_spring.api.controller.smartsync;

import eventify.api_spring.domain.smartsync.Comentario;
import eventify.api_spring.dto.smartsync.ComentarioCriacaoDto;
import eventify.api_spring.dto.smartsync.ComentarioRespostaDto;
import eventify.api_spring.service.smartsync.ComentarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

import java.net.URI;
import java.util.List;

@SecurityRequirement(name = "requiredAuth")
@RestController
@RequestMapping("/comentarios")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = {"Access-Control-Expose-Headers", "Access-Token", "Uid"})
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping("/tarefa/{id}")
    public ResponseEntity<List<ComentarioRespostaDto>> exibirComentarioPorIdTarefa(@PathVariable Integer id) {
        List<ComentarioRespostaDto> comentarios = comentarioService.exibirComentarioPorIdTarefa(id);

        return ok(comentarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioRespostaDto> exibirComentarioPorId(@PathVariable Integer id) {
        ComentarioRespostaDto comentario = comentarioService.exibirComentarioPorId(id);

        return ok(comentario);
    }

    @PostMapping
    public ResponseEntity<ComentarioRespostaDto> criarComentario(@RequestBody ComentarioCriacaoDto comentario) {
        ComentarioRespostaDto comentarioCriado = comentarioService.criarComentario(comentario);
        URI location = URI.create(String.format("/comentarios/%d", comentarioCriado.getId()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "Location");

        return created(location).body(comentarioCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioRespostaDto> atualizarComentario(@PathVariable Integer id, @RequestBody ComentarioCriacaoDto comentario) {
        ComentarioRespostaDto comentarioAtualizado = comentarioService.atualizarComentario(id, comentario);

        return ok(comentarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComentario(@PathVariable Integer id) {
        comentarioService.deletarComentario(id);

        return noContent().build();
    }
}
