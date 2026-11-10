package autofix.service;

import autofix.model.Cliente;
import autofix.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio dos clientes. O controller sempre passa por aqui (nunca direto no repository). */
@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> listar(String busca) {
        if (busca == null || busca.isBlank()) {
            return repository.findAllByOrderByNome();
        }
        return repository.findByNomeContainingIgnoreCaseOrCpfContainingOrTelefoneContainingOrderByNome(
                busca, busca, busca);
    }

    /** Devolve o cliente do id informado, ou um cliente vazio (para o formulário de cadastro). */
    public Cliente buscarOuNovo(Integer id) {
        if (id == null) {
            return new Cliente();
        }
        return repository.findById(id).orElse(new Cliente());
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("Informe o nome do cliente (mínimo 3 letras).");
        }
        cliente.setNome(cliente.getNome().trim());
        if (cliente.getCpf() != null && cliente.getCpf().isBlank()) {
            cliente.setCpf(null);   // vazio vira "sem CPF" (CPF repetido não é permitido)
        }
        return repository.save(cliente);
    }

    @Transactional
    public void excluir(Integer id) {
        repository.deleteById(id);
    }
}
