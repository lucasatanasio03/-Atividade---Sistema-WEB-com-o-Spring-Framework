package autofix.service;

import autofix.model.ItemOS;
import autofix.model.OrdemServico;
import autofix.model.Peca;
import autofix.model.Veiculo;
import autofix.repository.OrdemServicoRepository;
import autofix.repository.PecaRepository;
import autofix.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio das ordens de serviço (estoque e pagamento).
 * @Transactional = tudo dentro do método acontece junto; se der erro, o banco volta ao que era.
 */
@Service
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;

    public OrdemServicoService(OrdemServicoRepository osRepository,
                               VeiculoRepository veiculoRepository,
                               PecaRepository pecaRepository) {
        this.osRepository = osRepository;
        this.veiculoRepository = veiculoRepository;
        this.pecaRepository = pecaRepository;
    }

    public List<OrdemServico> listarTodas() {
        return osRepository.findAllByOrderByIdDesc();
    }

    public List<OrdemServico> listarAbertas() {
        return osRepository.findByStatusOrderByIdDesc("ABERTA");
    }

    /** Devolve a OS do número informado, ou uma OS vazia (para o formulário de nova OS). */
    public OrdemServico buscarOuNova(Integer id) {
        if (id == null) {
            return new OrdemServico();
        }
        return osRepository.findById(id).orElse(new OrdemServico());
    }

    /** Abre uma OS nova (id == null) ou altera uma aberta. */
    @Transactional
    public OrdemServico salvar(Integer id, Integer veiculoId, String mecanico, String problema, Double maoDeObra) {
        if (veiculoId == null) {
            throw new IllegalArgumentException("Selecione o veículo (cadastre um veículo antes, se necessário).");
        }
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));
        double valorMaoDeObra = maoDeObra == null ? 0 : maoDeObra;
        if (valorMaoDeObra < 0) {
            throw new IllegalArgumentException("A mão de obra não pode ser negativa.");
        }

        OrdemServico os;
        if (id == null) {
            os = new OrdemServico();
            os.setDataAbertura(LocalDate.now());
        } else {
            os = buscar(id);
            garantirAberta(os);
        }
        os.setVeiculo(veiculo);
        os.setMecanico(mecanico.trim());
        os.setProblema(problema.trim());
        os.setMaoDeObra(valorMaoDeObra);
        return osRepository.save(os);
    }

    /** Exclui a OS e devolve as peças para o estoque. */
    @Transactional
    public void excluir(Integer id) {
        OrdemServico os = buscar(id);
        garantirAberta(os);
        for (ItemOS item : os.getItens()) {
            devolverAoEstoque(item);
        }
        osRepository.delete(os);
    }

    /** Coloca uma peça na OS e já tira do estoque. */
    @Transactional
    public void adicionarItem(Integer osId, Integer pecaId, Integer quantidade) {
        OrdemServico os = buscar(osId);
        garantirAberta(os);
        if (pecaId == null) {
            throw new IllegalArgumentException("Selecione a peça.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada."));
        if (peca.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente: só há " + peca.getQuantidade()
                    + " unidade(s) de " + peca.getDescricao() + ".");
        }

        peca.setQuantidade(peca.getQuantidade() - quantidade);   // baixa o estoque
        pecaRepository.save(peca);

        ItemOS item = new ItemOS();
        item.setOs(os);
        item.setPeca(peca);
        item.setQuantidade(quantidade);
        item.setValorUnitario(peca.getPreco());                   // guarda o preço de agora
        os.getItens().add(item);
        osRepository.save(os);
    }

    /** Tira a peça da OS e devolve para o estoque. */
    @Transactional
    public void removerItem(Integer osId, Integer itemId) {
        OrdemServico os = buscar(osId);
        garantirAberta(os);
        ItemOS item = os.getItens().stream()
                .filter(i -> itemId.equals(i.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Peça da OS não encontrada."));
        devolverAoEstoque(item);
        os.getItens().remove(item);   // orphanRemoval apaga o item do banco
        osRepository.save(os);
    }

    /** Registra o pagamento e fecha a OS. */
    @Transactional
    public OrdemServico pagar(Integer osId, Double desconto, String forma) {
        OrdemServico os = buscar(osId);
        garantirAberta(os);
        double valorDesconto = desconto == null ? 0 : desconto;
        if (valorDesconto < 0 || valorDesconto > os.getSubtotal()) {
            throw new IllegalArgumentException("O desconto deve ficar entre R$ 0,00 e o subtotal da OS.");
        }
        if (forma == null || forma.isBlank()) {
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }
        os.setDesconto(valorDesconto);
        os.setFormaPagamento(forma);
        os.setStatus("PAGA");
        return osRepository.save(os);
    }

    // ---------- apoio ----------

    private OrdemServico buscar(Integer id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));
    }

    private void garantirAberta(OrdemServico os) {
        if (os.isPaga()) {
            throw new IllegalArgumentException("Esta OS já foi paga e não pode ser alterada.");
        }
    }

    private void devolverAoEstoque(ItemOS item) {
        Peca peca = item.getPeca();
        peca.setQuantidade(peca.getQuantidade() + item.getQuantidade());
        pecaRepository.save(peca);
    }
}
