package autofix.controller;

import autofix.model.Veiculo;
import autofix.service.ClienteService;
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

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService service;
    private final ClienteService clienteService;

    public VeiculoController(VeiculoService service, ClienteService clienteService) {
        this.service = service;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String busca,
                         @RequestParam(required = false) Integer editar,
                         Model model) {
        preparar(model, service.buscarOuNovo(editar), busca);
        return "veiculos";
    }

    @PostMapping("/salvar")
    public String salvar(Veiculo veiculo,
                         @RequestParam(required = false) Integer clienteId,
                         Model model, RedirectAttributes redirect) {
        try {
            service.salvar(veiculo, clienteId);
            redirect.addFlashAttribute("mensagem", "Veículo salvo com sucesso!");
            return "redirect:/veiculos";
        } catch (Exception e) {
            model.addAttribute("erro", Erros.mensagem(e));
            preparar(model, veiculo, "");
            return "veiculos";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("mensagem", "Veículo excluído.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", Erros.mensagem(e));
        }
        return "redirect:/veiculos";
    }

    private void preparar(Model model, Veiculo veiculo, String busca) {
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("busca", busca);
        model.addAttribute("clientes", clienteService.listar(""));
        model.addAttribute("lista", service.listar(busca));
    }
}
