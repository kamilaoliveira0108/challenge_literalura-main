package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.*;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi api = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private static final String ENDERECO = "https://gutendex.com/books/?search=";

    @Autowired
    private LivroRepository livroRepositorio;

    @Autowired
    private AutorRepository autorRepositorio;

    public void exibeMenu() {
        int opcao;
        do {
            String menu = """
                    \n******************* MENU ********************
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    6 - Gerar estatísticas de downloads de livros
                    7 - Top 10 livros mais baixados
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> pesquisarPorTitulo();
                case 2 -> pesquisarLivrosRegistrados();
                case 3 -> pesquisarPorAutores();
                case 4 -> pesquisarAutoresVivosPorAno();
                case 5 -> pesquisarLivrosPorIdioma();
                case 6 -> gerarEstatisticasDownloads();
                case 7 -> top10LivrosMaisBaixados();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida");
            }
        } while (opcao != 0);
    }

    private void gerarEstatisticasDownloads() {
        List<Livro> livros = livroRepositorio.findAll();

        DoubleSummaryStatistics estatisticas = livros.stream()
                .mapToDouble(Livro::getNumeroDownload)  // Obtém o número de downloads de cada livro
                .summaryStatistics();  // Gera as estatísticas

        System.out.println("Estatísticas de Downloads dos Livros:");
        System.out.println("Total de Downloads: " + estatisticas.getSum());
        System.out.println("Média de Downloads: " + estatisticas.getAverage());
        System.out.println("Download Mínimo: " + estatisticas.getMin());
        System.out.println("Download Máximo: " + estatisticas.getMax());
    }

    private void top10LivrosMaisBaixados() {
        List<Livro> livros = livroRepositorio.findAll();

        List<Livro> top10Livros = livros.stream()
                .sorted(Comparator.comparingInt(Livro::getNumeroDownload).reversed())  // Ordena pelos downloads (decrescente)
                .limit(10)  // Pega os top 10
                .collect(Collectors.toList());

        System.out.println("Top 10 Livros Mais Baixados:");
        top10Livros.forEach(livro -> {
            System.out.println("Título: " + livro.getTitulo());
            System.out.println("Número de Downloads: " + livro.getNumeroDownload());
            System.out.println("-------------------------------------");
        });
    }

    private DadosLivro getDadosLivro() {
        System.out.println("Digite o livro que deseja encontrar: ");
        String nomeLivro = leitura.nextLine().trim();
        String endereco = ENDERECO + nomeLivro.replace(" ", "%20");
        String json = api.obterDadosLivros(endereco);

        if (json == null || json.isEmpty()) {
            System.out.println("Nenhum dado encontrado para o livro pesquisado.");
            return null;
        }

        Gutendex gutendex = conversor.obterDados(json, Gutendex.class);
        if (gutendex == null || gutendex.results().isEmpty()) {
            System.out.println("Nenhum dado encontrado para o livro pesquisado.");
            return null;
        }

        return gutendex.results().get(0); // Pega o primeiro livro
    }

    private void pesquisarPorTitulo() {
        DadosLivro dadosLivro = getDadosLivro();
        if (dadosLivro == null) return;

        // Verifica se o livro já existe no banco pelo título
        Livro livroExistente = livroRepositorio.findByTituloIgnoreCase(dadosLivro.titulo());
        if (livroExistente != null) {
            System.out.println("O livro \"" + livroExistente.getTitulo() + "\" já está cadastrado.");
            return;
        }

        List<Autor> autores = dadosLivro.autores().stream()
                .map(autorDados -> {
                    Autor autorExistente = autorRepositorio.findByNomeIgnoreCase(autorDados.nome());
                    return autorExistente != null ? autorExistente : autorRepositorio.save(new Autor(autorDados));
                })
                .collect(Collectors.toList());

        Livro livro = new Livro(dadosLivro);
        livro.setAutores(autores);
        livroRepositorio.save(livro);

        System.out.println("Livro \"" + livro.getTitulo() + "\" salvo com sucesso!");
        System.out.println(dadosLivro.toString());
    }

    private void pesquisarLivrosRegistrados() {
        List<Livro> livros = livroRepositorio.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
        } else {
            livros.forEach(livro -> {
                System.out.println("--------------- Livro -----------------");
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("Autores: " + livro.getAutores().stream()
                        .map(Autor::getNome)
                        .collect(Collectors.joining(", ", "", "")));
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println("Número de Downloads: " + livro.getNumeroDownload());
                System.out.println("--------------------------------------");
            });
        }
    }

    @Transactional
    private void pesquisarPorAutores() {
        System.out.println("Digite o nome do autor (ou deixe em branco para listar todos): ");
        String autorBuscado = leitura.nextLine().trim();
        List<Autor> autores = autorBuscado.isEmpty() ?
                autorRepositorio.findAll() :
                autorRepositorio.findByNomeContainingIgnoreCase(autorBuscado);

        if (autores.isEmpty()) {
            System.out.println("Nenhum autor encontrado.");
        } else {
            autores.forEach(autor -> {
                System.out.println("--------------- Autor -----------------");
                System.out.println("Nome: " + autor.getNome());
                System.out.println("Ano de nascimento: " + autor.getAnoNascimento());
                System.out.println("Ano de falecimento: " + autor.getAnoFalecimento());
                System.out.println("Livros: " + (autor.getLivros().isEmpty() ? "Nenhum" :
                        autor.getLivros().stream().map(Livro::getTitulo).collect(Collectors.joining(", "))));
                System.out.println("--------------------------------------");
            });
        }
    }

    private void pesquisarLivrosPorIdioma() {
        System.out.println("Digite a sigla do idioma (ex: es, en, fr, pt): ");
        String idioma = leitura.nextLine().trim();
        List<Livro> livros = livroRepositorio.findByIdioma(idioma);

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado neste idioma.");
        } else {
            livros.forEach(livro -> {
                System.out.println("--------------- Livro -----------------");
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("Autores: " + livro.getAutores().stream()
                        .map(Autor::getNome)
                        .collect(Collectors.joining(", ")));
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println("Número de Downloads: " + livro.getNumeroDownload());
                System.out.println("--------------------------------------");
            });
        }
    }

    private void pesquisarAutoresVivosPorAno() {
        System.out.println("Digite o ano para pesquisa: ");
        int anoPesquisa = leitura.nextInt();
        leitura.nextLine();

        try {
            List<Autor> autores = autorRepositorio.findAll();
            List<Autor> autoresVivos = autores.stream()
                    .filter(autor -> {
                        Integer anoNascimento = autor.getAnoNascimento();
                        Integer anoFalecimento = autor.getAnoFalecimento();
                        return anoNascimento != null &&
                                anoNascimento <= anoPesquisa &&
                                (anoFalecimento == null || anoFalecimento > anoPesquisa);
                    })
                    .collect(Collectors.toList());

            if (autoresVivos.isEmpty()) {
                System.out.println("Nenhum autor vivo encontrado no ano " + anoPesquisa);
            } else {
                System.out.println("Autores vivos no ano " + anoPesquisa + ":");
                autoresVivos.forEach(autor -> {
                    System.out.println("Nome: " + autor.getNome());
                    System.out.println("Ano de nascimento: " + autor.getAnoNascimento());
                    System.out.println("Ano de falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "Ainda vivo"));
                });
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar autores: " + e.getMessage());
        }
    }
}
