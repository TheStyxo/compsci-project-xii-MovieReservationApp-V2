package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

//import codes.styxo.school.projects.SkyCinemas.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Utils.UserSearchKeyType;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;

public class User extends Base {
    //Userame of the user
    public String username;
    //First Name of the user
    public String firstName;
    //Last Name of the user
    public String lastName;
    //Email of the user
    public String email;
    //Phone number of the user
    public String phone;
    //Password of the user
    public String password;
    //Is this user an admin?
    public boolean isAdmin = false;

    //The constructor for creating blank instances of users
    public User() {
        super();
    }

    //Constructor to load stored data from file
    public User(String data) {
        String[] d = Serializer.getStringArray(data);

        this.id = d[0];
        this.username = d[1];
        this.firstName = d[2];
        this.lastName = d[3];
        this.email = d[4];
        this.phone = d[5];
        this.password = d[6];
        this.isAdmin = Boolean.parseBoolean(d[7]);
    }

    //Check is the user data is complete
    public Boolean isComplete() {
        return !(username == null || firstName == null || lastName == null || email == null || phone == null
                || password == null);
    }

    //Convert the data to a string that can be stored and converted back to a user object later
    public String serialize() {
        String[] d = {
                this.id,
                this.username,
                this.firstName,
                this.lastName,
                this.email,
                this.phone,
                this.password,
                this.isAdmin ? "true" : "false"
        };

        return Serializer.getString(d);
    }

    //Method to match a field of the user on serialized data
    public static boolean matchField(String serializedData, UserSearchKeyType key, String value) {
        String[] d = Serializer.getStringArray(serializedData);

        switch (key) {
            case ID:
                return d[0].equals(value);
            case PHONE:
                return d[5].equals(value);
            case EMAIL:
                return d[4].equals(value);
            case USERNAME:
                return d[1].equals(value);
            default:
                return false;
        }
    }

    //Method to match a field of the user on the object
    public boolean matchField(UserSearchKeyType key, String value) {
        switch (key) {
            case ID:
                return this.id.equals(value);
            case PHONE:
                return this.phone.equals(value);
            case EMAIL:
                return this.email.equals(value);
            case USERNAME:
                return this.username.equals(value);
            default:
                return false;
        }
    }

    //Get tickets the user has purchased by looping through movies
    // public Ticket[] getTicketHistory() {

    //     Ticket[] tickets = new Ticket[0];

    //     //There are better ways to do this but I am kinda out of time so ummm sorry for your eyes
    //     for (Movie movie : DataStore.movies)
    //         for (Row row : movie.screen.rows)
    //             for (Seat seat : row.seats)
    //                 if (seat.ticket != null && seat.ticket.user.id.equals(this.id)) {
    //                     Ticket[] newTickets = new Ticket[tickets.length + 1];
    //                     System.arraycopy(tickets, 0, newTickets, 0, tickets.length);
    //                     newTickets[tickets.length] = seat.ticket;
    //                     tickets = newTickets;
    //                 }
    //     return tickets;
    // }
}
