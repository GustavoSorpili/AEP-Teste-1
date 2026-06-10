package org.aep.observacao.model;

import java.time.LocalDateTime;

public class Solicitacao {
    private int id;
    private String protocolo;
    private Categoria categoria;
    private String descricao;
    private String localizacao; // text/bairro
    private Prioridade prioridade;
    private Status status;
    private LocalDateTime dataCriacao;
    private Usuario usuario; // null if anonymous
    private boolean anonimo;

    public Solicitacao(int id, String protocolo, Categoria categoria, String descricao, String localizacao, Prioridade prioridade, Status status, LocalDateTime dataCriacao, Usuario usuario, boolean anonimo) {
        this.id = id;
        this.protocolo = protocolo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.prioridade = prioridade;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.usuario = usuario;
        this.anonimo = anonimo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isAnonimo() {
        return anonimo;
    }

    // Setters for status update
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Protocolo: " + protocolo + "\nCategoria: " + categoria + "\nDescricao: " + descricao + "\nLocalizacao: " + localizacao + "\nPrioridade: " + prioridade + "\nStatus: " + status + "\nData: " + dataCriacao + "\nAnonimo: " + anonimo;
        if (!anonimo && usuario != null) {
            result += "\nSolicitante: " + usuario.getNome();
            result += "\nEmail: " + usuario.getEmail();
            result += "\nTelefone: " + usuario.getTelefone();
        }
        return result;
    }
}