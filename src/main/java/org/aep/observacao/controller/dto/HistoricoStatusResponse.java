package org.aep.observacao.controller.dto;

import org.aep.observacao.model.HistoricoStatus;

public record HistoricoStatusResponse(
        int id,
        int solicitacaoId,
        String status,
        String data,
        String responsavel,
        String comentario) {

    public static HistoricoStatusResponse from(HistoricoStatus historicoStatus) {
        return new HistoricoStatusResponse(
                historicoStatus.getId(),
                historicoStatus.getSolicitacaoId(),
                historicoStatus.getStatus().name(),
                historicoStatus.getData().toString(),
                historicoStatus.getResponsavel(),
                historicoStatus.getComentario());
    }
}