package autofix.util;

import org.springframework.stereotype.Component;

import java.util.Locale;

/** Usado nas páginas HTML: ${@fmt.moeda(valor)} */
@Component("fmt")
public class Formatador {

    public String moeda(Double valor) {
        double v = valor == null ? 0 : valor;
        return String.format(Locale.of("pt", "BR"), "R$ %,.2f", v);
    }
}
