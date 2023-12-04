package eventify.api_spring.api.controller.smartsync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eventify.api_spring.dto.smartsync.dashboard.EventoProximoDto;
import eventify.api_spring.dto.smartsync.dashboard.KanbanStatusDto;
import eventify.api_spring.service.smartsync.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import static org.springframework.http.ResponseEntity.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = {"Access-Control-Expose-Headers", "Access-Token", "Uid"})
@SecurityRequirement(name = "requiredAuth")
@RestController
@RequestMapping("/dashboards")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/listagem-proximos-eventos/{idBuffet}")
    public ResponseEntity<List<EventoProximoDto>> retornarListagemProximosEventos(@PathVariable Integer idBuffet) {
        List<EventoProximoDto> eventos = dashboardService.retornarListagemProximosEventos(idBuffet);

        return ok(eventos);
    }

    @GetMapping("/listagem-dados-proximos-eventos/{idBuffet}")
    public ResponseEntity<List<KanbanStatusDto>> retornarListagemDadosProximosEventos(@PathVariable Integer idBuffet) {
        List<KanbanStatusDto> kanbans = dashboardService.retornarListagemDadosProximosEventos(idBuffet);

        return ok(kanbans);
    }
}