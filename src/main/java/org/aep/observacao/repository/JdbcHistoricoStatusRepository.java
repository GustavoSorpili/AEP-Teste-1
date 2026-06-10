package org.aep.observacao.repository;

import org.aep.observacao.model.HistoricoStatus;
import org.aep.observacao.model.Status;
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

@Repository
public class JdbcHistoricoStatusRepository implements HistoricoStatusRepository {

    private final Map<Integer, HistoricoStatus> cache = new LinkedHashMap<>();

    public JdbcHistoricoStatusRepository() {
        DatabaseManager.initializeDatabase();
        carregarCacheDoBanco();
    }

    @Override
    public List<HistoricoStatus> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<HistoricoStatus> findBySolicitacaoId(int solicitacaoId) {
        List<HistoricoStatus> historicoStatus = new ArrayList<>();
        for (HistoricoStatus historico : cache.values()) {
            if (historico.getSolicitacaoId() == solicitacaoId) {
                historicoStatus.add(historico);
            }
        }
        return historicoStatus;
    }

    @Override
    public HistoricoStatus save(HistoricoStatus historicoStatus) {
        String sql = "INSERT INTO historico_status (id, solicitacao_id, status, data, responsavel, comentario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, historicoStatus.getId());
            statement.setInt(2, historicoStatus.getSolicitacaoId());
            statement.setString(3, historicoStatus.getStatus().name());
            statement.setString(4, historicoStatus.getData().toString());
            statement.setString(5, historicoStatus.getResponsavel());
            statement.setString(6, historicoStatus.getComentario());
            statement.executeUpdate();
            cache.put(historicoStatus.getId(), historicoStatus);
            return historicoStatus;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir histórico no banco de dados", e);
        }
    }

    private void carregarCacheDoBanco() {
        String sql = "SELECT * FROM historico_status ORDER BY id";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                HistoricoStatus historicoStatus = mapHistorico(resultSet);
                cache.put(historicoStatus.getId(), historicoStatus);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar histórico do banco de dados", e);
        }
    }

    private HistoricoStatus mapHistorico(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int solicitacaoId = resultSet.getInt("solicitacao_id");
        Status status = Status.valueOf(resultSet.getString("status"));
        LocalDateTime data = LocalDateTime.parse(resultSet.getString("data"));
        String responsavel = resultSet.getString("responsavel");
        String comentario = resultSet.getString("comentario");
        return new HistoricoStatus(id, solicitacaoId, status, data, responsavel, comentario);
    }
}