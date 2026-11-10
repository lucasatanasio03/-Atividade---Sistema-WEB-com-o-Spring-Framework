package autofix.controller;

import autofix.model.Peca;
import autofix.service.PecaService;
import autofix.util.Erros;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Estoque de peças. */
@Controller
@RequestMapping("/estoque")
public class PecaController {

    private final PecaService service;

    public PecaController(PecaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String busca,
                         @RequestParam(required = false) Integer editar,
                         Model model) {
        preparar(model, service.buscarOuNova(editar), busca);
        return "estoque";
    }

    @PostMapping("/salvar")
    public String salvar(Peca peca, Model model, RedirectAttributes redirect) {
        try {
            service.salvar(peca);
            redirect.addFlashAttribute("mensagem", "Peça salva com sucesso!");
            return "redirect:/estoque";
        } catch (Exception e) {
            model.addAttribute("erro", Erros.mensagem(e));
            preparar(model, peca, "");
            return "estoque";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("mensagem", "Peça excluída.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
        }
        return "redirect:/estoque";
    }

    private void preparar(Model model, Peca peca, String busca) {
        List<Peca> lista = service.listar(busca);
        model.addAttribute("peca", peca);
        model.addAttribute("busca", busca);
        model.addAttribute("lista", lista);
        model.addAttribute("alertas", service.contarAlertas(lista));
    }
}
