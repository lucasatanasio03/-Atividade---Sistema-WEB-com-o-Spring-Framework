package autofix.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Uma ordem de serviço (OS). O número começa em 1037. */
@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @Column(length = 60)
    private String mecanico;

    @Column(length = 255)
    private String problema;

    @Column(nullable = false)
    private Double maoDeObra = 0.0;

    @Column(nullable = false, length = 10)
    private String status = "ABERTA";   // ABERTA ou PAGA

    @Column(nullable = false)
    private Double desconto = 0.0;

    @Column(length = 30)
    private String formaPagamento;

    private LocalDate dataAbertura;

    @OneToMany(mappedBy = "os", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOS> itens = new ArrayList<>();

    public boolean isPaga() {
        return "PAGA".equals(status);
    }

    public double getTotalPecas() {
        double soma = 0;
        for (ItemOS item : itens) {
            soma += item.getSubtotal();
        }
        return soma;
    }

    /** Peças + mão de obra (sem desconto). */
    public double getSubtotal() {
        return getTotalPecas() + maoDeObra;
    }

    /** Valor final, já com desconto. */
    public double getTotal() {
        return getSubtotal() - desconto;
    }

    public String getDataFormatada() {
        return dataAbertura == null ? "" : dataAbertura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getMecanico() {
        return mecanico;
    }

    public void setMecanico(String mecanico) {
        this.mecanico = mecanico;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public Double getMaoDeObra() {
        return maoDeObra;
    }

    public void setMaoDeObra(Double maoDeObra) {
        this.maoDeObra = maoDeObra;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public List<ItemOS> getItens() {
        return itens;
    }

    public void setItens(List<ItemOS> itens) {
        this.itens = itens;
    }
}
