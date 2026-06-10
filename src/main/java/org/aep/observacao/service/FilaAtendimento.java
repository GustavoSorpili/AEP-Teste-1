package org.aep.observacao.service;

import org.aep.observacao.model.*;
import java.util.List;

public class FilaAtendimento {
    private ServicoSolicitacoes servico;

    public FilaAtendimento(ServicoSolicitacoes servico) {
        this.servico = servico;
    }

    public List<Solicitacao> getFilaPorPrioridade(Prioridade prioridade) {
        return servico.listarSolicitacoes(prioridade, null, null);
    }

    public List<Solicitacao> getFilaPorBairro(String bairro) {
        return servico.listarSolicitacoes(null, bairro, null);
    }

    public List<Solicitacao> getFilaPorCategoria(Categoria categoria) {
        return servico.listarSolicitacoes(null, null, categoria);
    }
}