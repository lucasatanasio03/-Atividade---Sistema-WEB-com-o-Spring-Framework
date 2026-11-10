package autofix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Ponto de partida: ao executar esta classe, o Spring sobe o servidor em http://localhost:8080 */
@SpringBootApplication
public class AutofixApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutofixApplication.class, args);
    }
}
