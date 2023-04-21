package eventify.api_spring.controller;

import eventify.api_spring.domain.TipoEvento;
import eventify.api_spring.repository.TipoEventoRepository;
import eventify.api_spring.service.TipoEventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-eventos")
@CrossOrigin(origins = "http://localhost:3000")
public class TipoEventoController {


    @Autowired
    private TipoEventoService tipoEventoService;

    @PostMapping
    public ResponseEntity<TipoEvento> criarTipoEvento(@RequestBody @Valid TipoEvento t) {
        TipoEvento tipoEvento = this.tipoEventoService.criarTipoEvento(t);
        return ResponseEntity.status(201).body(t);
    }

    @GetMapping
    public ResponseEntity<List<TipoEvento>> exibirTipoEvento() {
         List<TipoEvento> tipoEventos = this.tipoEventoService.exibirTipoEvento();
        return ResponseEntity.status(200).body(tipoEventos);
    }
}
