package autofix.config;

import autofix.model.Cliente;
import autofix.model.ItemOS;
import autofix.model.OrdemServico;
import autofix.model.Peca;
import autofix.model.Veiculo;
import autofix.repository.ClienteRepository;
import autofix.repository.OrdemServicoRepository;
import autofix.repository.PecaRepository;
import autofix.repository.VeiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Roda uma vez quando o sistema liga: se o banco estiver vazio, cadastra dados de exemplo. */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;
    private final OrdemServicoRepository osRepository;
    private final JdbcTemplate jdbc;

    public DadosIniciais(ClienteRepository clienteRepository, VeiculoRepository veiculoRepository,
                         PecaRepository pecaRepository, OrdemServicoRepository osRepository,
                         JdbcTemplate jdbc) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.pecaRepository = pecaRepository;
        this.osRepository = osRepository;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            return;   // já tem dados
        }

        // As OS começam no número 1037
        jdbc.execute("ALTER TABLE ordem_servico AUTO_INCREMENT = 1037");

        Cliente carlos = cliente("Carlos Eduardo Silva", "123.456.789-00", "(47) 99123-4567");
        Cliente marina = cliente("Marina Souza Lima", "987.654.321-00", "(47) 99876-1122");
        Cliente norte = cliente("Transportes Norte Ltda", "12.345.678/0001-90", "(47) 3422-5566");

        Veiculo argo = veiculo(carlos, "ABC-1D23", "Fiat Argo 1.3", 2020);
        veiculo(carlos, "QWE-4R56", "VW Gol 1.0", 2014);
        Veiculo civic = veiculo(marina, "RTY-7U89", "Honda Civic EXL", 2018);
        Veiculo ducato = veiculo(norte, "OPQ-5R61", "Fiat Ducato", 2019);

        // (o estoque já considera as peças usadas nas OS de exemplo)
        Peca filtro = peca("P-0001", "Filtro de óleo Fram", 24, 10, 32.00);
        peca("P-0002", "Pastilha de freio dianteira", 4, 6, 109.90);
        Peca amortecedor = peca("P-0003", "Amortecedor dianteiro", 2, 4, 349.90);
        Peca vela = peca("P-0004", "Vela de ignição NGK", 36, 16, 26.00);
        peca("P-0005", "Correia dentada", 0, 3, 149.90);
        Peca oleo = peca("P-0006", "Óleo 5W30 sintético 1L", 32, 20, 49.90);

        OrdemServico os1 = os(argo, "Anderson", "Barulho na suspensão dianteira", 180.00);
        item(os1, amortecedor, 2);
        osRepository.save(os1);

        OrdemServico os2 = os(civic, "Paulo Ribeiro", "Revisão dos 90 mil km", 135.00);
        item(os2, filtro, 1);
        item(os2, oleo, 4);
        os2.setStatus("PAGA");
        os2.setFormaPagamento("Pix");
        osRepository.save(os2);

        OrdemServico os3 = os(ducato, "Anderson", "Falha na partida", 90.00);
        item(os3, vela, 4);
        osRepository.save(os3);
    }

    private Cliente cliente(String nome, String cpf, String telefone) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCpf(cpf);
        c.setTelefone(telefone);
        return clienteRepository.save(c);
    }

    private Veiculo veiculo(Cliente dono, String placa, String modelo, int ano) {
        Veiculo v = new Veiculo();
        v.setCliente(dono);
        v.setPlaca(placa);
        v.setModelo(modelo);
        v.setAno(ano);
        return veiculoRepository.save(v);
    }

    private Peca peca(String codigo, String descricao, int quantidade, int minimo, double preco) {
        Peca p = new Peca();
        p.setCodigo(codigo);
        p.setDescricao(descricao);
        p.setQuantidade(quantidade);
        p.setMinimo(minimo);
        p.setPreco(preco);
        return pecaRepository.save(p);
    }

    private OrdemServico os(Veiculo veiculo, String mecanico, String problema, double maoDeObra) {
        OrdemServico os = new OrdemServico();
        os.setVeiculo(veiculo);
        os.setMecanico(mecanico);
        os.setProblema(problema);
        os.setMaoDeObra(maoDeObra);
        os.setDataAbertura(LocalDate.now());
        return os;
    }

    private void item(OrdemServico os, Peca peca, int quantidade) {
        ItemOS item = new ItemOS();
        item.setOs(os);
        item.setPeca(peca);
        item.setQuantidade(quantidade);
        item.setValorUnitario(peca.getPreco());
        os.getItens().add(item);
    }
}
