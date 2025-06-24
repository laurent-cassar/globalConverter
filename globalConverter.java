import java.util.Scanner;
import java.util.regex.Pattern;

// Classe principale de l'application
public class globalConverter {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== Application de Conversion de Bases et Chiffrement ===");
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput("Votre choix: ");
                
                switch (choice) {
                    case 1:
                        performBaseConversion();
                        break;
                    case 2:
                        performEncryption();
                        break;
                    case 3:
                        performDecryption();
                        break;
                    case 4:
                        System.out.println("Au revoir !");
                        return;
                    default:
                        System.out.println("Choix invalide. Veuillez choisir entre 1 et 4.");
                }
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
            
            System.out.println("\n" + "=".repeat(50) + "\n");
        }
    }
    
    private static void showMainMenu() {
        System.out.println("1. Conversion de bases");
        System.out.println("2. Chiffrement");
        System.out.println("3. Déchiffrement");
        System.out.println("4. Quitter");
    }
    
    private static void performBaseConversion() {
        // Saisie et validation de la chaîne
        String input = getValidatedInput();
        
        // Choix de la base source
        BaseType sourceBase = getBaseChoice("Choisissez la base source");
        
        // Choix de la base destination
        BaseType targetBase = getBaseChoice("Choisissez la base de destination");
        
        // Conversion
        BaseConverter converter = new BaseConverter();
        String result = converter.convert(input, sourceBase, targetBase);
        
        System.out.println("\nRésultat de la conversion:");
        System.out.println("Entrée (" + sourceBase + "): " + input);
        System.out.println("Sortie (" + targetBase + "): " + result);
    }
    
    private static void performEncryption() {
        String input = getValidatedInput();
        
        System.out.println("Algorithmes de chiffrement disponibles:");
        System.out.println("1. César");
        System.out.println("2. Vigenère");
        
        int choice = getIntInput("Choisissez l'algorithme (1-2): ");
        Cipher cipher;
        
        switch (choice) {
            case 1:
                int key = getIntInput("Entrez la clé de décalage pour César: ");
                cipher = new CaesarCipher(key);
                break;
            case 2:
                String vigenereKey = getValidatedInput("Entrez la clé pour Vigenère: ");
                cipher = new VigenereCipher(vigenereKey);
                break;
            default:
                System.out.println("Algorithme invalide, utilisation de César par défaut.");
                cipher = new CaesarCipher(3);
        }
        
        String encrypted = cipher.encrypt(input);
        System.out.println("\nTexte chiffré: " + encrypted);
    }
    
    private static void performDecryption() {
        String input = getValidatedInput();
        
        System.out.println("Algorithmes de déchiffrement disponibles:");
        System.out.println("1. César");
        System.out.println("2. Vigenère");
        
        int choice = getIntInput("Choisissez l'algorithme (1-2): ");
        Cipher cipher;
        
        switch (choice) {
            case 1:
                int key = getIntInput("Entrez la clé de décalage pour César: ");
                cipher = new CaesarCipher(key);
                break;
            case 2:
                String vigenereKey = getValidatedInput("Entrez la clé pour Vigenère: ");
                cipher = new VigenereCipher(vigenereKey);
                break;
            default:
                System.out.println("Algorithme invalide, utilisation de César par défaut.");
                cipher = new CaesarCipher(3);
        }
        
        String decrypted = cipher.decrypt(input);
        System.out.println("\nTexte déchiffré: " + decrypted);
    }
    
    private static String getValidatedInput() {
        return getValidatedInput("Entrez une chaîne alphanumèrique: ");
    }
    
    private static String getValidatedInput(String prompt) {
        String input;
        Pattern alphanumericPattern = Pattern.compile("^[a-zA-Z0-9\\s]+$");
        
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();
            
            if (input.trim().isEmpty()) {
                System.out.println("Erreur: La chaîne ne peut pas être vide.");
                continue;
            }
            
            if (!alphanumericPattern.matcher(input).matches()) {
                System.out.println("Erreur: Seuls les caractères alphanumériques et espaces sont autorisés.");
                continue;
            }
            
            break;
        }
        
        return input;
    }
    
    private static BaseType getBaseChoice(String prompt) {
        while (true) {
            System.out.println("\n" + prompt + ":");
            System.out.println("hexadecimal (-h), octal (-o), decimal (-d), binary (-b), text (-t)");
            System.out.print("Votre choix: ");
            
            String choice = scanner.nextLine().toLowerCase().trim();
            
            switch (choice) {
                case "hexadecimal":
                case "-h":
                case "h":
                    return BaseType.HEXADECIMAL;
                case "octal":
                case "-o":
                case "o":
                    return BaseType.OCTAL;
                case "decimal":
                case "-d":
                case "d":
                    return BaseType.DECIMAL;
                case "binary":
                case "-b":
                case "b":
                    return BaseType.BINARY;
                case "text":
                case "-t":
                case "t":
                    return BaseType.TEXT;
                default:
                    System.out.println("Base invalide. Veuillez réessayer.");
            }
        }
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }
}

// Énumération des types de bases
enum BaseType {
    HEXADECIMAL, OCTAL, DECIMAL, BINARY, TEXT;
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

// Classe pour la conversion de bases
class BaseConverter {
    
    public String convert(String input, BaseType sourceBase, BaseType targetBase) {
        if (sourceBase == targetBase) {
            return input;
        }
        
        // Conversion vers ASCII décimal comme étape intermédiaire
        int[] asciiValues = convertToAscii(input, sourceBase);
        
        // Conversion des valeurs ASCII vers la base cible
        return convertFromAscii(asciiValues, targetBase);
    }
    
    private int[] convertToAscii(String input, BaseType sourceBase) {
        switch (sourceBase) {
            case TEXT:
                return convertTextToAscii(input);
            case BINARY:
                return convertBinaryToAscii(input);
            case OCTAL:
                return convertOctalToAscii(input);
            case DECIMAL:
                return convertDecimalToAscii(input);
            case HEXADECIMAL:
                return convertHexToAscii(input);
            default:
                throw new IllegalArgumentException("Base source non supportée");
        }
    }
    
    private String convertFromAscii(int[] asciiValues, BaseType targetBase) {
        switch (targetBase) {
            case TEXT:
                return convertAsciiToText(asciiValues);
            case BINARY:
                return convertAsciiToBinary(asciiValues);
            case OCTAL:
                return convertAsciiToOctal(asciiValues);
            case DECIMAL:
                return convertAsciiToDecimal(asciiValues);
            case HEXADECIMAL:
                return convertAsciiToHex(asciiValues);
            default:
                throw new IllegalArgumentException("Base cible non supportée");
        }
    }
    
    // Conversions depuis le texte vers ASCII
    private int[] convertTextToAscii(String text) {
        int[] ascii = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            ascii[i] = (int) text.charAt(i);
        }
        return ascii;
    }
    
    // Conversions depuis ASCII vers les différentes bases
    private String convertAsciiToText(int[] ascii) {
        StringBuilder result = new StringBuilder();
        for (int value : ascii) {
            result.append((char) value);
        }
        return result.toString();
    }
    
    private String convertAsciiToBinary(int[] ascii) {
        StringBuilder result = new StringBuilder();
        for (int value : ascii) {
            result.append(decimalToBinary(value));
        }
        return result.toString();
    }
    
    private String convertAsciiToOctal(int[] ascii) {
        StringBuilder result = new StringBuilder();
        for (int value : ascii) {
            result.append(decimalToOctal(value));
        }
        return result.toString();
    }
    
    private String convertAsciiToDecimal(int[] ascii) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ascii.length; i++) {
            result.append(ascii[i]);
            if (i < ascii.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }
    
    private String convertAsciiToHex(int[] ascii) {
        StringBuilder result = new StringBuilder();
        for (int value : ascii) {
            result.append(decimalToHex(value));
        }
        return result.toString();
    }
    
    // Conversions depuis les bases vers ASCII
    private int[] convertBinaryToAscii(String binary) {
        // Regrouper par blocs de 8 bits (ou longueur minimale pour représenter les caractères ASCII)
        return parseEncodedString(binary, 2, 8);
    }
    
    private int[] convertOctalToAscii(String octal) {
        // Regrouper par blocs de 3 chiffres octaux
        return parseEncodedString(octal, 8, 3);
    }
    
    private int[] convertDecimalToAscii(String decimal) {
        String[] parts = decimal.trim().split("\\s+");
        int[] ascii = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ascii[i] = Integer.parseInt(parts[i]);
        }
        return ascii;
    }
    
    private int[] convertHexToAscii(String hex) {
        // Regrouper par blocs de 2 chiffres hexadécimaux
        return parseEncodedString(hex, 16, 2);
    }
    
    private int[] parseEncodedString(String encoded, int base, int blockSize) {
        // Supprimer les espaces
        encoded = encoded.replaceAll("\\s", "");
        
        // Calculer le nombre de blocs
        int numBlocks = (encoded.length() + blockSize - 1) / blockSize;
        int[] ascii = new int[numBlocks];
        
        for (int i = 0; i < numBlocks; i++) {
            int start = i * blockSize;
            int end = Math.min(start + blockSize, encoded.length());
            String block = encoded.substring(start, end);
            
            ascii[i] = parseToDecimal(block, base);
        }
        
        return ascii;
    }
    
    // Conversions manuelles (sans utiliser les fonctions système Java)
    private String decimalToBinary(int decimal) {
        if (decimal == 0) return "00000000"; // 8 bits pour ASCII
        
        StringBuilder binary = new StringBuilder();
        while (decimal > 0) {
            binary.insert(0, decimal % 2);
            decimal /= 2;
        }
        
        // Padding à 8 bits
        while (binary.length() < 8) {
            binary.insert(0, '0');
        }
        
        return binary.toString();
    }
    
    private String decimalToOctal(int decimal) {
        if (decimal == 0) return "000";
        
        StringBuilder octal = new StringBuilder();
        while (decimal > 0) {
            octal.insert(0, decimal % 8);
            decimal /= 8;
        }
        
        // Padding à 3 chiffres
        while (octal.length() < 3) {
            octal.insert(0, '0');
        }
        
        return octal.toString();
    }
    
    private String decimalToHex(int decimal) {
        if (decimal == 0) return "00";
        
        StringBuilder hex = new StringBuilder();
        char[] hexChars = "0123456789ABCDEF".toCharArray();
        
        while (decimal > 0) {
            hex.insert(0, hexChars[decimal % 16]);
            decimal /= 16;
        }
        
        // Padding à 2 chiffres
        while (hex.length() < 2) {
            hex.insert(0, '0');
        }
        
        return hex.toString();
    }
    
    private int parseToDecimal(String value, int base) {
        int result = 0;
        int power = 0;
        
        for (int i = value.length() - 1; i >= 0; i--) {
            char c = value.charAt(i);
            int digit = 0;
            
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                digit = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                digit = c - 'a' + 10;
            }
            
            if (digit >= base) {
                throw new IllegalArgumentException("Chiffre invalide pour la base " + base + ": " + c);
            }
            
            result += digit * power(base, power);
            power++;
        }
        
        return result;
    }
    
    private int power(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }
}

// Interface pour les algorithmes de chiffrement
interface Cipher {
    String encrypt(String text);
    String decrypt(String text);
}

// Implémentation du chiffrement de César
class CaesarCipher implements Cipher {
    private final int shift;
    
    public CaesarCipher(int shift) {
        this.shift = shift % 26; // S'assurer que le décalage est dans la plage 0-25
    }
    
    @Override
    public String encrypt(String text) {
        return shiftText(text, shift);
    }
    
    @Override
    public String decrypt(String text) {
        return shiftText(text, -shift);
    }
    
    private String shiftText(String text, int shift) {
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shifted = (c - base + shift + 26) % 26;
                result.append((char) (base + shifted));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}

// Implémentation du chiffrement de Vigenère
class VigenereCipher implements Cipher {
    private final String key;
    
    public VigenereCipher(String key) {
        this.key = key.toUpperCase().replaceAll("[^A-Z]", "");
        if (this.key.isEmpty()) {
            throw new IllegalArgumentException("La clé doit contenir au moins une lettre");
        }
    }
    
    @Override
    public String encrypt(String text) {
        return processText(text, true);
    }
    
    @Override
    public String decrypt(String text) {
        return processText(text, false);
    }
    
    private String processText(String text, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int textChar = c - base;
                int keyChar = key.charAt(keyIndex % key.length()) - 'A';
                
                int shifted = encrypt ? 
                    (textChar + keyChar) % 26 : 
                    (textChar - keyChar + 26) % 26;
                
                result.append((char) (base + shifted));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}