package org.aep.observacao.repository;

import org.aep.observacao.model.Solicitacao;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository {
    List<Solicitacao> findAll();

    Optional<Solicitacao> findById(int id);

    Optional<Solicitacao> findByProtocolo(String protocolo);

    Solicitacao save(Solicitacao solicitacao);

    Solicitacao updateStatus(int id, String status);
}