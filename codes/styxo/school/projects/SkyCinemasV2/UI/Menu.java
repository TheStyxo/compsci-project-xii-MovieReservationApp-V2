package codes.styxo.school.projects.SkyCinemasV2.UI;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Movie;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Seat;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Show;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.ShowScreenInstance;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Ticket;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.User;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Barcode;
import codes.styxo.school.projects.SkyCinemasV2.Utils.CustomArrayList;
import codes.styxo.school.projects.SkyCinemasV2.Utils.FuzzySearch;
import codes.styxo.school.projects.SkyCinemasV2.Utils.LoginInput;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Pricing;
import codes.styxo.school.projects.SkyCinemasV2.Utils.TimeUtils;
import codes.styxo.school.projects.SkyCinemasV2.Utils.UserSearchKeyType;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

enum MenuState {
    MAIN,
    LOGIN,
    REGISTER,
    LOGGED_IN_MAIN,
    ADMIN_MENU,
    ADD_MOVIE,
    ADD_SHOW,
    BOOK_TICKET,
    VIEW_BOOKINGS,
    CLOSED
}

public class Menu {
    /** Stores the menu state */
    public static MenuState state = MenuState.MAIN;
    /** Stores the amount of password retries the user has attempted */
    private static int passwordRetries = 0;
    /** Stores the max amount of password retries before user gets thrown to login screen with error */
    private static final int maxPasswordRetries = 5;
    /** The temporay user object used to confirm login before actually logging in */
    private static User tempUser = null;
    /** The temporary show object used to store the data before adding it to storage */
    private static Show tempShow = null;
    /** The temporary movie object used to store the data before adding it to storage */
    private static Movie tempMovie = null;

    /** The max items displayed in lists per page list */
    private static final int maxItemsPerPage = 10;

    /** The seat type based on sectionIndex */
    private static final String[] seatTypes = { "Silver", "Gold", "Platinum", "Recliner" };

    /**
    * Use the menu state to draw output
    */
    public static void useState() {
        switch (state) {
            case MAIN:
                main();
                break;
            case LOGIN:
                login();
                break;
            case REGISTER:
                register();
                break;
            case LOGGED_IN_MAIN:
                logged_in_main();
                break;
            case BOOK_TICKET:
                book_ticket();
                break;
            case VIEW_BOOKINGS:
                view_bookings();
                break;
            case ADMIN_MENU:
                admin_menu();
                break;
            case ADD_MOVIE:
                add_movie();
            case ADD_SHOW:
                add_show();
                break;
            case CLOSED:
                closed();
                break;
        }
    }

    /**
    * Below are the methods for each page of the menu
    */
    public static void main() {
        //Set page content
        UI.setHeaderPrimaryContent("Sky Cinemas");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("1 - Login.");
        options.add("2 - Register new account.");
        options.add("X - Close app. [warning: deletes all data]");

        UI.setPageContent(options);
        UI.setFooterContent("Select an option.");

        //While loop for handling user input
        while (state == MenuState.MAIN) {
            //Render view each time the loop runs, used inside while loop instead of outside to handle displaying error messages in footer using the UI.invalid string
            UI.renderView();

            //The user input
            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            //Use switch with .lowerCase() to handle input in caps
            switch (input.toLowerCase()) {
                case "1":
                    state = MenuState.LOGIN;
                    break;
                case "2":
                    state = MenuState.REGISTER;
                    break;
                case "x":
                    UI.user = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    UI.setError("Invalid option provided, try again.");
            }
        }

        //Run the use state method to switch to next page after the user input stage is completed
        useState();
    }

    public static void register() {
        tempUser = new User();
        UI.setHeaderPrimaryContent("Sky Cinemas - Create an account");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("U - Edit account username.");
        options.add("F - Edit first name.");
        options.add("L - Edit last name.");
        options.add("E - Edit email.");
        options.add("P - Edit phone number.");
        options.add("S - Edit password.");
        options.add("C - Cancel login.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Account Details");
        options.add("_______________");
        options.add("Username: "); //11
        options.add("First Name: "); //12
        options.add("Last Name: "); //13
        options.add("Email: "); //14
        options.add("Phone: "); //15
        options.add("Password: "); //16
        UI.setPageContent(options); //This is reference based, so any update to the options will reflect in the render without additional calls

        String pref = null;
        while (state == MenuState.REGISTER) {

            //Check which field is empty if no preference has been set
            if (pref == null) {
                if (tempUser.username == null)
                    pref = "username";
                else if (tempUser.firstName == null)
                    pref = "first name";
                else if (tempUser.lastName == null)
                    pref = "last name";
                else if (tempUser.email == null)
                    pref = "email";
                else if (tempUser.phone == null)
                    pref = "phone number";
                else if (tempUser.password == null)
                    pref = "password";
                else
                    pref = "confirmation";
            }

            UI.setFooterContent(pref.equals("confirmation")
                    ? "Enter your password again to confirm account creation and login with the new account."
                    : String.format("Enter %s or select an option.", pref));

            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "u":
                    pref = "username";
                    break;
                case "f":
                    pref = "first name";
                    break;
                case "l":
                    pref = "last name";
                    break;
                case "e":
                    pref = "email";
                    break;
                case "p":
                    pref = "phone number";
                    break;
                case "s":
                    pref = "password";
                    break;
                case "c":
                    UI.user = null;
                    tempUser = null;
                    state = MenuState.MAIN;
                    break;
                case "x":
                    UI.user = null;
                    tempUser = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    switch (pref) {
                        case "username":
                            if (!Utils.isValidUsername(input))
                                UI.setError("The entered username is invalid, try again.");
                            else if (DataStore.getUser(input, UserSearchKeyType.USERNAME) != null)
                                UI.setError(
                                        "A different user with that username already exists, try using a different username or log in through that account.");
                            else {
                                tempUser.username = input;
                                options.set(11, "Username: " + input);
                                pref = null;
                            }
                            break;
                        case "first name":
                            if (input.length() < 2)
                                UI.setError(
                                        "First name must contain more that 2 characters.");
                            else if (input.contains(" "))
                                UI.setError(
                                        "First name cannot contain space.");
                            else {
                                tempUser.firstName = input;
                                options.set(12, "First Name: " + input);
                                pref = null;
                            }
                            break;
                        case "last name":
                            if (input.length() < 2)
                                UI.setError(
                                        "Last name must contain more that 2 characters.");
                            else if (input.contains(" "))
                                UI.setError(
                                        "Last name cannot contain space.");
                            else {
                                tempUser.lastName = input;
                                options.set(13, "Last Name: " + input);
                                pref = null;
                            }
                            break;
                        case "email":
                            if (!Utils.isValidEmail(input))
                                UI.setError(
                                        "The entered email is invalid, try again.");
                            else if (DataStore.getUser(input, UserSearchKeyType.EMAIL) != null)
                                UI.setError(
                                        "A different user with that email already exists, try using a different email or log in through that account.");
                            else {
                                tempUser.email = input;
                                options.set(14, "Email: " + input);
                                pref = null;
                            }
                            break;
                        case "phone number":
                            if (!Utils.isValidPhone(input))
                                UI.setError(
                                        "The entered phone number is invalid, try again.");
                            else if (DataStore.getUser(input, UserSearchKeyType.PHONE) != null)
                                UI.setError(
                                        "A different user with that phone number already exists, try using a different phone number or log in through that account.");
                            else {
                                tempUser.phone = input;
                                options.set(15, "Phone: " + input);
                                pref = null;
                            }
                            break;
                        case "password":
                            if (input.length() < 8)
                                UI.setError(
                                        "The entered password is too short, try again with a longer password.");
                            else if (!Utils.hasUpperCaseChar(input))
                                UI.setError(
                                        "Password must have atleast one uppercase character, try again with a different password.");
                            else if (!Utils.hasLowerCaseChar(input))
                                UI.setError(
                                        "Password must have atleast one lowercase character, try again with a different password.");
                            else if (!Utils.hasSpecialChar(input))
                                UI.setError(
                                        "Password must have atleast one special character, try again with a different password.");
                            else {
                                tempUser.password = input;
                                options.set(16, "Password: " + "*".repeat(input.length()));
                                pref = null;
                            }
                            break;
                        case "confirmation":
                        default:
                            if (!tempUser.password.equals(input)) {
                                UI.setError(
                                        "Entered passwords do not match, create a new password again.");
                                tempUser.password = null;
                                options.set(16, "Password: ");
                                pref = "password";
                            } else {
                                UI.user = DataStore.saveUser(tempUser);
                                tempUser = null;
                                state = MenuState.LOGGED_IN_MAIN;
                            }
                    }
            }
        }
        useState();
    }

    public static void login() {
        //Notes
        //The tempUser determines wether the user is entering username/other credentials or the password
        //If the user is entering the username, the tempUser will be null before that
        //If the password is being entered, a tempUser will exist

        //Set page content
        UI.setHeaderPrimaryContent("Sky Cinemas - Login");

        //Set the options
        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("C - Cancel login.");
        options.add("X - Close app. [warning: deletes all data]");
        UI.setPageContent(options);

        //Set the footer message for input username
        UI.setFooterContent("Enter username, email or phone number to login.");

        //While loop for handling user input
        while (state == MenuState.LOGIN) {
            //Render view each time the loop runs, used inside while loop instead of outside to handle displaying error messages in footer using the UI.invalid string
            UI.renderView();

            //The user input
            String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            //Use switch with .lowerCase() to handle input in caps
            switch (input.toLowerCase()) {
                case "c":
                    tempUser = null;
                    state = MenuState.MAIN;
                    break;
                case "x":
                    tempUser = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    //If the username is not entered, find the user else ask for password
                    if (tempUser == null) {
                        //Validate user input and infer a type from the format, use the type to find user later
                        final LoginInput validatedInput = new LoginInput(input);

                        //If the input is invalid, use UI.invalid string to output an error message on next run
                        if (validatedInput.type == UserSearchKeyType.INVALID) {
                            UI.setError("User not found, try again with a different username, email or phone number.");
                            break;
                        }

                        //Find and temporarily store the user for login with password
                        tempUser = DataStore.getUser(input, validatedInput.type);

                        //If the user was not found, use UI.invalid string to output an error message on next run
                        if (tempUser == null)
                            UI.setError("User not found, try again with a different username, email or phone number.");
                        else
                            //If the user was found, ask for password
                            UI.setFooterContent(
                                    String.format("Enter password for %s to complete login.", tempUser.username));
                    } else if (passwordRetries++ < maxPasswordRetries) {
                        if (!tempUser.password.equals(input)) {
                            UI.setError("Wrong password, try again.");
                            break;
                        }

                        //If the password is correct, set the user on the ui and delete the temp user
                        //Set state to logged in main menu
                        UI.user = tempUser;
                        tempUser = null;
                        state = MenuState.LOGGED_IN_MAIN;
                    } else {
                        //Reset the password attempts and remove the set username
                        passwordRetries = 0;
                        tempUser = null;
                        UI.setError("You entered the wrong password too many times, login failed, try again.");
                        //Set the footer message for input username
                        UI.setFooterContent("Enter username, email or phone number to login.");
                    }
                    break;
            }
        }

        //Run the use state method to switch to next page after the user input stage is completed
        useState();
    }

    public static void logged_in_main() {
        while (state == MenuState.LOGGED_IN_MAIN) {
            //If for some reason the user is not logged in
            //Force back to the main menu with no login
            if (UI.user == null) {
                state = MenuState.MAIN;
                break;
            }

            UI.setHeaderPrimaryContent("Welcome to Sky Cinemas");

            final CustomArrayList<String> options = new CustomArrayList<String>();

            options.add("1 - Book a ticket.");
            options.add("2 - View booking history");
            //Add admin options if user is admin
            if (UI.user.isAdmin)
                options.add("A - Admin panel.");
            options.add("L - Log out.");
            options.add("X - Close app. [warning: deletes all data]");

            UI.setPageContent(options);
            UI.setFooterContent("Select an option.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            if (UI.user.isAdmin && input.toLowerCase().equals("a")) {
                state = MenuState.ADMIN_MENU;
            } else
                switch (input.toLowerCase()) {
                    case "1":
                        state = MenuState.BOOK_TICKET;
                        break;
                    case "2":
                        state = MenuState.VIEW_BOOKINGS;
                        break;
                    case "l":
                        UI.user = null;
                        state = MenuState.MAIN;
                        break;
                    case "x":
                        UI.user = null;
                        state = MenuState.CLOSED;
                        break;
                    default:
                        UI.setError("Invalid option provided, try again.");
                }
        }
        useState();
    }

    public static boolean confirm_ticket(Seat[] seats) {
        UI.setHeaderPrimaryContent("Sky Cinemas - Confirm Ticket");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("B - Edit Seats.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Ticket Info");
        options.add("___________");
        options.add("Movie Name: " + seats[0].screen.show.movie.name);
        options.add("Show Time: " + TimeUtils.format(seats[0].screen.show.startTime));
        options.add("Screen: " + "Screen-" + seats[0].screen.show.screenID);
        double[] finalCost = new double[3];
        String seatsString = null;
        for (Seat seat : seats) {
            //Add the cost of the seat to the total cost
            double[] cost = Pricing.getTicketCost(seat.screen.show.movie.baseCost, seat.sectionID - 1);
            //Add cost
            finalCost[0] += cost[0];
            //Add GST
            finalCost[1] += cost[1];
            //Add to the seat list string
            if (seatsString == null)
                seatsString = seat.getLabel();
            else
                seatsString += ", " + seat.getLabel();
        }
        finalCost[2] = finalCost[0] + finalCost[1];
        options.add("Selected Seats: " + seatsString);
        options.add("Booking Cost: " + finalCost[0]);
        options.add("GST: " + finalCost[1] + " @" + Pricing.GSTpercent + "%");
        options.add("Total Payable Cost: " + finalCost[2]);
        UI.setPageContent(options); //This is reference based, so any update to the options will reflect in the render without additional calls

        while (true) {
            UI.setFooterContent("Type 'confirm' to confirm the ticket or select an option.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                return true;
            }

            switch (input.toLowerCase()) {
                case "confirm":
                    Ticket ticket = new Ticket(UI.user, seats[0].screen.show, new String[seats.length], finalCost[2]);
                    for (int i = 0; i < seats.length; i++)
                        ticket.seats[i] = seats[i].getLabel();
                    DataStore.saveTicket(ticket);
                    for (Seat seat : seats)
                        seat.state = 2;
                    return true;
                case "b":
                    return false;
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    return true;
                case "x":
                    state = MenuState.CLOSED;
                    return true;
                default:
                    UI.setError("Invalid option provided, select a seat or an option.");
                    break;
            }
        }
    }

    public static boolean seat_selection(Show show) {
        UI.setHeaderPrimaryContent("Sky Cinemas - Select Seats");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("B - Go back to Show Selection.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Selected movie");
        options.add("______________");
        options.add("Name: " + show.movie.name);
        options.add(" ");
        options.add("_____________");
        options.add("Selected show");
        options.add("______________");
        options.add("Time: " + TimeUtils.format(show.startTime));
        options.add(" ");
        options.add("Selected seats will appear highlighted with █");
        options.add("Reserved seats will appear highlighted with -");
        options.add(" ");

        ShowScreenInstance screen = DataStore.getScreenInstance(show);
        int lineCount = 0;
        for (String line : screen.renderSeating()) {
            options.add(line);
            ++lineCount;
        }

        while (true) {
            //Reset options to this to handle coming back from next menu
            UI.setPageContent(options);
            UI.setFooterContent(
                    "Enter the seatID to add/remove seat from the ticket or select an option.\nPress enter to confirm seat selection.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                return true;
            }

            switch (input.toLowerCase()) {
                //When hit enter to confirm
                case Characters.EMPTY_SPACE:
                    if (confirm_ticket(screen.getSelectedSeats())) {
                        screen.clearSelection();
                        return true;
                    }
                    break;
                case "b":
                    screen.clearSelection();
                    return false;
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    screen.clearSelection();
                    return true;
                case "x":
                    state = MenuState.CLOSED;
                    return true;
                default:
                    Seat seat = screen.getSeatByID(input);
                    if (seat != null) {
                        switch (seat.state) {
                            case 0:
                                //Update seat state to selected
                                seat.state = 1;
                                //Update the displayed seats
                                options.removeRange(options.size() - lineCount,
                                        options.size());
                                for (String line : screen.renderSeating())
                                    options.add(line);
                                UI.setError("Seat added to selection.");
                                break;
                            case 1:
                                seat.state = 0;
                                //Update the displayed seats
                                options.removeRange(options.size() - lineCount,
                                        options.size());
                                for (String line : screen.renderSeating())
                                    options.add(line);
                                UI.setError("Seat removed from selection.");
                                break;
                            case 2:
                                UI.setError("Seat is reserved by someone else, try a different one.");
                                break;
                        }
                    } else
                        UI.setError("Invalid option provided, select a seat or an option.");
            }
        }
    }

    public static boolean show_selection(Movie movie) {
        UI.setHeaderPrimaryContent("Sky Cinemas - Select Show");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("D - Select date to filter.");
        options.add("S - Select seat type to filter.");
        options.add("B - Go back to Movie Selection.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Selected movie");
        options.add("______________");
        options.add("Name: " + movie.name);
        options.add(" ");
        options.add("_______________");
        options.add("Filters applied");
        options.add("_______________");
        options.add("Date: ALL"); //13
        options.add("Seat Type: ALL");

        CustomArrayList<Show> fetchedShows = DataStore.getUpcomingShowsArray(movie);
        ArrayList<ArrayList<Show>> pages = Utils.paginate(fetchedShows, maxItemsPerPage);
        Date dateFilter = null;
        int seatFilter = -1;

        int currentPage = 0;
        int additionalElements = renderShowsPage(options, pages, currentPage);

        String pref = null;
        while (true) {
            //Reset options to this to handle coming back from next menu
            UI.setPageContent(options);
            UI.setFooterContent("Select a " + (pref == null ? "show" : pref) + " or select an option.");
            UI.renderView();

            final String input = pref == null ? UI.requestInput() : pref;
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                return true;
            }

            switch (input.toLowerCase()) {
                case "d":
                    pref = "date";
                    break;
                case "s":
                    pref = "seat type";
                    break;
                case "date":
                    clearPage(options, pages, currentPage, additionalElements);
                    currentPage = 0;
                    ArrayList<Date> dates = DataStore.getUpcomingShowsDates(movie);
                    ArrayList<ArrayList<Date>> datePages = Utils.paginate(dates, maxItemsPerPage);
                    additionalElements = renderDatesPage(options, datePages, currentPage);
                    while (pref != null && pref.equals("date")) {
                        UI.renderView();
                        final String dateInput = UI.requestInput();
                        //If the input is null, ie., user hit ctrl+v in console, show app closed screen
                        if (dateInput == null) {
                            state = MenuState.CLOSED;
                            return true;
                        }
                        switch (dateInput.toLowerCase()) {
                            case "b":
                                clearPage(options, datePages, currentPage, additionalElements);
                                currentPage = 0;
                                additionalElements = renderShowsPage(options, pages, currentPage);
                                pref = null;
                                break;
                            case "c":
                                state = MenuState.LOGGED_IN_MAIN;
                                return true;
                            case "x":
                                state = MenuState.CLOSED;
                                return true;
                            case "<":
                                if (currentPage > 0) {
                                    clearPage(options, datePages, currentPage, additionalElements);
                                    --currentPage;
                                    additionalElements = renderDatesPage(options, datePages, currentPage);
                                }
                                break;
                            case ">":
                                if (currentPage < datePages.size() - 1) {
                                    clearPage(options, datePages, currentPage, additionalElements);
                                    ++currentPage;
                                    additionalElements = renderDatesPage(options, datePages, currentPage);
                                }
                            default:
                                if (dateInput.equals(Characters.EMPTY_SPACE)) {
                                    dateFilter = null;
                                    options.set(13, "Date: ALL");
                                    clearPage(options, datePages, currentPage, additionalElements);
                                    pages = Utils.paginate(DataStore
                                            .sortShows(DataStore.sortShows(fetchedShows, dateFilter),
                                                    DataStore.getScreenSections(), seatFilter),
                                            maxItemsPerPage);
                                    additionalElements = renderShowsPage(options, pages, currentPage);
                                    pref = null;
                                } else if (Utils.isValidNumber(dateInput)
                                        && Integer.parseInt(dateInput) <= datePages.get(currentPage).size()
                                        && Integer.parseInt(dateInput) > 0) {
                                    dateFilter = datePages.get(currentPage).get(Integer.parseInt(dateInput) - 1);
                                    options.set(13, "Date: " + TimeUtils.displayFormat_DateOnly.format(dateFilter));
                                    clearPage(options, datePages, currentPage, additionalElements);
                                    pages = Utils.paginate(
                                            DataStore.sortShows(DataStore.sortShows(fetchedShows, dateFilter),
                                                    DataStore.getScreenSections(), seatFilter),
                                            maxItemsPerPage);
                                    additionalElements = renderShowsPage(options, pages, currentPage);
                                    pref = null;
                                } else
                                    UI.setError("Invalid option provided, select a date or select an option.");
                                break;
                        }
                    }
                    break;
                case "seat type":
                    clearPage(options, pages, currentPage, additionalElements);
                    currentPage = 0;
                    int maxSectionIndex = DataStore.getMaxSectionIndex();
                    ArrayList<Integer> seatTypes = new ArrayList<Integer>();
                    for (int i = 0; i <= maxSectionIndex; ++i)
                        seatTypes.add(i);
                    ArrayList<ArrayList<Integer>> seatTypesPages = Utils.paginate(seatTypes, maxItemsPerPage);
                    additionalElements = renderSeatTypesPage(options, seatTypesPages, currentPage);
                    while (pref != null && pref.equals("seat type")) {
                        UI.renderView();
                        final String seatTypeInput = UI.requestInput();
                        //If the input is null, ie., user hit ctrl+v in console, show app closed screen
                        if (seatTypeInput == null) {
                            state = MenuState.CLOSED;
                            return true;
                        }
                        switch (seatTypeInput.toLowerCase()) {
                            case "b":
                                clearPage(options, seatTypesPages, currentPage, additionalElements);
                                currentPage = 0;
                                additionalElements = renderShowsPage(options, pages, currentPage);
                                pref = null;
                                break;
                            case "c":
                                state = MenuState.LOGGED_IN_MAIN;
                                return true;
                            case "x":
                                state = MenuState.CLOSED;
                                return true;
                            case "<":
                                if (currentPage > 0) {
                                    clearPage(options, seatTypesPages, currentPage, additionalElements);
                                    --currentPage;
                                    additionalElements = renderSeatTypesPage(options, seatTypesPages, currentPage);
                                }
                                break;
                            case ">":
                                if (currentPage < pages.size() - 1) {
                                    clearPage(options, seatTypesPages, currentPage, additionalElements);
                                    ++currentPage;
                                    additionalElements = renderSeatTypesPage(options, seatTypesPages, currentPage);
                                }
                            default:
                                if (seatTypeInput.equals(Characters.EMPTY_SPACE)) {
                                    seatFilter = -1;
                                    options.set(14, "Seat Type: ALL");
                                    clearPage(options, seatTypesPages, currentPage, additionalElements);
                                    pages = Utils.paginate(DataStore
                                            .sortShows(DataStore.sortShows(fetchedShows, dateFilter),
                                                    DataStore.getScreenSections(), seatFilter),
                                            maxItemsPerPage);
                                    additionalElements = renderShowsPage(options, pages, currentPage);
                                    pref = null;
                                } else if (Utils.isValidNumber(seatTypeInput)
                                        && Integer.parseInt(seatTypeInput) <= seatTypesPages.get(currentPage).size()
                                        && Integer.parseInt(seatTypeInput) > 0) {
                                    seatFilter = seatTypesPages.get(currentPage)
                                            .get(Integer.parseInt(seatTypeInput) - 1);
                                    options.set(14, "Seat Type: " + Menu.seatTypes[seatFilter]);
                                    clearPage(options, seatTypesPages, currentPage, additionalElements);
                                    pages = Utils.paginate(
                                            DataStore.sortShows(DataStore.sortShows(fetchedShows, dateFilter),
                                                    DataStore.getScreenSections(), seatFilter),
                                            maxItemsPerPage);
                                    additionalElements = renderShowsPage(options, pages, currentPage);
                                    pref = null;
                                } else
                                    UI.setError("Invalid option provided, select a seat type or select an option.");
                                break;
                        }
                    }
                    break;
                case "<":
                    if (currentPage > 0) {
                        clearPage(options, pages, currentPage, additionalElements);
                        --currentPage;
                        additionalElements = renderShowsPage(options, pages, currentPage);
                    }
                    break;
                case ">":
                    if (currentPage < pages.size() - 1) {
                        clearPage(options, pages, currentPage, additionalElements);
                        ++currentPage;
                        additionalElements = renderShowsPage(options, pages, currentPage);
                    }
                    break;
                case "b":
                    return false;
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    return true;
                case "x":
                    state = MenuState.CLOSED;
                    return true;
                default:
                    if (Utils.isValidNumber(input)
                            && Integer.parseInt(input) <= pages.get(currentPage).size()
                            && Integer.parseInt(input) > 0) {
                        if (seat_selection(pages.get(currentPage).get(Integer.parseInt(input) - 1)))
                            return true;
                    } else
                        UI.setError("Invalid option provided, select a show or select an option.");
            }
        }
    }

    private static int renderSeatTypesPage(CustomArrayList<String> options, ArrayList<ArrayList<Integer>> pages,
            int currentPage) {
        options.add(" ");
        options.add("_________________");
        options.add("Available options");
        options.add("_________________");
        int additionalElements = 4;
        if (pages.size() == 0)
            options.add("NO TYPES TO SELECT FROM");
        else {
            for (int i = 0; i < pages.get(currentPage).size(); ++i)
                options.add((i + 1) + " - " + seatTypes[pages.get(currentPage).get(i)]);
            if (pages.size() > 1) {
                options.add(" ");
                options.add("Page " + (currentPage + 1) + " of " + pages.size());
                options.add("Use '<' or '>' to move between pages.");
                additionalElements += 3;
            }
        }
        return additionalElements;
    }

    private static int renderDatesPage(CustomArrayList<String> options, ArrayList<ArrayList<Date>> pages,
            int currentPage) {
        options.add(" ");
        options.add("________________________");
        options.add("Dates for upcoming shows");
        options.add("________________________");
        int additionalElements = 4;
        if (pages.size() == 0)
            options.add("NO DATES TO SELECT FROM");
        else {
            for (int i = 0; i < pages.get(currentPage).size(); ++i)
                options.add((i + 1) + " - " + TimeUtils.displayFormat_DateOnly.format(pages.get(currentPage).get(i)));
            if (pages.size() > 1) {
                options.add(" ");
                options.add("Page " + (currentPage + 1) + " of " + pages.size());
                options.add("Use '<' or '>' to move between pages.");
                additionalElements += 3;
            }
        }
        return additionalElements;
    }

    private static int renderShowsPage(CustomArrayList<String> options, ArrayList<ArrayList<Show>> pages,
            int currentPage) {
        options.add(" ");
        options.add("________________");
        options.add("Available Shows");
        options.add("________________");
        int additionalElements = 4;
        if (pages.size() == 0)
            options.add("NO SHOWS AVAILABLE");
        else {
            for (int i = 0; i < pages.get(currentPage).size(); i++) {
                Show show = pages.get(currentPage).get(i);
                options.add(
                        (i + 1) + " - " + "Time: " + TimeUtils.format(show.startTime) + " - Screen:" + show.screenID);
            }
            if (pages.size() > 1) {
                options.add(" ");
                options.add("Page " + (currentPage + 1) + " of " + pages.size());
                options.add("Use '<' or '>' to move between pages.");
                additionalElements += 3;
            }
        }
        return additionalElements;
    }

    public static void book_ticket() {
        UI.setHeaderPrimaryContent("Sky Cinemas - Book Tickets");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Searching for: "); //3

        CustomArrayList<Movie> fetchedMovies = DataStore.getMoviesArray();
        ArrayList<ArrayList<Movie>> pages = Utils.paginate(fetchedMovies, maxItemsPerPage);
        int currentPage = 0;

        int additionalElements = renderMoviesPage(options, pages, currentPage);

        while (state == MenuState.BOOK_TICKET) {
            //Reset options to this to handle coming back from next menu
            UI.setPageContent(options);
            UI.setFooterContent("Enter a name to search or select an option or a movie to continue.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "<":
                    if (currentPage > 0) {
                        clearPage(options, pages, currentPage, additionalElements);
                        --currentPage;
                        additionalElements = renderMoviesPage(options, pages, currentPage);
                    }
                    break;
                case ">":
                    if (currentPage < pages.size() - 1) {
                        clearPage(options, pages, currentPage, additionalElements);
                        ++currentPage;
                        additionalElements = renderMoviesPage(options, pages, currentPage);
                    }
                    break;
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    break;
                case "x":
                    UI.user = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    if (Utils.isValidNumber(input)
                            && Integer.parseInt(input) <= pages.get(currentPage).size()
                            && Integer.parseInt(input) > 0) {
                        if (show_selection(pages.get(currentPage).get(Integer.parseInt(input) - 1)))
                            state = MenuState.LOGGED_IN_MAIN;
                    } else {
                        clearPage(options, pages, currentPage, additionalElements);
                        options.set(3, "Searching for: " + input);
                        //Create the filtered list
                        pages = Utils.paginate(FuzzySearch.sortMovies(input, fetchedMovies), maxItemsPerPage);
                        currentPage = 0;

                        renderMoviesPage(options, pages, currentPage);
                    }

            }
        }
        useState();
    }

    private static <T> void clearPage(CustomArrayList<String> options, ArrayList<ArrayList<T>> pages,
            int currentPage, int additionalElements) {
        options.removeRange(
                options.size()
                        - (((pages.size() == 0 || pages.get(currentPage).size() == 0) ? 1
                                : pages.get(currentPage).size())
                                + additionalElements),
                options.size());
    }

    private static int renderMoviesPage(CustomArrayList<String> options, ArrayList<ArrayList<Movie>> pages,
            int currentPage) {
        options.add(" ");
        options.add("________________");
        options.add("Available Movies");
        options.add("________________");
        int additionalElements = 4;
        if (pages.size() == 0)
            options.add("NO MOVIES AVAILABLE");
        else {
            for (int i = 0; i < pages.get(currentPage).size(); i++)
                options.add((i + 1) + " - " + pages.get(currentPage).get(i).name);
            if (pages.size() > 1) {
                options.add(" ");
                options.add("Page " + (currentPage + 1) + " of " + pages.size());
                options.add("Use '<' or '>' to move between pages.");
                additionalElements += 3;
            }
        }
        return additionalElements;
    }

    public static void view_bookings() {
        UI.setHeaderPrimaryContent("Sky Cinemas - View Bookings");
        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("B - Back.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");

        ArrayList<Ticket> fetchedTickets = DataStore.getTickets(UI.user);
        ArrayList<ArrayList<Ticket>> pages = Utils.paginate(fetchedTickets, maxItemsPerPage);
        int currentPage = 0;
        int additionalElements = renderTicketsPage(options, pages, currentPage);

        while (state == MenuState.VIEW_BOOKINGS) {
            UI.setPageContent(options);
            UI.setFooterContent("Select an option or a ticket to continue.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "b":
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    break;
                case "<":
                    if (currentPage > 0) {
                        clearPage(options, pages, currentPage, additionalElements);
                        --currentPage;
                        additionalElements = renderTicketsPage(options, pages, currentPage);
                    }
                    break;
                case ">":
                    if (currentPage < pages.size() - 1) {
                        clearPage(options, pages, currentPage, additionalElements);
                        ++currentPage;
                        additionalElements = renderTicketsPage(options, pages, currentPage);
                    }
                default:
                    if (Utils.isValidNumber(input)
                            && Integer.parseInt(input) <= pages.get(currentPage).size()
                            && Integer.parseInt(input) > 0) {
                        view_ticket(pages.get(currentPage).get(Integer.parseInt(input) - 1));
                    } else
                        UI.setError("Invalid input. Please try again.");
            }
        }
        useState();
    }

    public static int renderTicketsPage(CustomArrayList<String> options, ArrayList<ArrayList<Ticket>> pages,
            int currentPage) {
        options.add(" ");
        options.add("_____________");
        options.add("Your Bookings");
        options.add("_____________");
        int additionalElements = 4;
        if (pages.size() == 0)
            options.add("NO BOOKINGS AVAILABLE");
        else {
            for (int i = 0; i < pages.get(currentPage).size(); i++)
                options.add((i + 1) + " - " + pages.get(currentPage).get(i).getSummary());
            if (pages.size() > 1) {
                options.add(" ");
                options.add("Page " + (currentPage + 1) + " of " + pages.size());
                options.add("Use '<' or '>' to move between pages.");
                additionalElements += 3;
            }
        }
        return additionalElements;
    }

    public static boolean view_ticket(Ticket ticket) {
        UI.setHeaderPrimaryContent("Sky Cinemas - View Ticket");
        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("B - Back.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");

        options.add(" ");
        options.add("________________");
        options.add("Ticket Information");
        options.add("________________");
        options.add(" ");
        options.add("Movie: " + ticket.show.movie.name);
        options.add("Date: " + TimeUtils.displayFormat_DateOnly.format(ticket.show.startTime));
        options.add("Time: " + TimeUtils.displayFormat_TimeOnly.format(ticket.show.startTime));
        options.add("Seats: " + String.join(",", ticket.seats));
        options.add("Price: Rs." + ticket.cost);
        options.add(" ");
        options.add("______________");
        options.add("Ticket Barcode");
        options.add("______________");
        options.add(" ");
        final String codeLine = Barcode.generate(ticket.id);
        for (int i = 0; i <= 5; i++)
            options.add(codeLine);
        UI.setPageContent(options);

        loop: while (true) {
            UI.setFooterContent("Select an option to continue.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "b":
                case "c":
                    state = MenuState.LOGGED_IN_MAIN;
                    break loop;
                case "x":
                    state = MenuState.CLOSED;
                    break loop;
                default:
                    UI.setError("Invalid input. Please try again.");
            }
        }
        useState();
        return true;
    }

    public static void admin_menu() {
        while (state == MenuState.ADMIN_MENU) {
            //If for some reason the user is not logged in
            //Force back to the main menu with no login
            if (UI.user == null) {
                state = MenuState.MAIN;
                break;
            }

            UI.setHeaderPrimaryContent("Sky Cinemas - Admin Menu");

            final CustomArrayList<String> options = new CustomArrayList<String>();
            options.add("M - Add a movie.");
            options.add("A - Add a show to listing.");
            options.add("B - Go back to main ");
            options.add("L - Log out.");
            options.add("X - Close app. [warning: deletes all data]");
            UI.setPageContent(options);

            UI.setFooterContent("Select an option.");
            UI.renderView();

            final String input = UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "m":
                    state = MenuState.ADD_MOVIE;
                    break;
                case "a":
                    state = MenuState.ADD_SHOW;
                    break;
                case "b":
                    state = MenuState.LOGGED_IN_MAIN;
                    break;
                case "l":
                    UI.user = null;
                    state = MenuState.MAIN;
                    break;
                case "x":
                    UI.user = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    UI.setError("Invalid option provided, try again.");
            }
        }
        useState();
    }

    public static void add_movie() {
        tempMovie = new Movie();
        UI.setHeaderPrimaryContent("Sky Cinemas - Add Movie");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("N - Edit movie name.");
        options.add("P - Edit base cost.");
        options.add("D - Edit movie streaming duration.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Movie Details");
        options.add("_____________");
        options.add("Name:"); //8
        options.add("Base Cost:"); //9
        options.add("Show duration:"); //10
        UI.setPageContent(options); //This is reference based, so any update to the options will reflect in the render without additional calls

        String pref = null;
        while (state == MenuState.ADD_MOVIE) {
            if (pref == null) {
                if (tempMovie.name == null)
                    pref = "movie name";
                else if (tempMovie.baseCost == 0)
                    pref = "base cost (in rupees as a number)";
                else if (tempMovie.duration == null)
                    pref = "duration (hh:mm)";
                else
                    pref = "confirmation";
            }
            UI.setFooterContent(
                    pref.equals("confirmation")
                            ? "Are you sure you want to add this to the list of available movies.\nEnter y to confirm or select a field to edit."
                            : String.format("Enter %s or select an option.", pref));
            UI.renderView();

            final String input = pref.equals("movie screen") ? "movie screen" : UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "n":
                    pref = "movie name";
                    break;
                case "p":
                    pref = "base cost (in rupees as a number)";
                    break;
                case "d":
                    pref = "duration (hh:mm)";
                    break;
                case "c":
                    tempMovie = null;
                    state = MenuState.ADMIN_MENU;
                    break;
                case "x":
                    UI.user = null;
                    tempMovie = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    switch (pref) {
                        case "movie name":
                            tempMovie.name = input;
                            options.set(8, "Name: " + input);
                            pref = null;
                            break;
                        case "base cost (in rupees as a number)":
                            if (!Utils.isValidNumberWithDecimal(input))
                                UI.setError("Invalid amount entered for base cost, try again.");
                            else {
                                tempMovie.baseCost = Double.parseDouble(input);
                                options.set(9, "Base Cost: " + tempMovie.baseCost);
                                pref = null;
                            }
                            break;
                        case "duration (hh:mm)":
                            tempMovie.duration = TimeUtils.parseDuration(input);
                            if (tempMovie.duration == null)
                                UI.setError("Invalid duration entered, try again.");
                            else {
                                options.set(10, "Show duration: " + TimeUtils.format(tempMovie.duration));
                                pref = null;
                            }
                            break;
                        case "confirmation":
                            if (input.toLowerCase().equals("y")) {
                                DataStore.saveMovie(tempMovie);
                                tempMovie = null;
                                state = MenuState.ADMIN_MENU;
                            }
                            break;
                        default:
                            UI.setError("Invalid option provided, try again.");
                            pref = null;
                    }
            }
        }
        useState();
    }

    public static void add_show() {
        tempShow = new Show();
        UI.setHeaderPrimaryContent("Sky Cinemas - Add Show");

        final CustomArrayList<String> options = new CustomArrayList<String>();
        options.add("N - Edit movie.");
        options.add("S - Edit movie screen.");
        options.add("T - Edit movie streaming start time.");
        options.add("C - Cancel.");
        options.add("X - Close app. [warning: deletes all data]");
        options.add(" ");
        options.add("Show Details");
        options.add("____________");
        options.add("Name: "); //8
        options.add("Screen: "); //9
        options.add("Show Duration: "); //10
        options.add("Show Start Time: "); //11
        UI.setPageContent(options);

        String pref = null;
        while (state == MenuState.ADD_SHOW) {
            if (pref == null) {
                if (tempShow.movie == null)
                    pref = "movie name";
                else if (tempShow.screenID == 0)
                    pref = "show screen";
                else if (tempShow.startTime == null)
                    pref = "start time (hh:mm dd/mm/yyyy in 24 hour format)";
                else
                    pref = "confirmation";
            }

            UI.setFooterContent(
                    pref.equals("confirmation")
                            ? "Are you sure you want to add this to the list of available shows.\nEnter y to confirm or select a field to edit."
                            : String.format("Enter %s or select an option.", pref));

            UI.renderView();

            final String input = pref.equals("movie name") || pref.equals("show screen") ? pref
                    : UI.requestInput();
            //If the input is null, ie., user hit ctrl+v in console, show app closed screen
            if (input == null) {
                state = MenuState.CLOSED;
                break;
            }

            switch (input.toLowerCase()) {
                case "n":
                    pref = "movie name";
                    break;
                case "s":
                    pref = "show screen";
                    break;
                case "t":
                    pref = "start time (hh:mm dd/mm/yyyy in 24 hour format)";
                    break;
                case "c":
                    tempShow = null;
                    state = MenuState.ADMIN_MENU;
                    break;
                case "x":
                    UI.user = null;
                    tempShow = null;
                    state = MenuState.CLOSED;
                    break;
                default:
                    boolean exit = false;
                    switch (pref) {
                        case "movie name":
                            CustomArrayList<Movie> movies = DataStore.getMoviesArray();

                            options.add(" ");
                            options.add("Available Movies");
                            options.add("________________");
                            if (movies.size() == 0)
                                options.add("NO MOVIES AVAILABLE");

                            for (int i = 0; i < movies.size(); i++)
                                options.add((i + 1) + " - " + movies.get(i).name);

                            while (!exit) {
                                UI.setFooterContent("Select a movie or select an option.");
                                UI.renderView();

                                final String movieInput = UI.requestInput();
                                //If the input is null, ie., user hit ctrl+v in console, show app closed screen
                                if (input == null) {
                                    state = MenuState.CLOSED;
                                    break;
                                }

                                if (Utils.isValidNumber(movieInput)
                                        && Integer.parseInt(movieInput) <= movies.size()
                                        && Integer.parseInt(movieInput) > 0) {
                                    tempShow.movie = movies.get(Integer.parseInt(movieInput) - 1);
                                    //Remove movie selection options
                                    options.removeRange(options.size() - (movies.size() == 0 ? 1 : movies.size() + 3),
                                            options.size());

                                    if (tempShow.startTime != null) {
                                        Show overlappingShow = DataStore.getOverlappingShow(tempShow.startTime,
                                                tempShow.movie.duration);
                                        if (overlappingShow != null) {
                                            UI.setError(String.format(
                                                    "This show overlaps another show which starts at %s and ends at %s",
                                                    TimeUtils.format(overlappingShow.startTime),
                                                    TimeUtils.format(new Date(overlappingShow.startTime.getTime()
                                                            + overlappingShow.movie.duration.toMillis()))));
                                            tempShow.movie = null;
                                            continue; //Break so that the pref is not set to null
                                        }
                                    }

                                    //Display the selected movie
                                    options.set(8, "Name: " + tempShow.movie.name);
                                    options.set(10, "Show Duration: " + TimeUtils.format(tempShow.movie.duration));
                                    pref = null;
                                    exit = true;
                                } else {
                                    pref = null;
                                    switch (movieInput.toLowerCase()) {
                                        case "n":
                                            pref = "movie name";
                                            exit = true;
                                            break;
                                        case "s":
                                            pref = "show screen";
                                            exit = true;
                                            break;
                                        case "t":
                                            pref = "start time (hh:mm dd/mm/yyyy in 24 hour format)";
                                            exit = true;
                                            break;
                                        case "c":
                                            tempShow = null;
                                            state = MenuState.ADMIN_MENU;
                                            exit = true;
                                            break;
                                        case "x":
                                            UI.user = null;
                                            tempShow = null;
                                            state = MenuState.CLOSED;
                                            exit = true;
                                            break;
                                        default:
                                            UI.setError(
                                                    "Invalid option provided, select a movie or select an option.");
                                    }
                                }
                            }
                            break;
                        case "show screen":
                            CustomArrayList<Integer> screenIDs = DataStore.getScreenIDs();

                            options.add(" ");
                            options.add("Available Screens");
                            options.add("_________________");
                            if (screenIDs.size() == 0)
                                options.add("NO SCREENS AVAILABLE");

                            for (int i = 0; i < screenIDs.size(); i++)
                                options.add(String.format((i + 1) + " - " + "Screen-%d",
                                        screenIDs.get(i)));

                            while (!exit) {
                                UI.setFooterContent("Select a screen or select an option.");
                                UI.renderView();

                                final String screenInput = UI.requestInput();
                                //If the input is null, ie., user hit ctrl+v in console, show app closed screen
                                if (input == null) {
                                    state = MenuState.CLOSED;
                                    break;
                                }

                                if (Utils.isValidNumber(screenInput)
                                        && Integer.parseInt(screenInput) <= screenIDs.size()
                                        && Integer.parseInt(screenInput) > 0) {
                                    tempShow.screenID = screenIDs.get(Integer.parseInt(screenInput) - 1);
                                    options.removeRange(
                                            options.size() - (screenIDs.size() == 0 ? 1 : screenIDs.size() + 3),
                                            options.size());
                                    options.set(9, "Screen: Screen-" + Integer.toString(tempShow.screenID));
                                    pref = null;
                                    exit = true;
                                } else {
                                    pref = null;
                                    switch (screenInput.toLowerCase()) {
                                        case "n":
                                            pref = "movie name";
                                            exit = true;
                                            break;
                                        case "s":
                                            pref = "show screen";
                                            exit = true;
                                            break;
                                        case "t":
                                            pref = "start time (hh:mm dd/mm/yyyy in 24 hour format)";
                                            exit = true;
                                            break;
                                        case "c":
                                            tempShow = null;
                                            state = MenuState.ADMIN_MENU;
                                            exit = true;
                                            break;
                                        case "x":
                                            UI.user = null;
                                            tempShow = null;
                                            state = MenuState.CLOSED;
                                            exit = true;
                                            break;
                                        default:
                                            UI.setError(
                                                    "Invalid option provided, select a screen or select an option.");
                                    }
                                }
                            }
                            break;
                        case "start time (hh:mm dd/mm/yyyy in 24 hour format)":
                            tempShow.startTime = TimeUtils.parse(input);
                            if (tempShow.startTime == null)
                                UI.setError("Invalid time entered, try again.");
                            else {
                                if (System.currentTimeMillis() > tempShow.startTime.getTime()) {
                                    UI.setError("The show cannot start in the past :/" + "\n" + "Current time: "
                                            + TimeUtils.format(new Date(System.currentTimeMillis()))
                                            + "\n" + "Entered Show time: " + TimeUtils.format(tempShow.startTime));
                                    tempShow.startTime = null;
                                    break;
                                }
                                if (tempShow.movie != null) {
                                    Show overlappingShow = DataStore.getOverlappingShow(tempShow.startTime,
                                            tempShow.movie.duration);
                                    if (overlappingShow != null) {
                                        UI.setError(String.format(
                                                "This show overlaps another show which starts at %s and ends at %s",
                                                TimeUtils.format(overlappingShow.startTime),
                                                TimeUtils.format(new Date(overlappingShow.startTime.getTime()
                                                        + overlappingShow.movie.duration.toMillis()))));
                                        tempShow.startTime = null;
                                        break; //Break so that the pref is not set to null
                                    }
                                }
                                pref = null;
                                options.set(11, "Show Start Time: " + TimeUtils.format(tempShow.startTime));
                            }
                            break;
                        case "confirmation":
                            if (input.toLowerCase().equals("y")) {
                                DataStore.saveShow(tempShow);
                                tempShow = null;
                                state = MenuState.ADMIN_MENU;
                            }
                            break;
                        default:
                            UI.setError("Invalid option provided, try again.");
                            pref = null;
                    }
            }
        }
        useState();
    }

    /**
     * APP CLOSED SCREEN
     */
    public static void closed() {
        UI.setHeaderPrimaryContent("Sky Cinemas");
        UI.setPageContent("APP CLOSED");
        UI.setFooterContent("Thanks for using this app!");
        UI.renderView();
        System.exit(0);
    }
}
