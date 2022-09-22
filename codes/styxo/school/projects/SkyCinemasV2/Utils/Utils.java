package codes.styxo.school.projects.SkyCinemasV2.Utils;

import java.util.ArrayList;

//Utility methods for the project
public class Utils {
    //Method to check if the input is a valid email
    public static boolean isValidEmail(String input) {
        //Email has @ sign, so split at the @ sign
        String[] chunks = input.split("@");

        //and check if the string has an username and domain, ie. two parts when split
        if (chunks.length != 2)
            return false;

        //The username length should not be less than 1 and domain length should not be less than 4 [ domain_name(1 char minimum) + dot(1 char) + domain_extension(2 chars minimum)]
        if (chunks[0].length() < 1 || chunks[1].length() < 4)
            return false;

        //Split the domain chunks
        String[] domainChunks = chunks[1].split("\\."); //Here I'm passing the regexp as string, soo the double slash is to escape the slash and then the escaped slash and dot (\.) is because dot by itself is a reserved char in regex for any character

        //If the domain does not contain a dot and two parts then it is invalid
        if (domainChunks.length < 2)
            return false;

        //If the chunk length is less than 1, it is invalid
        for (String chunk : domainChunks)
            if (chunk.length() < 1)
                return false;

        //If the first character is not a letter
        if (!Character.isLetter((chunks[0]).charAt(0)))
            return false;

        //If any character is unsupported
        for (char c : input.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '.' && c != '@')
                return false;

        //Email cannot have [.. or .@ or @.]
        if (input.contains("..") || input.contains(".@") || input.contains("@."))
            return false;

        //Email cannot end with dot, coz domain name will be invalid
        if (input.endsWith("."))
            return false;

        //If passes validation then is valid
        return true;
    }

    //Method to check if the input is a valid phone number
    public static boolean isValidPhone(String input) {
        //The input must be of length 10
        if (input.length() != 10)
            return false;

        //Check if all chars are numbers
        if (!isValidNumber(input))
            return false;

        //If passes validation then is valid
        return true;
    }

    //Method to check if the input is a username
    public static boolean isValidUsername(String input) {
        //Username must be atleast 3 characters in length
        if (input.length() < 3)
            return false;

        //Username can only contain allowed characters
        for (char c : input.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '.')
                return false;

        //If passes validation then is valid
        return true;
    }

    //Method to check if the input has any uppercase characters
    public static boolean hasUpperCaseChar(String input) {
        //Loop through string and return true when a char is uppercase
        for (char c : input.toCharArray())
            if (Character.isUpperCase(c))
                return true;

        //If passes above loop then does not have an uppercase char
        return false;
    }

    //Method to check if the input has any lowercase characters
    public static boolean hasLowerCaseChar(String input) {
        //Loop through string and return true when a char is lowercase
        for (char c : input.toCharArray())
            if (Character.isLowerCase(c))
                return true;

        //If passes above loop then does not have a lowercase char
        return false;
    }

    //Method to check if the input has any special characters
    public static boolean hasSpecialChar(String input) {
        //Loop through string and return true when a char is a special char
        for (char c : input.toCharArray())
            if (!Character.isLetterOrDigit(c))
                return true;

        //If passes above loop then does not have a special char
        return false;
    }

    //Method to check if the input is a valid number with a decimal point
    public static boolean isValidNumberWithDecimal(String input) {
        //Loop through string and return false when a char is a not a number or decimal
        for (char c : input.toCharArray())
            if (!Character.isDigit(c) && c != '.')
                return false;

        //If passes validation then is valid
        return true;
    }

    //Method to check if the input is a valid number, ie., it has only digits
    public static boolean isValidNumber(String input) {
        for (char c : input.toCharArray())
            if (!Character.isDigit(c))
                return false;

        //If passes validation then is valid
        return true;
    }

    //Method to check if the input is a valid seat ID
    public static boolean isValidSeatID(String input) {
        //The first character must be a letter
        if (!Character.isLetter(input.charAt(0)))
            return false;

        //The other characters must be numbers
        for (int i = 1; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i)))
                return false;
        }

        //If passes validation then is valid
        return true;
    }

    //Method to check if a string is empty ("") or null
    public static boolean isEmptyString(String s) {
        return s == null || s.length() == 0;
    }

    //Method to count how many times the substring appears in the larger string
    public static int countOccurances(String text, String str) {
        if (isEmptyString(text) || isEmptyString(str))
            return 0;

        int index = 0, count = 0;

        while (true) {
            index = text.indexOf(str, index);
            if (index != -1) {
                count++;
                index += str.length();
            } else
                break;
        }

        return count;
    }

    //Method to paginate lists
    public static <T> ArrayList<ArrayList<T>> paginate(ArrayList<T> list, int maxItemsPerPage) {
        ArrayList<ArrayList<T>> res = new ArrayList<ArrayList<T>>();

        int i = 0;
        for (T item : list) {
            if (i++ % maxItemsPerPage == 0)
                res.add(new ArrayList<T>());
            res.get(res.size() - 1).add(item);
        }
        return res;
    }
}
