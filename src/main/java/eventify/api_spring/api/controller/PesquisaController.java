package eventify.api_spring.api.controller;

import eventify.api_spring.api.assets.ListaBuffet;
import eventify.api_spring.domain.*;
import eventify.api_spring.dto.BuffetDtoResposta;
import eventify.api_spring.dto.usuario.BuffetDto;
import eventify.api_spring.service.BuffetService;
import eventify.api_spring.service.PesquisaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hibernate.Hibernate.get;

@RestController
@RequestMapping("/pesquisa")
@Tag(name="3. Pesquisa", description="Controller com os endpoints que controlam as pesquisas do sistema")
public class PesquisaController {
    @Autowired
    private PesquisaService pesquisaService;

    @GetMapping
    private ResponseEntity<List<BuffetDtoResposta>> buscador(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "faixaEtaria", required = false) List<String> faixaEtaria,
            @RequestParam(value = "tamanho", required = false) Integer tamanho,
            @RequestParam(value = "qtdPessoas", required = false) Integer qtdPessoas,
            @RequestParam(value = "tipoEvento", required = false) List<String> tipoEvento,
            @RequestParam(value = "orcMin", required = false) Double orcMin,
            @RequestParam(value = "orcMax", required = false) Double orcMax,
            @RequestParam(value = "dataEvento", required = false) LocalDate dataEvento,
            @RequestParam(value = "servico", required = false) List<String> servico,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {

        Pesquisa p = new Pesquisa(nome, faixaEtaria, tamanho, qtdPessoas, tipoEvento, orcMin, orcMax, dataEvento, servico, latitude, longitude);
        List<BuffetDtoResposta> listaFiltrada = pesquisaService.getBuffetPorPesquisa(p);

        if((listaFiltrada.size()) == 0){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(listaFiltrada);
    }
}
