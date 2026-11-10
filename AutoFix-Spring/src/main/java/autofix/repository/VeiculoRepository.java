package autofix.repository;

import autofix.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Integer> {

    List<Veiculo> findAllByOrderByPlaca();

    // "ClienteNome" = o campo nome do cliente do veículo
    List<Veiculo> findByPlacaContainingIgnoreCaseOrModeloContainingIgnoreCaseOrClienteNomeContainingIgnoreCaseOrderByPlaca(
            String placa, String modelo, String nomeCliente);
}
