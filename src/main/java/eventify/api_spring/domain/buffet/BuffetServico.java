package eventify.api_spring.domain.buffet;

import java.util.List;

import eventify.api_spring.domain.smartsync.Bucket;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "buffet_servico")
public class BuffetServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_buffet", referencedColumnName = "id")
    private Buffet buffet;

    @ManyToOne
    @JoinColumn(name = "id_servico", referencedColumnName = "id")
    private Servico servico;

    @OneToMany(mappedBy = "buffetServico", cascade = CascadeType.ALL)
    private List<Bucket> buckets;
}