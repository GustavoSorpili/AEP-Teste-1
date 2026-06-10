package org.aep.observacao.service;

import org.aep.observacao.model.*;
import org.aep.observacao.repository.HistoricoStatusRepository;
import org.aep.observacao.repository.JdbcHistoricoStatusRepository;
import org.aep.observacao.repository.JdbcSolicitacaoRepository;
import org.aep.observacao.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoSolicitacoes {
    private final SolicitacaoRepository solicitacaoRepository;
    private final HistoricoStatusRepository historicoRepository;

    public ServicoSolicitacoes() {
        this(new JdbcSolicitacaoRepository(), new JdbcHistoricoStatusRepository());
    }

    public ServicoSolicitacoes(SolicitacaoRepository solicitacaoRepository, HistoricoStatusRepository historicoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.historicoRepository = historicoRepository;
    }

    public Solicitacao criarSolicitacao(Categoria categoria, String descricao, String localizacao, Prioridade prioridade, Usuario usuario, boolean anonimo) {
        int solicitacaoId = proximoSolicitacaoId();
        String protocolo = "SOL-" + String.format("%06d", solicitacaoId);
        Solicitacao solicitacao = new Solicitacao(solicitacaoId, protocolo, categoria, descricao, localizacao, prioridade, Status.ABERTO, LocalDateTime.now(), usuario, anonimo);
        solicitacaoRepository.save(solicitacao);
        adicionarHistorico(solicitacao.getId(), Status.ABERTO, "Sistema", "Solicitação criada");
        return solicitacao;
    }

    public List<Solicitacao> listarSolicitacoes(Prioridade prioridade, String bairro, Categoria categoria) {
        return solicitacaoRepository.findAll().stream()
                .filter(s -> prioridade == null || s.getPrioridade() == prioridade)
                .filter(s -> bairro == null || s.getLocalizacao().toLowerCase().contains(bairro.toLowerCase()))
                .filter(s -> categoria == null || s.getCategoria().getNome().equalsIgnoreCase(categoria.getNome()))
                .collect(Collectors.toList());
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        return solicitacaoRepository.findByProtocolo(protocolo).orElse(null);
    }

    public boolean atualizarStatus(int solicitacaoId, Status novoStatus, String responsavel, String comentario) {
        Optional<Solicitacao> solicitacaoOptional = solicitacaoRepository.findById(solicitacaoId);
        if (solicitacaoOptional.isEmpty()) {
            return false;
        }

        Solicitacao solicitacao = solicitacaoOptional.get();
        solicitacao.setStatus(novoStatus);
        solicitacaoRepository.updateStatus(solicitacaoId, novoStatus.name());
        adicionarHistorico(solicitacaoId, novoStatus, responsavel, comentario);
        return true;
    }

    private void adicionarHistorico(int solicitacaoId, Status status, String responsavel, String comentario) {
        HistoricoStatus hist = new HistoricoStatus(proximoHistoricoId(), solicitacaoId, status, LocalDateTime.now(), responsavel, comentario);
        historicoRepository.save(hist);
    }

    public List<HistoricoStatus> getHistorico(int solicitacaoId) {
        return historicoRepository.findBySolicitacaoId(solicitacaoId);
    }

    public List<Solicitacao> getSolicitacoesPorUsuario(Usuario usuario) {
        return solicitacaoRepository.findAll().stream()
                .filter(s -> !s.isAnonimo() && s.getUsuario() != null && s.getUsuario().getId() == usuario.getId())
                .collect(Collectors.toList());
    }

    private int proximoSolicitacaoId() {
        return solicitacaoRepository.findAll().stream()
                .mapToInt(Solicitacao::getId)
                .max()
                .orElse(0) + 1;
    }

    private int proximoHistoricoId() {
        return historicoRepository.findAll().stream()
                .mapToInt(HistoricoStatus::getId)
                .max()
                .orElse(0) + 1;
    }
}
