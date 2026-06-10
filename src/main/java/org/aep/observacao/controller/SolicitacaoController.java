package org.aep.observacao.controller;

import org.aep.observacao.controller.dto.AtualizarStatusRequest;
import org.aep.observacao.controller.dto.CriarSolicitacaoRequest;
import org.aep.observacao.controller.dto.HistoricoStatusResponse;
import org.aep.observacao.controller.dto.SolicitacaoResponse;
import org.aep.observacao.controller.dto.UsuarioResponse;
import org.aep.observacao.model.Categoria;
import org.aep.observacao.model.HistoricoStatus;
import org.aep.observacao.model.Prioridade;
import org.aep.observacao.model.Solicitacao;
import org.aep.observacao.model.Status;
import org.aep.observacao.model.Usuario;
import org.aep.observacao.service.ServicoSolicitacoes;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final ServicoSolicitacoes servicoSolicitacoes;

    public SolicitacaoController(ServicoSolicitacoes servicoSolicitacoes) {
        this.servicoSolicitacoes = servicoSolicitacoes;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResponse> criarSolicitacao(@Valid @RequestBody CriarSolicitacaoRequest request) {
        Categoria categoria = new Categoria(request.categoriaNome(), request.categoriaSlaDias());
        Usuario usuario = request.anonimo() ? null : new Usuario(
                request.usuario() != null && request.usuario().id() != null ? request.usuario().id() : 0,
                request.usuario() != null ? request.usuario().nome() : null,
                request.usuario() != null ? request.usuario().email() : null,
                request.usuario() != null ? request.usuario().telefone() : null
        );
        Solicitacao solicitacao = servicoSolicitacoes.criarSolicitacao(
                categoria,
                request.descricao(),
                request.localizacao(),
                request.prioridade(),
                usuario,
                request.anonimo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitacaoResponse.from(solicitacao));
    }

    @GetMapping
    public List<SolicitacaoResponse> listarSolicitacoes(
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) String categoriaNome) {
        Categoria categoria = categoriaNome == null || categoriaNome.isBlank() ? null : new Categoria(categoriaNome, 0);
        return servicoSolicitacoes.listarSolicitacoes(prioridade, bairro, categoria).stream()
                .map(SolicitacaoResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{protocolo}")
    public SolicitacaoResponse buscarPorProtocolo(@PathVariable String protocolo) {
        Solicitacao solicitacao = servicoSolicitacoes.buscarPorProtocolo(protocolo);
        if (solicitacao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada");
        }
        return SolicitacaoResponse.from(solicitacao);
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoStatusResponse> historico(@PathVariable int id) {
        return servicoSolicitacoes.getHistorico(id).stream()
                .map(HistoricoStatusResponse::from)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable int id, @Valid @RequestBody AtualizarStatusRequest request) {
        boolean atualizado = servicoSolicitacoes.atualizarStatus(id, request.status(), request.responsavel(), request.comentario());
        if (!atualizado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada");
        }
        return ResponseEntity.noContent().build();
    }
}