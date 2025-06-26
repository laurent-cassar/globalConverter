import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

// Classe principale de l'application
public class BaseConverterApp {
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
        BaseConverter sourceConverter = getBaseConverterChoice("Choisissez la base source");
        
        // Choix de la base destination
        BaseConverter targetConverter = getBaseConverterChoice("Choisissez la base de destination");
        
        // Conversion
        ConversionEngine engine = new ConversionEngine();
        String result = engine.convert(input, sourceConverter, targetConverter);
        
        System.out.println("\nRésultat de la conversion:");
        System.out.println("Entrée (" + sourceConverter.getBaseName() + "): " + input);
        System.out.println("Sortie (" + targetConverter.getBaseName() + "): " + result);
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
    
    private static BaseConverter getBaseConverterChoice(String prompt) {
        BaseConverterFactory factory = new BaseConverterFactory();
        
        while (true) {
            System.out.println("\n" + prompt + ":");
            System.out.println("hexadecimal (-h), octal (-o), decimal (-d), binary (-b), text (-t)");
            System.out.print("Votre choix: ");
            
            String choice = scanner.nextLine().toLowerCase().trim();
            BaseConverter converter = factory.createConverter(choice);
            
            if (converter != null) {
                return converter;
            }
            
            System.out.println("Base invalide. Veuillez réessayer.");
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

// Classe abstraite de base pour tous les convertisseurs
abstract class BaseConverter {
    protected final String baseName;
    protected final int baseValue;
    
    public BaseConverter(String baseName, int baseValue) {
        this.baseName = baseName;
        this.baseValue = baseValue;
    }
    
    public String getBaseName() {
        return baseName;
    }
    
    public int getBaseValue() {
        return baseValue;
    }
    
    // Méthodes abstraites que chaque sous-classe doit implémenter
    public abstract int[] toAscii(String input);
    public abstract String fromAscii(int[] asciiValues);
    
    // Méthodes utilitaires communes
    protected int power(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }
    
    protected int parseCharToDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException("Caractère invalide: " + c);
    }
}

// Convertisseur pour le texte
class TextConverter extends BaseConverter {
    public TextConverter() {
        super("text", 10); // Base 10 pour représentation
    }
    
    @Override
    public int[] toAscii(String input) {
        int[] ascii = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            ascii[i] = (int) input.charAt(i);
        }
        return ascii;
    }
    
    @Override
    public String fromAscii(int[] asciiValues) {
        StringBuilder result = new StringBuilder();
        for (int value : asciiValues) {
            result.append((char) value);
        }
        return result.toString();
    }
}

// Convertisseur pour le binaire
class BinaryConverter extends BaseConverter {
    public BinaryConverter() {
        super("binary", 2);
    }
    
    @Override
    public int[] toAscii(String input) {
        // Supprimer les espaces
        input = input.replaceAll("\\s", "");
        
        // Regrouper par blocs de 8 bits
        int numBlocks = (input.length() + 7) / 8; // Arrondir vers le haut
        int[] ascii = new int[numBlocks];
        
        for (int i = 0; i < numBlocks; i++) {
            int start = i * 8;
            int end = Math.min(start + 8, input.length());
            String block = input.substring(start, end);
            
            ascii[i] = binaryToDecimal(block);
        }
        
        return ascii;
    }
    
    @Override
    public String fromAscii(int[] asciiValues) {
        StringBuilder result = new StringBuilder();
        for (int value : asciiValues) {
            result.append(decimalToBinary(value));
        }
        return result.toString();
    }
    
    private int binaryToDecimal(String binary) {
        int result = 0;
        int power = 0;
        
        for (int i = binary.length() - 1; i >= 0; i--) {
            char bit = binary.charAt(i);
            if (bit != '0' && bit != '1') {
                throw new IllegalArgumentException("Caractère binaire invalide: " + bit);
            }
            
            if (bit == '1') {
                result += power(2, power);
            }
            power++;
        }
        
        return result;
    }
    
    private String decimalToBinary(int decimal) {
        if (decimal == 0) return "00000000";
        
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
}

// Convertisseur pour l'octal
class OctalConverter extends BaseConverter {
    public OctalConverter() {
        super("octal", 8);
    }
    
    @Override
    public int[] toAscii(String input) {
        input = input.replaceAll("\\s", "");
        
        // Regrouper par blocs de 3 chiffres octaux
        int numBlocks = (input.length() + 2) / 3;
        int[] ascii = new int[numBlocks];
        
        for (int i = 0; i < numBlocks; i++) {
            int start = i * 3;
            int end = Math.min(start + 3, input.length());
            String block = input.substring(start, end);
            
            ascii[i] = octalToDecimal(block);
        }
        
        return ascii;
    }
    
    @Override
    public String fromAscii(int[] asciiValues) {
        StringBuilder result = new StringBuilder();
        for (int value : asciiValues) {
            result.append(decimalToOctal(value));
        }
        return result.toString();
    }
    
    private int octalToDecimal(String octal) {
        int result = 0;
        int power = 0;
        
        for (int i = octal.length() - 1; i >= 0; i--) {
            int digit = parseCharToDigit(octal.charAt(i));
            if (digit >= 8) {
                throw new IllegalArgumentException("Chiffre octal invalide: " + octal.charAt(i));
            }
            
            result += digit * power(8, power);
            power++;
        }
        
        return result;
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
}

// Convertisseur pour le décimal
class DecimalConverter extends BaseConverter {
    public DecimalConverter() {
        super("decimal", 10);
    }
    
    @Override
    public int[] toAscii(String input) {
        String[] parts = input.trim().split("\\s+");
        int[] ascii = new int[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            try {
                ascii[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Nombre décimal invalide: " + parts[i]);
            }
        }
        
        return ascii;
    }
    
    @Override
    public String fromAscii(int[] asciiValues) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < asciiValues.length; i++) {
            result.append(asciiValues[i]);
            if (i < asciiValues.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }
}

// Convertisseur pour l'hexadécimal
class HexadecimalConverter extends BaseConverter {
    public HexadecimalConverter() {
        super("hexadecimal", 16);
    }
    
    @Override
    public int[] toAscii(String input) {
        input = input.replaceAll("\\s", "");
        
        // Regrouper par blocs de 2 chiffres hexadécimaux
        int numBlocks = (input.length() + 1) / 2;
        int[] ascii = new int[numBlocks];
        
        for (int i = 0; i < numBlocks; i++) {
            int start = i * 2;
            int end = Math.min(start + 2, input.length());
            String block = input.substring(start, end);
            
            ascii[i] = hexToDecimal(block);
        }
        
        return ascii;
    }
    
    @Override
    public String fromAscii(int[] asciiValues) {
        StringBuilder result = new StringBuilder();
        for (int value : asciiValues) {
            result.append(decimalToHex(value));
        }
        return result.toString();
    }
    
    private int hexToDecimal(String hex) {
        int result = 0;
        int power = 0;
        
        for (int i = hex.length() - 1; i >= 0; i--) {
            int digit = parseCharToDigit(hex.charAt(i));
            if (digit >= 16) {
                throw new IllegalArgumentException("Chiffre hexadécimal invalide: " + hex.charAt(i));
            }
            
            result += digit * power(16, power);
            power++;
        }
        
        return result;
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
}

// Factory pour créer les convertisseurs selon le choix de l'utilisateur
class BaseConverterFactory {
    private final Map<String, BaseConverter> converters;
    
    public BaseConverterFactory() {
        converters = new HashMap<>();
        initializeConverters();
    }
    
    private void initializeConverters() {
        // Convertisseur texte
        TextConverter textConverter = new TextConverter();
        converters.put("text", textConverter);
        converters.put("-t", textConverter);
        converters.put("t", textConverter);
        
        // Convertisseur binaire
        BinaryConverter binaryConverter = new BinaryConverter();
        converters.put("binary", binaryConverter);
        converters.put("-b", binaryConverter);
        converters.put("b", binaryConverter);
        
        // Convertisseur octal
        OctalConverter octalConverter = new OctalConverter();
        converters.put("octal", octalConverter);
        converters.put("-o", octalConverter);
        converters.put("o", octalConverter);
        
        // Convertisseur décimal
        DecimalConverter decimalConverter = new DecimalConverter();
        converters.put("decimal", decimalConverter);
        converters.put("-d", decimalConverter);
        converters.put("d", decimalConverter);
        
        // Convertisseur hexadécimal
        HexadecimalConverter hexConverter = new HexadecimalConverter();
        converters.put("hexadecimal", hexConverter);
        converters.put("-h", hexConverter);
        converters.put("h", hexConverter);
    }
    
    public BaseConverter createConverter(String choice) {
        return converters.get(choice.toLowerCase().trim());
    }
    
    public void registerConverter(String key, BaseConverter converter) {
        converters.put(key, converter);
    }
}

// Moteur de conversion qui utilise les convertisseurs
class ConversionEngine {
    public String convert(String input, BaseConverter sourceConverter, BaseConverter targetConverter) {
        if (sourceConverter.getClass().equals(targetConverter.getClass())) {
            return input; // Même base, pas de conversion nécessaire
        }
        
        // Étape 1: Convertir l'entrée vers ASCII
        int[] asciiValues = sourceConverter.toAscii(input);
        
        // Étape 2: Convertir ASCII vers la base cible
        return targetConverter.fromAscii(asciiValues);
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
        this.shift = shift % 26;
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