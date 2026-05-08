package minishop.networking.jsonprotocol;

public class TextUtils {

    public static String simpleEncode(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += 3;
        }
        return new String(chars);
    }

    public static String simpleDecode(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] -= 3;
        }
        return new String(chars);
    }
}