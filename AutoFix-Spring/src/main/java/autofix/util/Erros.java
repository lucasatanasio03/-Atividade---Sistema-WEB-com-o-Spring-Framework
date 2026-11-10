package autofix.util;

/** Transforma erros técnicos em mensagens amigáveis para o usuário. */
public class Erros {

    public static String mensagem(Exception e) {
        // nossas próprias validações (ex.: "Informe o nome do cliente")
        if (e instanceof IllegalArgumentException && e.getMessage() != null) {
            return e.getMessage();
        }
        // erros do MySQL: procura a causa original
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        String texto = causa.getMessage() == null ? "" : causa.getMessage();
        if (texto.contains("Duplicate entry")) {
            return "Já existe um cadastro com esse valor (CPF, placa ou código repetido).";
        }
        if (texto.contains("foreign key constraint")) {
            return "Não é possível concluir: existem outros cadastros ligados a este registro.";
        }
        return "Erro: " + e.getMessage();
    }
}
