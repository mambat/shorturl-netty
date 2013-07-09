package org.n3r.shorturl.base62;

public class Base62Encoder {

    public static final String space = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int spaceLength = space.length();

    public static String encode(Long sequence) {
        String base62 = "";
        do {
            int mod = (int)(sequence % spaceLength);
            base62 = space.charAt(mod) + base62;
            sequence = sequence / spaceLength;
        } while(sequence > 0);

        return base62;
    }

}
