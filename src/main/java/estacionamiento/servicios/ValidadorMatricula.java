package estacionamiento.servicios;

import java.util.regex.Pattern;

public class ValidadorMatricula {

    private static final Pattern FORMATO =
            Pattern.compile("[A-Za-z]{3}\\d{4}");

    public static String normalizar(String matricula) {
        if (matricula == null) {
            return null;
        }

        return matricula.replaceAll("\\s+", "")
                .toUpperCase(java.util.Locale.ROOT);
    }

    public static boolean esValida(String matricula) {

        if (matricula == null) {
            return false;
        }

        return FORMATO.matcher(matricula).matches();
    }
}
