package sg.nus.iss.team7.locum.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeParser {
    public static String parseDate(String datetimeStr) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        Date date = inputFormat.parse(datetimeStr);
        return dateFormat.format(date);
    }

    public static String parseTime(String datetimeStr) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        Date date = inputFormat.parse(datetimeStr);
        return timeFormat.format(date);
    }
}

