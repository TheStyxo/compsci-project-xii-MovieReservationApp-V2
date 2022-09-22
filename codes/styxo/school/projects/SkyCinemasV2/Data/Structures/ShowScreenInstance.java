package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Utils;

public class ShowScreenInstance {
    public int labelLength = 0;
    public final ArrayList<ArrayList<ArrayList<Seat>>> sections = new ArrayList<ArrayList<ArrayList<Seat>>>();
    public Show show;
    private String schema = null;

    public String[] renderSeating() {
        ArrayList<String> labels = new ArrayList<String>();
        for (ArrayList<ArrayList<Seat>> section : sections)
            for (ArrayList<Seat> row : section)
                for (Seat seat : row)
                    labels.add(seat.render());
        return String.format(schema, labels.toArray()).split("\n");
    }

    public ShowScreenInstance(Show show) {
        this.show = show;
        String reservedSeatsString = DataStore.getReservedShowSeats(show);
        try {
            BufferedReader configFileInput = new BufferedReader(new FileReader(DataStore.ScreensConfigFile));
            String currentLine = configFileInput.readLine();

            //If the reading has started for the schema
            boolean readStart = false;
            //If seats were read in the previous line
            boolean prevLineSeatsRead = false;
            //The index for the seat section, silver, platinum, etc.
            int sectionIndex = 0;
            //The index for the overall rows to calculate rowID char.
            int overallRowIndex = 0;
            while (currentLine != null) {
                //If the current line is reading the top of the config for the current screen, start reading the rest of the file
                if (!readStart) {
                    if (currentLine.startsWith("Screen-" + show.screenID)) {
                        //Read the label length value in first line
                        labelLength = Integer.parseInt(currentLine.split("\\|")[1]);
                        readStart = true;
                    }
                } else {
                    if (currentLine.length() == 0)
                        break;

                    if (schema == null)
                        schema = currentLine;
                    else
                        schema += "\n" + currentLine;

                    //Count the number of seats in this line (%s)
                    int seatCount = Utils.countOccurances(currentLine, "%s");

                    //Switch the section if the seats have ended in the current one
                    if (prevLineSeatsRead && seatCount == 0) {
                        ++sectionIndex;
                        prevLineSeatsRead = false;
                    }

                    //If there are seats in this line, add them to the array
                    if (seatCount != 0) {
                        //If the list for the current section does not exist, add one
                        if (sections.size() < sectionIndex + 1)
                            sections.add(new ArrayList<ArrayList<Seat>>());

                        ArrayList<ArrayList<Seat>> currentSection = sections.get(sectionIndex);

                        //Create the row
                        ArrayList<Seat> row = new ArrayList<Seat>();
                        for (int i = 1; i <= seatCount; i++)
                            row.add(new Seat(this, i, (char) ('A' + overallRowIndex), sectionIndex + 1,
                                    reservedSeatsString));

                        //Add row of seats to the section
                        currentSection.add(row);
                        prevLineSeatsRead = true;
                        //increment the row index to increment the char
                        ++overallRowIndex;
                    }
                }

                //Go to next line
                currentLine = configFileInput.readLine();
            }
            configFileInput.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public Seat getSeatByID(String ID) {
        //Check if the ID is valid, else return null
        if (!Utils.isValidSeatID(ID))
            return null;
        ID = ID.toUpperCase();

        //Get the overall index for the row
        int index = ID.charAt(0) - 'A';
        int compare = 0;
        for (ArrayList<ArrayList<Seat>> section : sections)
            for (ArrayList<Seat> row : section)
                if (compare++ == index)
                    return row.get(Integer.parseInt(ID.substring(1, ID.length())) - 1);

        return null;
    }

    public Seat[] getSelectedSeats() {
        ArrayList<Seat> res = new ArrayList<Seat>();
        for (ArrayList<ArrayList<Seat>> section : sections)
            for (ArrayList<Seat> row : section)
                for (Seat seat : row)
                    if (seat.state == 1)
                        res.add(seat);
        Seat[] resArr = new Seat[res.size()];
        for (int i = 0; i < res.size(); i++)
            resArr[i] = res.get(i);
        return resArr;
    }

    public void clearSelection() {
        for (ArrayList<ArrayList<Seat>> section : sections)
            for (ArrayList<Seat> row : section)
                for (Seat seat : row)
                    if (seat.state == 1)
                        seat.state = 0;
    }
}
