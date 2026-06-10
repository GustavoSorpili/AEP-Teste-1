package org.aep.observacao.service;

import org.aep.observacao.model.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class ServicoSolicitacoesTest {

    @Test
    public void testCriarSolicitacao() {
        ServicoSolicitacoes servico = new ServicoSolicitacoes();
        Categoria categoria = new Categoria("Teste", 5);
        Solicitacao sol = servico.criarSolicitacao(categoria, "Descrição", "Local", Prioridade.ALTA, null, true);
        assertNotNull(sol);
        assertTrue(sol.getProtocolo().startsWith("SOL-"));
        assertTrue(sol.getProtocolo().matches("SOL-\\d{6}"));
        assertEquals(Status.ABERTO, sol.getStatus());
    }

    @Test
    public void testBuscarPorProtocolo() {
        ServicoSolicitacoes servico = new ServicoSolicitacoes();
        Categoria categoria = new Categoria("Teste", 5);
        Solicitacao sol = servico.criarSolicitacao(categoria, "Descrição", "Local", Prioridade.ALTA, null, true);
        Solicitacao found = servico.buscarPorProtocolo(sol.getProtocolo());
        assertEquals(sol, found);
    }

    @Test
    public void testAtualizarStatus() {
        ServicoSolicitacoes servico = new ServicoSolicitacoes();
        Categoria categoria = new Categoria("Teste", 5);
        Solicitacao sol = servico.criarSolicitacao(categoria, "Descrição", "Local", Prioridade.ALTA, null, true);
        boolean sucesso = servico.atualizarStatus(sol.getId(), Status.TRIAGEM, "Teste", "Comentário");
        assertTrue(sucesso);
        assertEquals(Status.TRIAGEM, sol.getStatus());
        List<HistoricoStatus> hist = servico.getHistorico(sol.getId());
        assertEquals(2, hist.size()); // initial + update
    }
}