package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private String idioma;
    private Integer numeroDownload;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private List<Autor> autores = new ArrayList<>();

    public Livro () {}

    public Livro(DadosLivro dadosLivro) {
        this.titulo = dadosLivro.titulo();
        this.numeroDownload = dadosLivro.numeroDownload();
        this.autores = getAutores();

    // Copia autores para a lista
    if (dadosLivro.autores() != null) {
        this.autores = dadosLivro.autores().stream()
                .map(autor -> new Autor(new DadosAutor(autor.nome(), autor.anoNascimento(), autor.anoFalecimento())))
                .toList();
    }
        this.idioma = dadosLivro.idioma() != null ? String.join(", ", dadosLivro.idioma()) : "Desconhecido";

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDownload() {
        return numeroDownload;
    }

    public void setNumeroDownload(Integer numeroDownload) {
        this.numeroDownload = numeroDownload;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores != null ? autores : new ArrayList<>();
    }
}
