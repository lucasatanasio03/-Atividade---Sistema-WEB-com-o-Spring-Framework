package autofix.model;

import jakarta.persistence.*;

/** Uma peça do estoque. */
@Entity
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade = 0;

    @Column(nullable = false)
    private Integer minimo = 0;

    @Column(nullable = false)
    private Double preco = 0.0;

    /** OK, BAIXO ou ZERADO (usado para pintar a linha na tela). */
    public String getSituacao() {
        if (quantidade == 0) return "ZERADO";
        if (quantidade <= minimo) return "BAIXO";
        return "OK";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        this.minimo = minimo;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
