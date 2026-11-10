package autofix.controller;

import autofix.model.OrdemServico;
import autofix.service.OrdemServicoService;
import autofix.service.PecaService;
import autofix.service.VeiculoService;
import autofix.util.Erros;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Ordens de serviço e as peças usadas em cada uma. */
@Controller
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService service;
    private final VeiculoService veiculoService;
    private final PecaService pecaService;

    public OrdemServicoController(OrdemServicoService service,
                                  VeiculoService veiculoService,
                                  PecaService pecaService) {
        this.service = service;
        this.veiculoService = veiculoService;
        this.pecaService = pecaService;
    }

    /** GET /ordens  (opcional: ?editar=numeroDaOS) */
    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        model.addAttribute("os", service.buscarOuNova(editar));
        model.addAttribute("lista", service.listarTodas());
        model.addAttribute("veiculos", veiculoService.listar(""));
        model.addAttribute("pecas", pecaService.listar(""));
        return "ordens";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Integer id,
                         @RequestParam(required = false) Integer veiculoId,
                         @RequestParam(defaultValue = "") String mecanico,
                         @RequestParam(defaultValue = "") String problema,
                         @RequestParam(required = false) Double maoDeObra,
                         RedirectAttributes redirect) {
        try {
            OrdemServico os = service.salvar(id, veiculoId, mecanico, problema, maoDeObra);
            redirect.addFlashAttribute("mensagem", "OS " + os.getId() + " salva com sucesso!");
            return "redirect:/ordens?editar=" + os.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
            return voltar(id);
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("mensagem", "OS excluída. As peças voltaram para o estoque.");
            return "redirect:/ordens";
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
            return voltar(id);
        }
    }

    @PostMapping("/{id}/itens")
    public String adicionarPeca(@PathVariable Integer id,
                                @RequestParam(required = false) Integer pecaId,
                                @RequestParam(required = false) Integer quantidade,
                                RedirectAttributes redirect) {
        try {
            service.adicionarItem(id, pecaId, quantidade);
            redirect.addFlashAttribute("mensagem", "Peça adicionada à OS.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
        }
        return voltar(id);
    }

    @PostMapping("/{id}/itens/{itemId}/remover")
    public String removerPeca(@PathVariable Integer id, @PathVariable Integer itemId,
                              RedirectAttributes redirect) {
        try {
            service.removerItem(id, itemId);
            redirect.addFlashAttribute("mensagem", "Peça removida. O estoque foi devolvido.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
        }
        return voltar(id);
    }

    private String voltar(Integer id) {
        return id == null ? "redirect:/ordens" : "redirect:/ordens?editar=" + id;
    }
}
