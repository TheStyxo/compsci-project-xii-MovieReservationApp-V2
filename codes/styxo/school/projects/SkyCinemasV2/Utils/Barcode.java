package codes.styxo.school.projects.SkyCinemasV2.Utils;

//Class to handle barcode generation, these aren't standardized, just an internal format that might be used by the specific cinema
public class Barcode {
    //Because barcode scanner scans the black part, need to invert the colors in case of VSCode dark theme
    private static final boolean negateBarcodeForVSCode = true;
    private static final String sideGuard = "101";
    private static final String centerGuard = "01010";
    private static final String[] leftEncoding = {
            "0001101",
            "0011001",
            "0010011",
            "0111101",
            "0100011",
            "0110001",
            "0101111",
            "0111011",
            "0110111",
            "0001011"
    };
    private static final String[] rightEncoding = {
            "1110010",
            "1100110",
            "1101100",
            "1000010",
            "1011100",
            "1001110",
            "1010000",
            "1000100",
            "1001000",
            "1110100"
    };

    public static String generate(String code) {
        //Handle too large codes
        if (code.length() > 10)
            return "ERROR GENERATING BARCODE: CODE TOO LONG";

        //Make the code to length 10 if it is shorter
        if (code.length() < 10)
            code = "0".repeat(10 - code.length()) + code;

        int[] codeDigits = new int[11];
        for (int i = 1; i < 11; i++)
            codeDigits[i] = Integer.parseInt(code.charAt(i - 1) + "");

        //Construct the barcode segments
        String[] barcodeSegments = new String[15];
        barcodeSegments[0] = sideGuard;
        barcodeSegments[7] = centerGuard;
        barcodeSegments[14] = sideGuard;
        //Add the main digits to the code
        for (int i = 2; i <= 6; i++) {
            barcodeSegments[i] = leftEncoding[codeDigits[i - 1]];
            barcodeSegments[14 - i] = rightEncoding[codeDigits[12 - i]];
        }
        //Add the checksum and type digits to the code
        //Type
        codeDigits[0] = 5;
        barcodeSegments[1] = leftEncoding[codeDigits[0]];
        //Checksum
        int checksum = 0;
        for (int i = 0; i < 11; i++)
            //Multiply by 3 if it is in an odd position, ie., even index
            checksum += codeDigits[i] * (i % 2 == 0 ? 3 : 1);
        //Modulo check digit is next multiple of 10 - checksum
        //To get next multiple of 10, ((n + 9) / 10) * 10
        barcodeSegments[13] = rightEncoding[((checksum + 9) / 10) * 10 - checksum];

        String res = "";
        //Loop through the segments array
        for (String segment : barcodeSegments)
            //For each segment loop through the characters and add the corresponding color of the bit
            for (char c : segment.toCharArray())
                //Use binary XOR to negate EZ hehe, kinda a head scratcher
                res += c == '1' ^ negateBarcodeForVSCode ? "█" : " ";
        return res;
    }
}
