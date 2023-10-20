package eventify.api_spring.dto.smartsync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDto {
    private Integer idBuffet;
    private Integer ano;
    private String mes;
    private Long qtdAvaliacao;
}