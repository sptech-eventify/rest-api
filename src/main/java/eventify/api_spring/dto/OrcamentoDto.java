package eventify.api_spring.dto;

import java.time.LocalDate;
import java.util.List;

public record OrcamentoDto(String nome, String nomeContratante, String endereco, LocalDate data, Object tiposServicos, Object tiposFaixasEtarias, Object tipoEventos, Double preco) {
    public OrcamentoDto {
    }
}