package org.aep.observacao.repository;

import org.aep.observacao.model.HistoricoStatus;

import java.util.List;

public interface HistoricoStatusRepository {
    List<HistoricoStatus> findAll();

    List<HistoricoStatus> findBySolicitacaoId(int solicitacaoId);

    HistoricoStatus save(HistoricoStatus historicoStatus);
}