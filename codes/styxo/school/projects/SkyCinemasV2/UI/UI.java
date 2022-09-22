package codes.styxo.school.projects.SkyCinemasV2.UI;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.User;

class Characters {
    public static final String LINE_SEPARATOR = "\n";
    public static final String HEADER_SEPARATOR = "=";
    public static final String FOOTER_SEPARATOR = "~";
    public static final String ERROR_SEPARATOR = "`";
    public static final String EMPTY_SPACE = " ";
    public static final String EMPTY = "";
}

//Class to handle UI that is displayed to the user, this is better than having to make a new menu in each switch statement
//Just a clean way to only update the needed information and keeping the rest same
//Just good practices :)
public class UI {
    private static Scanner input = new Scanner(System.in);
    private static String headerPrimaryContent = "Sky Cinemas - Main Menu";
    private static ArrayList<String> pageContent = new ArrayList<String>();
    private static String footerContent = "Select an option";
    //These are the minimum page dimensions regardless of the content
    //Just for asthetic purposes, to not make the page look too small when there's less content
    private static final int minPageWidth = 50;
    private static final int minPageHeight = 15;
    public static String error = null;
    public static User user = null;

    //Method to set the header primary content that is displayed on the left on top
    public static void setHeaderPrimaryContent(String headerPrimaryContent) {
        if (headerPrimaryContent == null)
            headerPrimaryContent = Characters.EMPTY;
        UI.headerPrimaryContent = headerPrimaryContent.split(Characters.LINE_SEPARATOR)[0];
    }

    //Method to set the page content that is displayed
    //The page content can be multiple lines using an array of strings
    public static void setPageContent(ArrayList<String> pageContent) {
        UI.pageContent = pageContent;
    }

    //Method to set the page content that is displayed
    //The page content can be single line using a string
    public static void setPageContent(String pageContent) {
        if (pageContent == null)
            pageContent = Characters.EMPTY;
        UI.pageContent = new ArrayList<String>();
        for (String line : pageContent.split(Characters.LINE_SEPARATOR))
            UI.pageContent.add(line);
    }

    //Method to set the footer content that is displayed left alligned on bottom
    public static void setFooterContent(String footerContent) {
        if (footerContent == null)
            footerContent = Characters.EMPTY;
        UI.footerContent = footerContent;
    }

    //Method to display the rendered UI
    public static void renderView() {
        final String sessionInfo = UI.user == null ? "|logged out|" : "|" + UI.user.username + "|";

        //Minimum spacing between head primary and secondary content
        final String separator = Characters.EMPTY_SPACE.repeat(10);

        //find max line length for page
        int maxPageContentLength = 0;
        for (String line : UI.pageContent)
            maxPageContentLength = Math.max(line.length(), maxPageContentLength);

        //find max line length of footer
        int maxFootLength = 0;
        for (String line : UI.footerContent.split(Characters.LINE_SEPARATOR))
            maxFootLength = Math.max(line.length(), maxFootLength);

        //find max line length of error message
        int maxErrorLength = 0;
        if (UI.error != null)
            for (String line : UI.error.split(Characters.LINE_SEPARATOR))
                maxErrorLength = Math.max(line.length(), maxErrorLength);

        //Find max length for head and foot
        final int length = Math.max(
                Math.max(maxPageContentLength,
                        Math.max((headerPrimaryContent + separator + sessionInfo).length(),
                                Math.max(maxFootLength, maxErrorLength))),
                minPageWidth);

        //Update header
        final String header = Characters.EMPTY //set the header for page
                + Characters.HEADER_SEPARATOR.repeat(length) //top divider
                + Characters.LINE_SEPARATOR //next line
                + headerPrimaryContent //Add primary content
                + separator
                + Characters.EMPTY_SPACE
                        .repeat(length - (headerPrimaryContent + separator).length() - sessionInfo.length()) //Add appropriate spacing
                + sessionInfo //content
                + Characters.LINE_SEPARATOR //next line
                + Characters.HEADER_SEPARATOR.repeat(length); //bottom divider

        //Update footer
        final String footer = Characters.EMPTY //set the footer for page
                + Characters.FOOTER_SEPARATOR.repeat(length) //top divider
                + Characters.LINE_SEPARATOR //next line
                + (UI.error != null
                        ? (UI.error + Characters.LINE_SEPARATOR + Characters.ERROR_SEPARATOR.repeat(UI.error.length())
                                + Characters.LINE_SEPARATOR)
                        : Characters.EMPTY) //Error message if it exists
                + UI.footerContent //footer content
                + Characters.LINE_SEPARATOR //next line
                + Characters.FOOTER_SEPARATOR.repeat(length); //bottom divider
        //Set error to null as it should be printed already by now
        UI.error = null;
        String mainContent = String.join(Characters.LINE_SEPARATOR, UI.pageContent);
        final int lineCount = header.split(Characters.LINE_SEPARATOR).length //Height of header
                + mainContent.split(Characters.LINE_SEPARATOR).length //Height of main content
                + footer.split(Characters.LINE_SEPARATOR).length; //Height of footer
        if (lineCount < minPageHeight)
            mainContent += Characters.LINE_SEPARATOR.repeat((minPageHeight - lineCount));

        clearScreen();
        System.out.print(header + Characters.LINE_SEPARATOR + mainContent + Characters.LINE_SEPARATOR + footer);
    }

    //Method to request user input as string
    //To handle errors with no string length, this always returns a string with length of 1
    public static String requestInput() {
        System.out.print(Characters.LINE_SEPARATOR.repeat(2) + "Input: ");

        try {
            String input = UI.input.nextLine();
            if (input.length() == 0)
                input = Characters.EMPTY_SPACE;
            return input;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    //Method to set error message
    public static void setError(String message) {
        UI.error = message;
    }

    //Method to clear screen
    public static void clearScreen() {
        //System.out.print("\n".repeat(5));
        try {
            //Clear the terminal screen using commands
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            //Fall back to printing a large amount of lines to clear screen as a backup
            System.out.print("\n".repeat(300));
        }
    }
}
