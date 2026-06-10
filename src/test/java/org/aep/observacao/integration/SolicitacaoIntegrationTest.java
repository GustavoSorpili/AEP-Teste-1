package org.aep.observacao.integration;

import org.aep.observacao.controller.dto.HistoricoStatusResponse;
import org.aep.observacao.controller.dto.SolicitacaoResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SolicitacaoIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void criarAtualizarBuscarHistorico_flow() {
        Map<String, Object> req = new HashMap<>();
        req.put("categoriaNome", "Teste");
        req.put("categoriaSlaDias", 5);
        req.put("descricao", "Descrição");
        req.put("localizacao", "Local");
        req.put("prioridade", "ALTA");
        req.put("anonimo", true);

        ResponseEntity<SolicitacaoResponse> create = rest.postForEntity("/api/solicitacoes", req, SolicitacaoResponse.class);
        assertEquals(201, create.getStatusCodeValue());
        assertNotNull(create.getBody());
        SolicitacaoResponse created = create.getBody();
        assertTrue(created.protocolo().startsWith("SOL-"));

        int id = created.id();

        // Atualizar status
        Map<String, Object> patch = new HashMap<>();
        patch.put("status", "TRIAGEM");
        patch.put("responsavel", "Tester");
        patch.put("comentario", "ok");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(patch, headers);

        ResponseEntity<Void> patchRes = rest.exchange("/api/solicitacoes/" + id + "/status", HttpMethod.PATCH, entity, Void.class);
        assertEquals(204, patchRes.getStatusCodeValue());

        // Buscar histórico
        ResponseEntity<HistoricoStatusResponse[]> histRes = rest.getForEntity("/api/solicitacoes/" + id + "/historico", HistoricoStatusResponse[].class);
        assertEquals(200, histRes.getStatusCodeValue());
        HistoricoStatusResponse[] history = histRes.getBody();
        assertNotNull(history);
        assertTrue(history.length >= 2);
    }
}
