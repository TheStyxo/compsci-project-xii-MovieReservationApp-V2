package codes.styxo.school.projects.SkyCinemasV2.Data;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Date;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.nio.file.*;

import codes.styxo.school.projects.SkyCinemasV2.App;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Movie;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Show;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.ShowScreenInstance;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Ticket;
import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.User;
import codes.styxo.school.projects.SkyCinemasV2.Utils.CustomArrayList;
import codes.styxo.school.projects.SkyCinemasV2.Utils.FuzzySearch;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;
import codes.styxo.school.projects.SkyCinemasV2.Utils.UserSearchKeyType;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Utils;
import codes.styxo.school.projects.SkyCinemasV2.Utils.TimeUtils;

//A class that has methods to handle data storage and retrival
//Methods can be changed later to change how the data is stored
public class DataStore {
    private static String[] sampleMovieNames = {
            "Sita Ramam",
            "Spider-Man: No Way Home",
            "Brahmastra Part One: Shiva",
            "Jahaan Chaar Yaar",
            "Saroj Ka Rishta",
            "Roop Nagar Ke Cheetey",
            "Moh",
            "Matto Ki Saikil",
            "Boyz 3",
            "Kabir Singh",
            "Dil Bechara",
            "The Conjuring: The Devil Made Me Do It",
            "Top Gun: Maverick"
    };

    public static void deleteData(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
        }
    }

    public static void replaceWithSampleData() {
        //Delete existing data
        deleteData(IDGenIncFile);
        deleteData(usersFile);
        deleteData(moviesFile);
        deleteData(showsFile);
        deleteData(ticketsFile);

        User admin = new User();
        admin.username = "admin";
        admin.firstName = "Admin";
        admin.lastName = "";
        admin.email = "admin@example.com";
        admin.phone = "0000000000";
        admin.password = "admin";
        admin.isAdmin = true;
        DataStore.saveUser(admin);

        User sampleUser = new User();
        sampleUser.username = "sample";
        sampleUser.firstName = "Sample";
        sampleUser.lastName = "User";
        sampleUser.email = "sample@example.com";
        sampleUser.phone = "1234567890";
        sampleUser.password = "1234";
        DataStore.saveUser(sampleUser);

        User sampleUser2 = new User();
        sampleUser2.username = "sample2";
        sampleUser2.firstName = "Sample2";
        sampleUser2.lastName = "User";
        sampleUser2.email = "sample2@example.com";
        sampleUser2.phone = "0123456789";
        sampleUser2.password = "1234";
        DataStore.saveUser(sampleUser2);

        for (String name : sampleMovieNames) {
            Movie sampleMovie = new Movie();
            //Name form sample list
            sampleMovie.name = name;
            //Random cost between 100 and 200
            sampleMovie.baseCost = Math.random() * 100 + 100;
            //Radom Duration between 1 and 3 hours
            sampleMovie.duration = java.time.Duration.ofHours((long) (Math.random() * 2 + 1));
            DataStore.saveMovie(sampleMovie);

            final long hour = 3600000;
            final long day = 24 * hour;

            //For each screen add a show for the entire week,
            //one show per day for each movie
            //These will overlap because it is sample data, 
            //but it is not a problem, since actual shows cannot overlap when entered by admin
            for (int screenID : DataStore.getScreenIDs()) {
                for (int i = 0; i <= 7; i++) {
                    Show sampleShow = new Show();
                    sampleShow.screenID = screenID;
                    sampleShow.movie = sampleMovie;
                    //Start time is 1 hour from now and then 1 hour from next day and so on
                    sampleShow.startTime = new Date(new Date().getTime() + (hour + day * i));
                    DataStore.saveShow(sampleShow);
                    String[] seats = { "A01", "A02", "A03", "B01", "B02", "B03", "C01", "C02", "C03" };
                    DataStore.saveTicket(new Ticket(sampleUser, sampleShow, seats, 300));
                }
            }
        }

    }

    /** ID Generator Inc Data */
    private static final String IDGenIncFile = "codes/styxo/school/projects/SkyCinemasV2/appdata/"
            + (App.demoMode ? "demo-" : "") + "inc.txt";
    private static long IDGenCache = -1;

    /** Users Data */
    private static final String usersFile = "codes/styxo/school/projects/SkyCinemasV2/appdata/"
            + (App.demoMode ? "demo-" : "") + "users.txt";
    //Cached users to avoid creating multiple instances of the same user
    private static final CustomArrayList<User> cachedUsers = new CustomArrayList<User>();

    /** Movies Data */
    private static final String moviesFile = "codes/styxo/school/projects/SkyCinemasV2/appdata/"
            + (App.demoMode ? "demo-" : "") + "movies.txt";
    //Cached movies to avoid creating multiple instances of the same movie
    private static final CustomArrayList<Movie> cachedMovies = new CustomArrayList<Movie>();

    /** Shows Data */
    private static final String showsFile = "codes/styxo/school/projects/SkyCinemasV2/appdata/"
            + (App.demoMode ? "demo-" : "") + "shows.txt";
    //Cached shows to avoid creating multiple instances of the same show
    private static final CustomArrayList<Show> cachedShows = new CustomArrayList<Show>();

    /** Tickets Data */
    private static final String ticketsFile = "codes/styxo/school/projects/SkyCinemasV2/appdata/"
            + (App.demoMode ? "demo-" : "") + "tickets.txt";
    //Cached tickets to avoid creating multiple instances of the same ticket
    private static final CustomArrayList<Ticket> cachedTickets = new CustomArrayList<Ticket>();

    /** Screens config Data */
    public static final String ScreensConfigFile = "codes/styxo/school/projects/SkyCinemasV2/config/Screens.txt";
    private static final CustomArrayList<Integer> screenIDs = new CustomArrayList<Integer>();
    private static final CustomArrayList<int[]> screenSectionsPerScreen = new CustomArrayList<int[]>();

    /** Show Screen Instancce Data */
    private static final CustomArrayList<ShowScreenInstance> cachedScreens = new CustomArrayList<ShowScreenInstance>();

    /** Method to get the screens from config to display options */
    public static CustomArrayList<Integer> getScreenIDs() {
        if (screenIDs.size() == 0) {
            try {
                BufferedReader configFileInput = new BufferedReader(new FileReader(DataStore.ScreensConfigFile));
                String currentLine = configFileInput.readLine();

                while (currentLine != null) {
                    //If the current line is reading the top of the config for the current screen, add it to arraylist
                    if (currentLine.startsWith("Screen-")) {
                        screenIDs.add(Integer.parseInt(currentLine.split("\\|")[0].split("-")[1]));
                    }

                    //Go to next line
                    currentLine = configFileInput.readLine();
                }
                configFileInput.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }

        return screenIDs;
    }

    /** Method to get the max level of section from config to display options */
    public static int getMaxSectionIndex() {
        int max = 0;
        for (int[] sections : getScreenSections())
            max = Math.max(max, sections[1]);
        return max - 1;
    }

    /** Method to get the sections from config to display options */
    public static CustomArrayList<int[]> getScreenSections() {
        if (screenSectionsPerScreen.size() == 0) {
            try {
                BufferedReader configFileInput = new BufferedReader(new FileReader(DataStore.ScreensConfigFile));
                String currentLine = configFileInput.readLine();

                //If the reading has started for the schema
                boolean readStart = false;
                //If seats were read in the previous line
                boolean prevLineSeatsRead = false;

                while (currentLine != null) {
                    //If the current line is reading the top of the config for the current screen, start reading the rest of the file
                    if (!readStart) {
                        if (currentLine.startsWith("Screen-")) {
                            int[] arr = new int[2];
                            arr[0] = Integer.parseInt(currentLine.split("\\|")[0].split("-")[1]);
                            arr[1] = 0;
                            screenSectionsPerScreen.add(arr);
                            readStart = true;
                        }
                    } else {
                        if (currentLine.length() == 0) {
                            //Go to next line
                            currentLine = configFileInput.readLine();
                            readStart = false;
                            continue;
                        }

                        //Count the number of seats in this line (%s)
                        int seatCount = Utils.countOccurances(currentLine, "%s");

                        //Switch the section if the seats have ended in the current one
                        if (prevLineSeatsRead && seatCount == 0) {
                            ++screenSectionsPerScreen.get(screenSectionsPerScreen.size() - 1)[1];
                            prevLineSeatsRead = false;
                        }

                        //If there are seats in this line, prevLineSeatsRead is true
                        if (seatCount != 0)
                            prevLineSeatsRead = true;
                    }

                    //Go to next line
                    currentLine = configFileInput.readLine();
                }
                configFileInput.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
        for (int[] arr : screenSectionsPerScreen)
            System.out.println(arr[0] + " - " + arr[1]);
        return screenSectionsPerScreen;
    }

    /** Method to get screen instance for a show */
    public static ShowScreenInstance getScreenInstance(Show show) {
        for (ShowScreenInstance screen : cachedScreens)
            if (screen.show.id.equals(show.id))
                return screen;
        return cachedScreens.customAdd(new ShowScreenInstance(show));
    }

    /** File handling to store persistent data
     *  Methods to write data to files
    */
    /**
     * Get the inc value for IDs from file
     * Store in file to avoid duplicate id generation on restarts
     * @return long
     */
    public static String genID() {
        if (IDGenCache == -1) {
            try {
                BufferedReader IDGenIncFileIntput = new BufferedReader(new FileReader(IDGenIncFile));
                String firstLine = IDGenIncFileIntput.readLine();
                IDGenIncFileIntput.close();
                IDGenCache = Long.valueOf(firstLine);
            } catch (IOException e) {
                //If file doesn't exist set cache to 0
                IDGenCache = 0;
            }
        }

        //The next id is the currently stored ID in the cache and file
        try {
            BufferedWriter IDGenIncFileOutput = new BufferedWriter(new FileWriter(IDGenIncFile));
            IDGenIncFileOutput.write(String.valueOf(IDGenCache + 1));
            IDGenIncFileOutput.close();
        } catch (IOException e) {
            System.out.println("Internal Error: " + e);
        }

        return String.valueOf(IDGenCache++);
    }

    /**
     * Save user to file
     * @param user The User to save to the file
     * @return User
     */
    public static User saveUser(User user) {
        try {
            BufferedWriter usersFileOutput = new BufferedWriter(new FileWriter(usersFile, true));
            usersFileOutput.append(user.serialize() + "\n");
            usersFileOutput.close();

            //Save to cached users
            cachedUsers.add(user);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return user;
    }

    /**
     * Save movie to file
     * @param movie The Movie to save to the file
     * @return Movie
     */
    public static Movie saveMovie(Movie movie) {
        try {
            BufferedWriter moviesFileOutput = new BufferedWriter(new FileWriter(moviesFile, true));
            moviesFileOutput.append(movie.serialize() + "\n");
            moviesFileOutput.close();

            //Save to cached movies
            cachedMovies.add(movie);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return movie;
    }

    /**
     * Save show to file
     * @param show The Show to save to the file
     * @return Show
     */
    public static Show saveShow(Show show) {
        try {
            BufferedWriter showsFileOutput = new BufferedWriter(new FileWriter(showsFile, true));
            showsFileOutput.append(show.serialize() + "\n");
            showsFileOutput.close();

            //Save to cached shows
            cachedShows.add(show);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return show;
    }

    /**
     * Save ticket to file
     * @param ticket The Ticket to save to the file
     * @return Ticket
     */
    public static Ticket saveTicket(Ticket ticket) {
        try {
            BufferedWriter ticketsFileOutput = new BufferedWriter(new FileWriter(ticketsFile, true));
            ticketsFileOutput.append(ticket.serialize() + "\n");
            ticketsFileOutput.close();

            //Save to cached movies
            cachedTickets.add(ticket);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return ticket;
    }

    /**Methods to fetch data from files
     */

    //Search a user using a key in the file
    public static User getUser(String value, UserSearchKeyType key) {
        //Search through cache first
        for (User user : cachedUsers)
            if (user.matchField(key, value))
                return user;

        //If the user is not in cache then search the file
        try {
            BufferedReader usersFileInput = new BufferedReader(new FileReader(usersFile));
            String currentLine = usersFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the user with the provided id is found in file
                //Convert it to a user object and return it
                if (User.matchField(currentLine, key, value)) {
                    usersFileInput.close();
                    return cachedUsers.customAdd(new User(currentLine));
                }

                //If not found, continue finding in the next line
                currentLine = usersFileInput.readLine();
            }
            usersFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return null;
    }

    //Search a show that overlaps the time frame
    public static Show getOverlappingShow(Date startTime, Duration duration) {
        long startMs = startTime.getTime();
        long endMs = startTime.getTime() + duration.toMillis();

        //Search through cache first
        for (Show show : cachedShows)
            if (TimeUtils.checkOverlap(startMs, endMs, show.startTime.getTime(),
                    show.startTime.getTime() + show.movie.duration.toMillis()))
                return show;

        //If the show is not in cache then search the file
        try {
            BufferedReader showsFileInput = new BufferedReader(new FileReader(showsFile));
            String currentLine = showsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                Show show = new Show(currentLine);
                long showStartMs = show.startTime.getTime();
                long showEndMs = showStartMs + show.movie.duration.toMillis();
                //If the show is found in file
                //Convert it to a show object and return it
                if (TimeUtils.checkOverlap(startMs, endMs, showStartMs, showEndMs)) {
                    showsFileInput.close();
                    //Add to cache and return
                    return cachedShows.customAdd(show);
                }

                //If not found, continue finding in the next line
                currentLine = showsFileInput.readLine();
            }
            showsFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return null;
    }

    //Search a show using id in the file
    public static Show getShow(String id, boolean cacheOnly) {
        //Search through cache first
        for (Show show : cachedShows)
            if (show.id.equals(id))
                return show;

        return null;
    }

    public static Show getShow(String id) {
        //Search through cache first
        for (Show show : cachedShows)
            if (show.id.equals(id))
                return show;

        //If the movie is not in cache then search the file
        try {
            BufferedReader showsFileInput = new BufferedReader(new FileReader(showsFile));
            String currentLine = showsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);
                if (d[0].equals(id)) {
                    showsFileInput.close();
                    return cachedShows.customAdd(new Show(currentLine));
                }

                //If not found, continue finding in the next line
                currentLine = showsFileInput.readLine();
            }
            showsFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return null;
    }

    //Search a movie using id in the file
    public static Movie getMovie(String id, boolean cacheOnly) {
        //Search through cache first
        for (Movie movie : cachedMovies)
            if (movie.id.equals(id))
                return movie;

        return null;
    }

    public static Movie getMovie(String id) {
        //Search through cache first
        for (Movie movie : cachedMovies)
            if (movie.id.equals(id))
                return movie;

        //If the movie is not in cache then search the file
        try {
            BufferedReader moviesFileInput = new BufferedReader(new FileReader(moviesFile));
            String currentLine = moviesFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);
                if (d[0].equals(id)) {
                    moviesFileInput.close();
                    return cachedMovies.customAdd(new Movie(currentLine));
                }

                //If not found, continue finding in the next line
                currentLine = moviesFileInput.readLine();
            }
            moviesFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return null;
    }

    public static Ticket getTicket(String id) {
        //Search through cache first
        for (Ticket ticket : cachedTickets)
            if (ticket.id.equals(id))
                return ticket;

        //If the movie is not in cache then search the file
        try {
            BufferedReader ticketsFileInput = new BufferedReader(new FileReader(ticketsFile));
            String currentLine = ticketsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);
                if (d[0].equals(id)) {
                    ticketsFileInput.close();
                    return cachedTickets.customAdd(new Ticket(currentLine));
                }

                //If not found, continue finding in the next line
                currentLine = ticketsFileInput.readLine();
            }
            ticketsFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return null;
    }

    public static String getReservedShowSeats(Show show) {
        String res = "";
        //If the movie is not in cache then search the file
        try {
            BufferedReader ticketsFileInput = new BufferedReader(new FileReader(ticketsFile));
            String currentLine = ticketsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);
                if (d[2].equals(show.id))
                    for (String seatID : d[3].split(":"))
                        res += seatID;

                //Continue finding in the next line
                currentLine = ticketsFileInput.readLine();
            }
            ticketsFileInput.close();
            return res;
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return res;
    }

    public static ArrayList<Ticket> getTickets(User user) {
        ArrayList<Ticket> res = new ArrayList<Ticket>();
        //If the movie is not in cache then search the file
        try {
            BufferedReader ticketsFileInput = new BufferedReader(new FileReader(ticketsFile));
            String currentLine = ticketsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);
                if (d[1].equals(user.id))
                    res.add(getTicket(d[0]));

                //Continue finding in the next line
                currentLine = ticketsFileInput.readLine();
            }
            ticketsFileInput.close();
            return res;
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        for (int i = 0; i < res.size() - 1; i++) {
            int index = i;
            for (int j = i + 1; j < res.size(); j++) {
                if (res.get(j).show.startTime.getTime() > res.get(index).show.startTime.getTime()) {
                    index = j;//searching for highest index  
                }
            }
            Ticket temp = res.get(index);
            res.set(index, res.get(i));
            res.set(i, temp);
        }

        //If not found return null
        return res;
    }

    public static CustomArrayList<Show> getShowsArray() {
        //If the movie is not in cache then search the file
        try {
            BufferedReader showsFileInput = new BufferedReader(new FileReader(showsFile));
            String currentLine = showsFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);

                //Search through cache if instance already exists or else write it to cached array
                if (getShow(d[0], true) == null)
                    cachedShows.add(new Show(currentLine));

                //Continue to the next line
                currentLine = showsFileInput.readLine();
            }
            showsFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        sortShows(cachedShows);
        return cachedShows;
    }

    public static CustomArrayList<Show> getUpcomingShowsArray(Movie movie) {
        CustomArrayList<Show> filteredList = new CustomArrayList<Show>();
        Date now = new Date();
        for (Show show : getShowsArray())
            if (show.movie.id.equals(movie.id) && show.startTime.after(now))
                filteredList.add(show);
        sortShows(filteredList);
        return filteredList;
    }

    public static ArrayList<Date> getUpcomingShowsDates(Movie movie) {
        ArrayList<Date> res = new ArrayList<Date>();
        outer: for (Show show : getUpcomingShowsArray(movie)) {
            for (Date date : res)
                if (show.startTime.toInstant().truncatedTo(ChronoUnit.DAYS)
                        .equals(date.toInstant().truncatedTo(ChronoUnit.DAYS)))
                    continue outer;
            res.add(new Date(show.startTime.toInstant().truncatedTo(ChronoUnit.DAYS).getEpochSecond() * 1000));
        }
        return res;
    }

    public static void sortShows(CustomArrayList<Show> shows) {
        for (int i = 0; i < shows.size() - 1; i++) {
            int index = i;
            for (int j = i + 1; j < shows.size(); j++) {
                if (shows.get(j).startTime.getTime() < shows.get(index).startTime.getTime()) {
                    index = j;//searching for lowest index  
                }
            }
            Show temp = shows.get(index);
            shows.set(index, shows.get(i));
            shows.set(i, temp);
        }
    }

    public static CustomArrayList<Show> sortShows(CustomArrayList<Show> shows, ArrayList<int[]> screenSections,
            int sectionIndex) {
        if (sectionIndex == -1)
            return shows;
        CustomArrayList<Show> sortedShows = new CustomArrayList<Show>();
        for (Show show : shows) {
            for (int[] section : screenSections) {
                if (section[0] == show.screenID && section[1] - 1 >= sectionIndex)
                    sortedShows.add(show);
            }
        }
        return sortedShows;
    }

    public static CustomArrayList<Show> sortShows(CustomArrayList<Show> shows, Date date) {
        if (date == null)
            return shows;
        long[][] showsTimeRanking = new long[shows.size()][2];
        for (int i = 0; i < shows.size(); i++) {
            showsTimeRanking[i][0] = i;
            //If the show is on previous date than entered, rank below 0 and later remove
            showsTimeRanking[i][1] = shows.get(i).startTime.getTime() < date.getTime() ? -1
                    : shows.get(i).startTime.getTime() - date.getTime();
        }

        FuzzySearch.selectionSort(showsTimeRanking, true);
        CustomArrayList<Show> sortedShows = new CustomArrayList<Show>();
        for (long[] rank : showsTimeRanking) {
            //Add only future shows in the final result
            if (rank[1] != -1)
                sortedShows.add(shows.get((int) rank[0]));
        }

        return sortedShows;
    }

    public static CustomArrayList<Movie> getMoviesArray() {
        //If the movie is not in cache then search the file
        try {
            BufferedReader moviesFileInput = new BufferedReader(new FileReader(moviesFile));
            String currentLine = moviesFileInput.readLine();
            while (currentLine != null && currentLine.length() != 0) {
                //If the movie with the provided id is found in file
                //Convert it to a user object and return it
                String[] d = Serializer.getStringArray(currentLine);

                //Search through cache if instance already exists or else write it to cached array
                if (getMovie(d[0], true) == null)
                    cachedMovies.add(new Movie(currentLine));

                //Continue to the next line
                currentLine = moviesFileInput.readLine();
            }
            moviesFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //If not found return null
        return cachedMovies;
    }
}
