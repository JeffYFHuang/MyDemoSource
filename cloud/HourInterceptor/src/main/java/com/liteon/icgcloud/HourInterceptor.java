package com.liteon.icgcloud;

import java.util.List;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * HourInterceptor is the class that implements Inteceptor
 * interface of Flume, this class intercepts the incoming
 * Flume event to determine the current hour range based
 * on current time and grep the school ID from the incoming
 * JSON data.
 *
 */
public class HourInterceptor implements Interceptor {

    private final String hrHeader;
    private final String schoolIdHeader;
    /* 
     * following year, month and date related
     * code is added for experimentation
     */
    private final String yearHeader;
    private final String monthHeader;
    private final String dateHeader;

    private HourInterceptor(String hrHeaderKey, String schoolIdHeaderKey, String yearHeaderKey, String monthHeaderKey, String dateHeaderKey) {
        System.out.println("[HourInterceptor] constructor");
        hrHeader = hrHeaderKey;
        schoolIdHeader = schoolIdHeaderKey;
        /* 
         * following year, month and date related
         * code is added for experimentation
         */
        yearHeader = yearHeaderKey;
        monthHeader = monthHeaderKey;
        dateHeader = dateHeaderKey;

        System.out.println("[HourInterceptor] hrHeaderKey => " + hrHeaderKey);
        System.out.println("[HourInterceptor] schoolIdHeaderKey => " + schoolIdHeaderKey);
        /* 
         * following year, month and date related
         * code is added for experimentation
         */
        System.out.println("[HourInterceptor] yearHeaderKey => " + yearHeaderKey);
        System.out.println("[HourInterceptor] monthHeaderKey => " + monthHeaderKey);
        System.out.println("[HourInterceptor] dateHeaderKey => " + dateHeaderKey);
    }

    @Override
    public void initialize() {
        System.out.println("[HourInterceptor] initialize()");
    }

    @Override
    public Event intercept(Event event) {
        System.out.println("[HourInterceptor] intercept()");
        String hourRange = "";
        String schoolId = "";
        /* 
         * following year, month and date related
         * code is added for experimentation
         */
        String year = "";
        String month = "";
        String date = "";

        byte[] eventBodyJsonData = event.getBody();
        //System.out.println("Body:" + new String(eventBodyJsonData));

        try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readValue(new String(eventBodyJsonData), JsonNode.class);
                schoolId = rootNode.get("sid").asText();
                /* 
                 * following year, month and date related
                 * code is added for experimentation
                 */
                year = rootNode.get("year").asText();
                month = rootNode.get("month").asText();
                date = rootNode.get("date").asText();
                hourRange = rootNode.get("hour_range").asText();

                /* 
                 * following year, month and date related
                 * code is added for experimentation
                 */
                if(null != schoolId) {
                    System.out.println("[HourInterceptor] school id: " + schoolId);
                }
                if(null != year) {
                    System.out.println("[HourInterceptor] year: " + year);
                }
                if(null != month) {
                    System.out.println("[HourInterceptor] month: " + month);
                }
                if(null != date) {
                    System.out.println("[HourInterceptor] date: " + date);
                }
                if(null != hourRange) {
                    System.out.println("[HourInterceptor] hourRange " + hourRange);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        // TBD: commenting out following piece of code
        // this has to be enabled after the testing.
        /*
        hourRange = getHourRange();
        System.out.println("[HourInterceptor] hourRange => " + hourRange);        */

        Map headers = event.getHeaders();
        headers.put(schoolIdHeader, schoolId);
        headers.put(hrHeader, hourRange);
        /* 
         * following year, month and date related
         * code is added for experimentation
         */
        headers.put(yearHeader, year);
        headers.put(monthHeader, month);
        headers.put(dateHeader, date);


        event.setHeaders(headers);

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        System.out.println("[HourInterceptor] intercept() List of events");
        for (Event event : events) {
            intercept(event);
        }

        return events;
    }

    @Override
    public void close() {
        System.out.println("[HourInterceptor] close()");
    }

    public static class Builder implements Interceptor.Builder {
        private String hourRangeHeaderKey;
        private String schoolIdHeaderKey;

        /* 
         * following year, month and date related
         * code is added for experimentation
         */
         private String yearHeaderKey;
         private String monthHeaderKey;
         private String dateHeaderKey;

        @Override
        public void configure(Context context) {
            System.out.println("[HourInterceptor] configure()");
            hourRangeHeaderKey = context.getString("mHourRange");
            schoolIdHeaderKey = context.getString("mSchoolId");

            /* 
             * following year, month and date related
             * code is added for experimentation
             */
            yearHeaderKey = context.getString("mYear");
            monthHeaderKey = context.getString("mMonth");
            dateHeaderKey = context.getString("mDate");


        }

        @Override
        public Interceptor build() {
            System.out.println("[HourInterceptor] build()");
            return new HourInterceptor(hourRangeHeaderKey, schoolIdHeaderKey, yearHeaderKey, monthHeaderKey, dateHeaderKey);
        }
    }

    private String getHourRange() {

        String hourRange = "";
        Calendar rightNow = Calendar.getInstance();
        int hourOfDay = rightNow.get(Calendar.HOUR_OF_DAY);

        if ((hourOfDay >= 8) && (hourOfDay < 14)) {
            hourRange = "08";
        } else if ((hourOfDay >= 14) && (hourOfDay < 20)) {
            hourRange = "14";
        } else if (((hourOfDay >= 20) && (hourOfDay < 24)) || ((hourOfDay >= 0) && (hourOfDay < 2))) {
            hourRange = "20";
        } else if ((hourOfDay >= 2) && (hourOfDay < 8)) {
            hourRange = "02";
        }

        return hourRange;
    }


}
