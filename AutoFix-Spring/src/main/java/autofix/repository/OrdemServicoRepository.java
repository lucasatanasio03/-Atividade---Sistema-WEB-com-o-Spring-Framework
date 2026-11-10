package autofix.repository;

import autofix.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findAllByOrderByIdDesc();

    List<OrdemServico> findByStatusOrderByIdDesc(String status);
}
