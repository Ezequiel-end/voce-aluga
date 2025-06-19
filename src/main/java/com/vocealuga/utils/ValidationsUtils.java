package com.vocealuga.utils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vocealuga.dao.FuncionarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidationsUtils {

    /**
     * Valida um número de CNH (11 dígitos) usando o algoritmo de verificação.
     * @param cnh Número da CNH a ser validado
     * @return true se for válido, false caso contrário
     */
    public static boolean isValidCNH(String cnh) {
        if (cnh == null || cnh.length() != 11 || !cnh.matches("\\d{11}")) {
            return false;
        }

        try {
            int[] numbers = new int[11];
            for (int i = 0; i < 11; i++) {
                numbers[i] = cnh.charAt(i) - '0';
            }

            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += numbers[i] * (9 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;
            if (firstDigit != numbers[9]) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += numbers[i] * (10 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;
            if (secondDigit != numbers[10]) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida um CPF (11 dígitos) usando o algoritmo de verificação.
     * @param cpf Número do CPF a ser validado
     * @return true se for válido, false caso contrário
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            return false;
        }

        try {
            int[] numbers = new int[11];
            for (int i = 0; i < 11; i++) {
                numbers[i] = cpf.charAt(i) - '0';
            }
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += numbers[i] * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;
            if (firstDigit != numbers[9]) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += numbers[i] * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;
            if (secondDigit != numbers[10]) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    /**
     * Valida se o e-mail é único (não existe no banco de dados).
     * @param email O e-mail a ser validado
     * @return true se o e-mail for único, false caso contrário
     */
    public boolean isEmailUnique(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return !funcionarioRepository.existsByEmail(email);
    }
}

