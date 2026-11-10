package autofix.service;

import autofix.model.Peca;
import autofix.repository.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio do estoque de peças. */
@Service
public class PecaService {

    private final PecaRepository repository;

    public PecaService(PecaRepository repository) {
        this.repository = repository;
    }

    public List<Peca> listar(String busca) {
        if (busca == null || busca.isBlank()) {
            return repository.findAllByOrderByCodigo();
        }
        return repository.findByCodigoContainingIgnoreCaseOrDescricaoContainingIgnoreCaseOrderByCodigo(busca, busca);
    }

    public Peca buscarOuNova(Integer id) {
        if (id == null) {
            return new Peca();
        }
        return repository.findById(id).orElse(new Peca());
    }

    /** Quantas peças estão com estoque BAIXO ou ZERADO. */
    public int contarAlertas(List<Peca> pecas) {
        int alertas = 0;
        for (Peca p : pecas) {
            if (!p.getSituacao().equals("OK")) {
                alertas++;
            }
        }
        return alertas;
    }

    @Transactional
    public Peca salvar(Peca peca) {
        if (peca.getCodigo() == null || peca.getCodigo().isBlank()
                || peca.getDescricao() == null || peca.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Informe o código e a descrição da peça.");
        }
        if (peca.getQuantidade() == null || peca.getMinimo() == null || peca.getPreco() == null) {
            throw new IllegalArgumentException("Informe quantidade, estoque mínimo e preço.");
        }
        if (peca.getQuantidade() < 0 || peca.getMinimo() < 0 || peca.getPreco() <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade e mínimo não podem ser negativos, e o preço deve ser maior que zero.");
        }
        peca.setCodigo(peca.getCodigo().trim().toUpperCase());
        peca.setDescricao(peca.getDescricao().trim());
        return repository.save(peca);
    }

    @Transactional
    public void excluir(Integer id) {
        repository.deleteById(id);
    }
}
