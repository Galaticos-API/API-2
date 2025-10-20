package util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Classe utilitária para criptografia AES-256 (CBC).
 * Esta classe lida com a criptografia e descriptografia de senhas.
 *
 * IMPORTANTE: Esta implementação usa uma chave e IV (Vetor de Inicialização)
 * hardcoded. Em um ambiente de produção real, isso é uma
 * MÁ PRÁTICA DE SEGURANÇA.
 * A chave e o IV devem ser armazenados de forma segura fora do código-fonte,
 * por exemplo, em variáveis de ambiente ou um serviço de gerenciamento de segredos.
 */
public class CriptografiaUtil {

    // Algoritmo e modo de operação/padding
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // --- CUIDADO DE SEGURANÇA ---
    // A chave DEVE ter 32 bytes (256 bits)
    // CORREÇÃO: Adicionado "*" ao final para completar 32 bytes.
    private static final String SECRET_KEY_STRING = "EstaEhUmaChaveSecretaDe32Bytes!*";
    // O IV (Vetor de Inicialização) DEVE ter 16 bytes para AES
    private static final String IV_STRING = "UmVetorDe16Bytes";
    // --- FIM DO CUIDADO ---

    private static SecretKeySpec secretKey;
    private static IvParameterSpec iv;

    // Bloco estático para inicializar a chave e o IV a partir das strings
    static {
        try {
            // Garante que a chave tenha 32 bytes
            byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length != 32) {
                // Esta exceção não deve mais ocorrer
                throw new IllegalArgumentException("A chave secreta não tem 32 bytes! Tamanho: " + keyBytes.length);
            }
            secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            // Garante que o IV tenha 16 bytes
            byte[] ivBytes = IV_STRING.getBytes(StandardCharsets.UTF_8);
            if (ivBytes.length != 16) {
                throw new IllegalArgumentException("O IV não tem 16 bytes! Tamanho: " + ivBytes.length);
            }
            iv = new IvParameterSpec(ivBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar a configuração de criptografia", e);
        }
    }

    /**
     * Criptografa um texto plano (ex: senha) usando AES-256 CBC.
     *
     * @param plainText O texto a ser criptografado.
     * @return O texto criptografado e codificado em Base64.
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Em uma aplicação real, logar o erro
            throw new RuntimeException("Erro ao criptografar a senha", e);
        }
    }

    /**
     * Descriptografa um texto (não é usado no fluxo de login, mas mantido para utilidade).
     *
     * @param cipherText O texto criptografado e codificado em Base64.
     * @return O texto plano original.
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] originalBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(originalBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Em uma aplicação real, logar o erro
            throw new RuntimeException("Erro ao descriptografar", e);
        }
    }

    /**
     * Método main para testar a criptografia e obter a string para o banco de dados.
     * Você pode executar este arquivo para obter a senha criptografada.
     */
    public static void main(String[] args) {
        // Use a senha que deseja testar
        String senhaPlana = "rh1234";
        String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);

        System.out.println("Senha Plana: " + senhaPlana);
        System.out.println("Senha Criptografada: " + senhaCriptografada);
    }
}