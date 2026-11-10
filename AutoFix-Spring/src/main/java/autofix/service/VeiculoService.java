package autofix.service;

import autofix.model.Cliente;
import autofix.model.Veiculo;
import autofix.repository.ClienteRepository;
import autofix.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio dos veículos. */
@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final ClienteRepository clienteRepository;

    public VeiculoService(VeiculoRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    public List<Veiculo> listar(String busca) {
        if (busca == null || busca.isBlank()) {
            return repository.findAllByOrderByPlaca();
        }
        return repository.findByPlacaContainingIgnoreCaseOrModeloContainingIgnoreCaseOrClienteNomeContainingIgnoreCaseOrderByPlaca(
                busca, busca, busca);
    }

    public Veiculo buscarOuNovo(Integer id) {
        if (id == null) {
            return new Veiculo();
        }
        return repository.findById(id).orElse(new Veiculo());
    }

    @Transactional
    public Veiculo salvar(Veiculo veiculo, Integer clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Selecione o cliente (dono do veículo).");
        }
        Cliente dono = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isBlank()
                || veiculo.getModelo() == null || veiculo.getModelo().isBlank()) {
            throw new IllegalArgumentException("Informe a placa e o modelo.");
        }
        if (veiculo.getAno() != null && (veiculo.getAno() < 1900 || veiculo.getAno() > 2100)) {
            throw new IllegalArgumentException("Ano inválido.");
        }
        veiculo.setCliente(dono);
        veiculo.setPlaca(veiculo.getPlaca().trim().toUpperCase());
        veiculo.setModelo(veiculo.getModelo().trim());
        return repository.save(veiculo);
    }

    @Transactional
    public void excluir(Integer id) {
        repository.deleteById(id);
    }
}
