package codes.styxo.school.projects.SkyCinemasV2.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Duration;

//Utility methods for parsing and handling time
public class TimeUtils {
    //kk is 24 hour format, 1-24
    //mm is minute format, 0-59
    //dd is day format, 1-31
    //MM is month format, 1-12
    //yyyy is year format, eg. 2022
    public static SimpleDateFormat inputFormat = new SimpleDateFormat("kk:mm dd/MM/yyyy");
    public static SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy | hh:mm aa");
    public static SimpleDateFormat displayFormat_DateOnly = new SimpleDateFormat("dd/MM/yy");
    public static SimpleDateFormat displayFormat_TimeOnly = new SimpleDateFormat("hh:mm aa");
    public static SimpleDateFormat durationInputFormat = new SimpleDateFormat("kk:mm");
    public static SimpleDateFormat durationDisplayFormat = new SimpleDateFormat("hh 'hours' mm 'minutes'");

    //Method to parse string user input to Date object
    public static Date parse(String time) {
        try {
            return inputFormat.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    //Method to parse string user input to Duration object
    public static Duration parseDuration(String time) {
        try {
            return Duration.ofMillis(durationInputFormat.parse(time).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    //Method to convert Date object to a user readable string for display
    public static String format(Date time) {
        return displayFormat.format(time);
    }

    //Method to convert Duration object to a user readable string for display
    public static String format(Duration duration) {
        return durationDisplayFormat.format(new Date(duration.toMillis()));
    }

    //Method to check overlap between two time periods
    public static boolean checkOverlap(long start1, long end1, long start2, long end2) {
        return ((start1 < end2 && start2 <= end1) || (start1 > start2 && start1 <= end2)
                || (start1 == start2 || end1 == end2)
                || ((start1 <= start2 && end1 >= end2) || (start1 >= start2 && end1 <= end2)));
    }
}
