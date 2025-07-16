package com.vocealuga.utils;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import com.vocealuga.dao.ClienteRepository;
import com.vocealuga.dao.FuncionarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidationsUtils {

    /**
     * Valida um número de CNH (11 dígitos) usando o algoritmo de verificação.
     * 
     * @param cnh Número da CNH a ser validado
     * @return true se for válido, false caso contrário
     */
    public static boolean isValidCNH(String cnh) {
        if (cnh == null || !cnh.matches("\\d{11}")) {
            return false;
        }

        int[] n = new int[11];
        for (int i = 0; i < 11; i++) {
            n[i] = cnh.charAt(i) - '0';
        }

        // Primeiro dígito verificador
        int d1 = 0;
        for (int i = 0, j = 9; i < 9; i++, j--) {
            d1 += n[i] * j;
        }
        d1 = d1 % 11;
        d1 = (d1 >= 10) ? 0 : d1;

        // Segundo dígito verificador
        int d2 = 0;
        for (int i = 0, j = 1; i < 9; i++, j++) {
            d2 += n[i] * j;
        }
        d2 = d2 % 11;
        if (d2 >= 10) {
            d2 = 0;
        }

        // Regra especial DETRAN: Se d2 == 10 e d1 == 0, então d2 = 0
        if ((d2 == 10) && (d1 == 0)) {
            d2 = 0;
        }

        return (d1 == n[9] && d2 == n[10]);
    }

    /**
     * Valida um CPF (11 dígitos) usando o algoritmo de verificação.
     * 
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
            if (firstDigit >= 10)
                firstDigit = 0;
            if (firstDigit != numbers[9])
                return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += numbers[i] * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10)
                secondDigit = 0;
            if (secondDigit != numbers[10])
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public boolean isEmailGloballyUnique(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        boolean existsInCliente = clienteRepository.existsByEmail(email);
        boolean existsInFuncionario = funcionarioRepository.existsByEmail(email);
        return !(existsInCliente || existsInFuncionario);
    }

    /**
     * Verifica se o CPF é globalmente único (não existe em Cliente ou Funcionario).
     * 
     * @param cpf Número do CPF a ser validado
     * @return true se o CPF não existe, false caso contrário
     */
    public boolean isCpfGloballyUnique(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        boolean existsInCliente = clienteRepository.existsByCpf(cleanCpf);
        boolean existsInFuncionario = funcionarioRepository.existsByCpf(cleanCpf);
        return !(existsInCliente || existsInFuncionario);
    }

    public boolean isMaiorDeIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        Period idade = Period.between(dataNascimento, hoje);
        return idade.getYears() >= 18;
    }
}
