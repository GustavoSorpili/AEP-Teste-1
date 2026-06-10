package org.aep.observacao.model;

import java.time.LocalDateTime;

public class HistoricoStatus {
    private int id;
    private int solicitacaoId;
    private Status status;
    private LocalDateTime data;
    private String responsavel; // name of the person who changed
    private String comentario;

    public HistoricoStatus(int id, int solicitacaoId, Status status, LocalDateTime data, String responsavel, String comentario) {
        this.id = id;
        this.solicitacaoId = solicitacaoId;
        this.status = status;
        this.data = data;
        this.responsavel = responsavel;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public int getSolicitacaoId() {
        return solicitacaoId;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public String getComentario() {
        return comentario;
    }

    @Override
    public String toString() {
        return data + " - " + status + " by " + responsavel + ": " + comentario;
    }
}