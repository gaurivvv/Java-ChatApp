public class EncryptionUtil {
    private static final int SHIFT = 3;

    public static String encrypt(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            sb.append((char) (c + SHIFT));
        }
        return sb.toString();
    }

    public static String decrypt(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            sb.append((char) (c - SHIFT));
        }
        return sb.toString();
    }
}
