package eventify.api_spring.api.controller.agenda;

import eventify.api_spring.domain.agenda.Agenda;
import eventify.api_spring.dto.agenda.AgendaCriacaoDto;
import eventify.api_spring.dto.agenda.AgendaDto;
import eventify.api_spring.service.agenda.AgendaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

import java.net.URI;
import java.util.List;

@SecurityRequirement(name = "requiredAuth")
@RestController
@RequestMapping("/agendas")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendaDto>> listarAgendas() {
        List<AgendaDto> agenda = agendaService.listarAgendas();

        return ok(agenda);
    }

    @PostMapping
    public ResponseEntity<Agenda> criarAgenda(@RequestBody AgendaCriacaoDto agenda) {
        Agenda agendaSalva = agendaService.criarAgenda(agenda);
        URI location = URI.create("/api/agendas/" + agendaSalva.getId());
        
        return created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaDto> buscarAgendaPorId(@PathVariable Integer id) {
        AgendaDto agenda = agendaService.buscarAgendaPorId(id);

        return ok(agenda);
    }

    @GetMapping("/buffet/{idBuffet}")
    public ResponseEntity<List<AgendaDto>> buscarAgendaPorBuffet(@PathVariable Integer idBuffet) {
        List<AgendaDto> agendas = agendaService.buscarAgendaPorBuffet(idBuffet);

        return ok(agendas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgenda(@PathVariable Integer id) {
        agendaService.deletarAgenda(id);

        return noContent().build();
    }

}