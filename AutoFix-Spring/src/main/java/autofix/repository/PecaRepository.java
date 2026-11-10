package autofix.repository;

import autofix.model.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PecaRepository extends JpaRepository<Peca, Integer> {

    List<Peca> findAllByOrderByCodigo();

    List<Peca> findByCodigoContainingIgnoreCaseOrDescricaoContainingIgnoreCaseOrderByCodigo(
            String codigo, String descricao);
}
