package autofix.repository;

import autofix.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Basta declarar a interface: o Spring Data cria a implementação sozinho.
 * Já vem pronto: save, findById, findAll, deleteById, count...
 * Os métodos abaixo são "consultas pelo nome": o Spring monta o SQL a partir do nome do método.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findAllByOrderByNome();

    List<Cliente> findByNomeContainingIgnoreCaseOrCpfContainingOrTelefoneContainingOrderByNome(
            String nome, String cpf, String telefone);
}
