package eventify.api_spring.dto.smartsync.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContratanteKpi {
    private Integer semContratos;
    private Integer umContrato;
    private Integer doisContratosOuMais;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class ContratanteKpiData {
        private Integer idUsuario;
        private String nome;
        private Integer quantidadeEventos;
    }
}
