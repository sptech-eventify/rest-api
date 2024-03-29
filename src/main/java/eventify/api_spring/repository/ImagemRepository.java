package eventify.api_spring.repository;

import eventify.api_spring.domain.buffet.Buffet;
import eventify.api_spring.domain.buffet.Imagem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Integer> {

    @Query("select i from Imagem i where i.buffet = :buffet")
    public List<Imagem> findByBuffet(@Param("buffet") Buffet buffet);
}