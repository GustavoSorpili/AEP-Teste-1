package org.aep.observacao.controller;

import org.aep.observacao.model.Categoria;
import org.aep.observacao.model.Prioridade;
import org.aep.observacao.model.Solicitacao;
import org.aep.observacao.model.Status;
import org.aep.observacao.model.Usuario;
import org.aep.observacao.service.ServicoSolicitacoes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SolicitacaoController.class)
public class SolicitacaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicoSolicitacoes servico;

    @Test
    public void criarSolicitacao_deveRetornar201() throws Exception {
        Categoria cat = new Categoria("Teste", 5);
        Solicitacao sol = new Solicitacao(1, "SOL-000001", cat, "Descrição", "Local", Prioridade.ALTA, Status.ABERTO, LocalDateTime.now(), null, true);
        when(servico.criarSolicitacao(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(sol);

        String json = "{\"categoriaNome\":\"Teste\",\"categoriaSlaDias\":5,\"descricao\":\"Descrição\",\"localizacao\":\"Local\",\"prioridade\":\"ALTA\",\"anonimo\":true}";

        mvc.perform(post("/api/solicitacoes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.protocolo").value("SOL-000001"));
    }

    @Test
    public void buscarPorProtocolo_deveRetornar200() throws Exception {
        Categoria cat = new Categoria("Teste", 5);
        Solicitacao sol = new Solicitacao(1, "SOL-000001", cat, "Descrição", "Local", Prioridade.ALTA, Status.ABERTO, LocalDateTime.now(), null, true);
        when(servico.buscarPorProtocolo(eq("SOL-000001"))).thenReturn(sol);

        mvc.perform(get("/api/solicitacoes/SOL-000001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value("SOL-000001"))
                .andExpect(jsonPath("$.categoria").value("Teste"));
    }

    @Test
    public void atualizarStatus_deveRetornarNoContent() throws Exception {
        when(servico.atualizarStatus(eq(1), any(), any(), any())).thenReturn(true);

        String json = "{\"status\":\"TRIAGEM\",\"responsavel\":\"Teste\",\"comentario\":\"ok\"}";

        mvc.perform(patch("/api/solicitacoes/1/status").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNoContent());
    }
}
