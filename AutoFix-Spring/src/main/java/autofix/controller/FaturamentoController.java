package autofix.controller;

import autofix.model.OrdemServico;
import autofix.service.OrdemServicoService;
import autofix.util.Erros;
import autofix.util.Formatador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Faturamento: escolhe uma OS aberta, aplica desconto e registra o pagamento. */
@Controller
@RequestMapping("/faturamento")
public class FaturamentoController {

    private final OrdemServicoService service;
    private final Formatador formatador;

    public FaturamentoController(OrdemServicoService service, Formatador formatador) {
        this.service = service;
        this.formatador = formatador;
    }

    /** GET /faturamento  (opcional: ?os=numeroDaOS) */
    @GetMapping
    public String tela(@RequestParam(required = false) Integer os, Model model) {
        List<OrdemServico> abertas = service.listarAbertas();

        // a OS escolhida na URL; se não houver, a primeira da lista
        OrdemServico selecionada = abertas.isEmpty() ? null : abertas.get(0);
        for (OrdemServico o : abertas) {
            if (o.getId().equals(os)) {
                selecionada = o;
            }
        }
        model.addAttribute("abertas", abertas);
        model.addAttribute("selecionada", selecionada);
        return "faturamento";
    }

    @PostMapping("/pagar")
    public String pagar(@RequestParam Integer osId,
                        @RequestParam(required = false) Double desconto,
                        @RequestParam String forma,
                        RedirectAttributes redirect) {
        try {
            OrdemServico os = service.pagar(osId, desconto, forma);
            redirect.addFlashAttribute("mensagem", "Pagamento confirmado! OS " + os.getId()
                    + " - " + formatador.moeda(os.getTotal()) + " (" + forma + ").");
            return "redirect:/faturamento";
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
            return "redirect:/faturamento?os=" + osId;
        }
    }
}
