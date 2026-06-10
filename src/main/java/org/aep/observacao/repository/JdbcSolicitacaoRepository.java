package org.aep.observacao.repository;

import org.aep.observacao.model.Categoria;
import org.aep.observacao.model.Prioridade;
import org.aep.observacao.model.Solicitacao;
import org.aep.observacao.model.Status;
import org.aep.observacao.model.Usuario;
import org.aep.observacao.service.DatabaseManager;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcSolicitacaoRepository implements SolicitacaoRepository {

    private final Map<Integer, Solicitacao> cache = new LinkedHashMap<>();

    public JdbcSolicitacaoRepository() {
        DatabaseManager.initializeDatabase();
        carregarCacheDoBanco();
    }

    @Override
    public List<Solicitacao> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Solicitacao> findById(int id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Optional<Solicitacao> findByProtocolo(String protocolo) {
        return cache.values().stream()
                .filter(solicitacao -> solicitacao.getProtocolo().equals(protocolo))
                .findFirst();
    }

    @Override
    public Solicitacao save(Solicitacao solicitacao) {
        String sql = "INSERT INTO solicitacao (id, protocolo, categoria, categoria_sla, descricao, localizacao, prioridade, status, data_criacao, usuario_id, usuario_nome, usuario_email, usuario_telefone, anonimo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preencherSolicitacaoStatement(statement, solicitacao);
            statement.executeUpdate();
            cache.put(solicitacao.getId(), solicitacao);
            return solicitacao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir solicitação no banco de dados", e);
        }
    }

    @Override
    public Solicitacao updateStatus(int id, String status) {
        String sql = "UPDATE solicitacao SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
            Solicitacao solicitacao = cache.get(id);
            if (solicitacao != null) {
                solicitacao.setStatus(Status.valueOf(status));
            }
            return solicitacao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status no banco de dados", e);
        }
    }

    private void carregarCacheDoBanco() {
        String sql = "SELECT * FROM solicitacao ORDER BY id";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Solicitacao solicitacao = mapSolicitacao(resultSet);
                cache.put(solicitacao.getId(), solicitacao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar solicitações do banco de dados", e);
        }
    }

    private void preencherSolicitacaoStatement(PreparedStatement statement, Solicitacao solicitacao) throws SQLException {
        statement.setInt(1, solicitacao.getId());
        statement.setString(2, solicitacao.getProtocolo());
        statement.setString(3, solicitacao.getCategoria().getNome());
        statement.setInt(4, solicitacao.getCategoria().getSlaDias());
        statement.setString(5, solicitacao.getDescricao());
        statement.setString(6, solicitacao.getLocalizacao());
        statement.setString(7, solicitacao.getPrioridade().name());
        statement.setString(8, solicitacao.getStatus().name());
        statement.setString(9, solicitacao.getDataCriacao().toString());
        if (solicitacao.getUsuario() != null) {
            statement.setInt(10, solicitacao.getUsuario().getId());
            statement.setString(11, solicitacao.getUsuario().getNome());
            statement.setString(12, solicitacao.getUsuario().getEmail());
            statement.setString(13, solicitacao.getUsuario().getTelefone());
        } else {
            statement.setNull(10, java.sql.Types.INTEGER);
            statement.setNull(11, java.sql.Types.VARCHAR);
            statement.setNull(12, java.sql.Types.VARCHAR);
            statement.setNull(13, java.sql.Types.VARCHAR);
        }
        statement.setBoolean(14, solicitacao.isAnonimo());
    }

    private Solicitacao mapSolicitacao(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String protocolo = resultSet.getString("protocolo");
        String categoriaNome = resultSet.getString("categoria");
        int categoriaSla = resultSet.getInt("categoria_sla");
        String descricao = resultSet.getString("descricao");
        String localizacao = resultSet.getString("localizacao");
        Prioridade prioridade = Prioridade.valueOf(resultSet.getString("prioridade"));
        Status status = Status.valueOf(resultSet.getString("status"));
        LocalDateTime dataCriacao = LocalDateTime.parse(resultSet.getString("data_criacao"));
        boolean anonimo = resultSet.getBoolean("anonimo");
        Usuario usuario = null;
        if (!anonimo) {
            int usuarioId = resultSet.getInt("usuario_id");
            String usuarioNome = resultSet.getString("usuario_nome");
            String usuarioEmail = resultSet.getString("usuario_email");
            String usuarioTelefone = resultSet.getString("usuario_telefone");
            usuario = new Usuario(usuarioId, usuarioNome, usuarioEmail, usuarioTelefone);
        }
        Categoria categoria = new Categoria(categoriaNome, categoriaSla);
        return new Solicitacao(id, protocolo, categoria, descricao, localizacao, prioridade, status, dataCriacao, usuario, anonimo);
    }
}