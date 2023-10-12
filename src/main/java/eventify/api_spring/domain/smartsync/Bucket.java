package eventify.api_spring.domain.smartsync;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Bucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 64)
    @NotBlank
    private String nome;

    @NotNull
    private Boolean isVisivel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "id_buffet_servico")
    private Integer idBuffetServico;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "id_evento")
    private Integer idEvento;
}