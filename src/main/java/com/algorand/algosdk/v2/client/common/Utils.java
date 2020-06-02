package com.algorand.algosdk.v2.client.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    // These are immutable and thread safe no matter how you use them.
    public static final ObjectWriter jsonWriter;
    public static final ObjectReader jsonReader;
    public static final ObjectWriter msgpWriter;
    public static final ObjectReader msgpReader;

    static {
        ObjectMapper msgpMapper = new ObjectMapper(new MessagePackFactory());
        msgpMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        msgpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // There's some odd bug in Jackson < 2.8.? where null values are not excluded. See:
        // https://github.com/FasterXML/jackson-databind/issues/1351. So we will
        // also annotate all fields manually
        msgpMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        jsonWriter = jsonMapper.writer();
        jsonReader = jsonMapper.reader();
        msgpWriter = msgpMapper.writer();
        msgpReader = msgpMapper.reader();
    }


    /**
     * Parse the date from String. The time-zone should be Z
     * @param dateString YYYY-MM-DDTHH:MM:SSZ e.g. 2011-12-03T10:15:30Z
     * @return Date object
     * @throws ParseException 
     */
    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        if (dateString.indexOf('.') > 0) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSX");
        }
        if (dateString.indexOf('Z') == -1) {
            throw new RuntimeException("The time should end with the time-zone Z");
        }
        return sdf.parse(dateString.replace("Z", "-0000"));
    }

    /**
     * Get the date formatted as: 2011-12-03T10:15:30Z
     * @param date object
     */
    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        // Include milliseconds if there are any.
        if ( date.getTime() % 1000 != 0) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSX");
        }
        sdf.setTimeZone(TimeZone.getTimeZone("Z"));
        return sdf.format(date);
    }
}
