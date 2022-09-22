package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

public class Seat {
    public final ShowScreenInstance screen;
    final int seatID;
    final char rowID;
    public final int sectionID;
    final String[] labels;
    //0 for available
    //1 for selected
    //2 for reserved
    public int state = 0;

    Seat(ShowScreenInstance screen, int seatID, char rowID, int sectionID, String reservedSeatsString) {
        this.screen = screen;
        this.seatID = seatID;
        this.rowID = rowID;
        this.sectionID = sectionID;
        this.labels = new String[3];
        labels[0] = rowID + "0".repeat(screen.labelLength - Integer.toString(seatID).length() - 1) + seatID;
        labels[1] = "█".repeat(labels[0].length());
        labels[2] = "-".repeat(labels[0].length());
        if (reservedSeatsString.indexOf(labels[0]) != -1)
            state = 2;
    }

    public String getLabel() {
        return labels[0];
    }

    public String render() {
        return labels[state];
    }
}
