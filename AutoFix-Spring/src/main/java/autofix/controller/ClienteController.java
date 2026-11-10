package autofix.controller;

import autofix.model.Cliente;
import autofix.service.ClienteService;
import autofix.util.Erros;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller = recebe os pedidos do navegador (URLs), chama o Service
 * e devolve o nome da página HTML (template) que será mostrada.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /** GET /clientes  (opcional: ?busca=texto  ou  ?editar=id) */
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String busca,
                         @RequestParam(required = false) Integer editar,
                         Model model) {
        preparar(model, service.buscarOuNovo(editar), busca);
        return "clientes";
    }

    /** POST /clientes/salvar  (formulário da página) */
    @PostMapping("/salvar")
    public String salvar(Cliente cliente, Model model, RedirectAttributes redirect) {
        try {
            service.salvar(cliente);
            redirect.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("erro", Erros.mensagem(e));
            preparar(model, cliente, "");
            return "clientes";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("mensagem", "Cliente excluído.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
        }
        return "redirect:/clientes";
    }

    private void preparar(Model model, Cliente cliente, String busca) {
        model.addAttribute("cliente", cliente);
        model.addAttribute("busca", busca);
        model.addAttribute("lista", service.listar(busca));
    }
}
