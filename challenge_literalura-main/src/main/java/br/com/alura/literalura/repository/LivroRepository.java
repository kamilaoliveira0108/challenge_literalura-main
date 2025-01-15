package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query("SELECT l FROM Livro l WHERE LOWER(l.idioma) = LOWER(:idioma)")
    List<Livro> findByIdioma(@Param("idioma") String idioma);

    Livro findByTituloIgnoreCase(String titulo);
}
