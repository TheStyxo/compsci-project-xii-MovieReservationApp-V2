package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

import java.util.Date;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;
import codes.styxo.school.projects.SkyCinemasV2.Utils.TimeUtils;
import codes.styxo.school.projects.SkyCinemasV2.Utils.UserSearchKeyType;

public class Ticket extends Base {
    public final User user;
    public final Show show;
    public final String[] seats;
    public final double cost;

    //The constructor for creating blank instances
    public Ticket(User user, Show show, String[] seats, double cost) {
        super();
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.cost = cost;
    }

    //Constructor to load stored data from file
    public Ticket(String data) {
        String[] d = Serializer.getStringArray(data);

        this.id = d[0];
        this.user = DataStore.getUser(d[1], UserSearchKeyType.ID);
        this.show = DataStore.getShow(d[2]);
        this.seats = d[3].split(":");
        this.cost = Double.parseDouble(d[4]);
    }

    //Convert the data to a string that can be stored and converted back to a object later
    public String serialize() {
        String[] d = {
                this.id,
                String.valueOf(this.user.id),
                this.show.id,
                String.join(":", this.seats),
                String.valueOf(this.cost)
        };

        return Serializer.getString(d);
    }

    public String getSummary() {
        return String.format("Ticket ID: %s | %s - %s | Seats: %s | %s", id, show.movie.name,
                TimeUtils.format(show.startTime),
                String.join(", ", seats), (show.startTime.getTime() < new Date().getTime() ? "ENDED" : "UPCOMING"));
    }
}
