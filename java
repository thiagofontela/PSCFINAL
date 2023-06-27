import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class VagaDeEmprego {
    private String titulo;
    private String formacaoMinima;
    private int tempoExperienciaMinimo;
    private String localidade;

    public VagaDeEmprego(String titulo, String formacaoMinima, int tempoExperienciaMinimo, String localidade) {
        this.titulo = titulo;
        this.formacaoMinima = formacaoMinima;
        this.tempoExperienciaMinimo = tempoExperienciaMinimo;
        this.localidade = localidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFormacaoMinima() {
        return formacaoMinima;
    }

    public int getTempoExperienciaMinimo() {
        return tempoExperienciaMinimo;
    }

    public String getLocalidade() {
        return localidade;
    }
}

class Candidato {
    private String nome;
    private String cpf;
    private int idade;
    private String formacao;
    private int tempoExperiencia;
    private String localidade;

    public Candidato(String nome, String cpf, int idade, String formacao, int tempoExperiencia, String localidade) {
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
        this.formacao = formacao;
        this.tempoExperiencia = tempoExperiencia;
        this.localidade = localidade;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public int getIdade() {
        return idade;
    }

    public String getFormacao() {
        return formacao;
    }

    public int getTempoExperiencia() {
        return tempoExperiencia;
    }

    public String getLocalidade() {
        return localidade;
    }
}

class VagasEmpregoDatabase {
    private Connection connection;

    public VagasEmpregoDatabase() {
        try {
            // Estabelece a conexão com o banco de dados MySQL
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pscthiagofontela", "PscProvafinal2023Th", "PSCprova2023Th05");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void criarVagaDeEmprego(String titulo, String formacaoMinima, int tempoExperienciaMinimo, String localidade) {
        try {
            // Insere a nova vaga de emprego no banco de dados
            String sql = "INSERT INTO vagas_emprego (titulo, formacao_minima, tempo_experiencia_minimo, localidade) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, titulo);
            statement.setString(2, formacaoMinima);
            statement.setInt(3, tempoExperienciaMinimo);
            statement.setString(4, localidade);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao criar a vaga de emprego: " + e.getMessage());
        }
    }

    public void cadastrarCandidato(String nome, String cpf, int idade, String formacao, int tempoExperiencia, String localidade) {
        try {
            // Insere o novo candidato no banco de dados
            String sql = "INSERT INTO candidatos (nome, cpf, idade, formacao, tempo_experiencia, localidade) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setString(2, cpf);
            statement.setInt(3, idade);
            statement.setString(4, formacao);
            statement.setInt(5, tempoExperiencia);
            statement.setString(6, localidade);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar o candidato: " + e.getMessage());
        }
    }

    public List<Candidato> classificarCandidatos(VagaDeEmprego vaga) {
        List<Candidato> candidatosClassificados = new ArrayList<>();
        try {
            // Busca candidatos que atendam aos requisitos da vaga
            String sql = "SELECT * FROM candidatos " +
                    "WHERE formacao = ? AND tempo_experiencia >= ? AND localidade = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, vaga.getFormacaoMinima());
            statement.setInt(2, vaga.getTempoExperienciaMinimo());
            statement.setString(3, vaga.getLocalidade());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String cpf = resultSet.getString("cpf");
                int idade = resultSet.getInt("idade");
                String formacao = resultSet.getString("formacao");
                int tempoExperiencia = resultSet.getInt("tempo_experiencia");
                String localidade = resultSet.getString("localidade");
                Candidato candidato = new Candidato(nome, cpf, idade, formacao, tempoExperiencia, localidade);
                candidatosClassificados.add(candidato);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao classificar candidatos: " + e.getMessage());
        }
        return candidatosClassificados;
    }

    public void fecharConexao() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        VagasEmpregoDatabase database = new VagasEmpregoDatabase();

        // Criar vagas de emprego
        database.criarVagaDeEmprego("Desenvolvedor Java", "Bacharelado em Ciência da Computação", 2, "São Paulo");
        database.criarVagaDeEmprego("Analista de Dados", "Bacharelado em Estatística", 3, "Rio de Janeiro");

        // Cadastrar candidatos
        database.cadastrarCandidato("João", "111.111.111-11", 25, "Bacharelado em Ciência da Computação", 3, "São Paulo");
        database.cadastrarCandidato("Maria", "222.222.222-22", 28, "Bacharelado em Ciência da Computação", 1, "São Paulo");
        database.cadastrarCandidato("Pedro", "333.333.333-33", 30, "Bacharelado em Estatística", 5, "Rio de Janeiro");

        // Classificar candidatos para uma vaga específica
        VagaDeEmprego vaga = new VagaDeEmprego("Desenvolvedor Java", "Bacharelado em Ciência da Computação", 2, "São Paulo");
        List<Candidato> candidatosClassificados = database.classificarCandidatos(vaga);

        System.out.println("Candidatos classificados para a vaga de " + vaga.getTitulo() + ":");
        for (Candidato candidato : candidatosClassificados) {
            System.out.println("Nome: " + candidato.getNome());
            System.out.println("CPF: " + candidato.getCpf());
            System.out.println("Idade: " + candidato.getIdade());
            System.out.println("Formação: " + candidato.getFormacao());
            System.out.println("Experiência: " + candidato.getTempoExperiencia() + " anos");
            System.out.println("Localidade: " + candidato.getLocalidade());
            System.out.println("--------------------------");
        }

        // Fechar a conexão com o banco de dados
        database.fecharConexao();
    }
}
