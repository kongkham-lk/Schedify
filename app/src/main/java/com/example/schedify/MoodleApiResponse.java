package com.example.schedify;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MoodleApiResponse {

    private List<CourseModel> courseList;
    private String requestMethod = "";
    private Context context;

    public MoodleApiResponse(Context context) {
        courseList = new ArrayList<>();
        this.context = context;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    // Callable task to fetch API data
    public List<CourseModel> retrievedCourseDataFromMoodle() {
//        APIFetcher moodleFetcher = new APIFetcher();
//        moodleFetcher.setRequestMethod(requestMethod);
//        String moodleHTML = moodleFetcher.getResponse(apiUrl, cookie); // fetch course schedule from API
        JSONObject jsonObject = extractCourseDataFromMoodle();
        return courseList;
    }
    // Fetch HTML content from a given URL
//    public String fetchHTML(String targetUrl, String cookie) throws Exception {
//        StringBuilder htmlContent = new StringBuilder();
//
//        // Establish HttpURLConnection
//        URL url = new URL(targetUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//        connection.setConnectTimeout(10000);
//        connection.setReadTimeout(10000);
//
//        // Check response code
//        int responseCode = connection.getResponseCode();
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            throw new RuntimeException("Failed to fetch HTML. HTTP Response Code: " + responseCode);
//        }
//
//        // Read HTML content
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            htmlContent.append(line);
//        }
//        reader.close();
//
//        return htmlContent.toString();
//    }

    // Parse HTML and extract assignments and courses
    public JSONObject extractCourseDataFromMoodle() {
        String html = Helper.loadHTMLFromPreferences(context);
        // Parse HTML using Jsoup
        Document doc = Jsoup.parse(html);

        JSONObject result = new JSONObject();
        JSONArray courses = new JSONArray();
        JSONArray assignmentsByDate = new JSONArray();

        try {
            // Extract enrolled courses
            Elements courseOptions = doc.select("#calendar-course-filter-1 option");
            for (Element course : courseOptions) {
                String courseId = course.attr("value");
                String courseName = course.text();
                String courseUrl = "https://moodle.tru.ca/course/view.php?id=" + courseId;

                if (!courseId.equals("1")) { // Skip "All courses"
                    JSONObject courseJson = new JSONObject();
                    courseJson.put("courseId", courseId);
                    courseJson.put("courseName", courseName);
                    courseJson.put("courseUrl", courseUrl);
                    courses.put(courseJson);
                }
            }

            // Extract assignments
            Elements dateSections = doc.select("div[data-region='event-list-content-date']");

            // Assignment content with full detail - dynamic loaded data is included
            if (dateSections != null) {
                for (Element dateSection : dateSections) {
                    // Get the date
                    String dateText = dateSection.selectFirst("h5").text();
                    String dateTimestamp = dateSection.attr("data-timestamp");

                    // Select corresponding assignments under this date
                    Element parentDiv = dateSection.nextElementSibling(); // Sibling contains assignment list
                    JSONArray assignments = new JSONArray();

                    if (parentDiv != null && parentDiv.hasClass("list-group")) {
                        Elements assignmentElements = parentDiv.select("div[data-region='event-list-item']");

                        for (Element assignmentElement : assignmentElements) {
                            JSONObject assignmentJson = new JSONObject();

                            // Extract details for each assignment
                            String title = assignmentElement.selectFirst("a[title]").text();
                            String assignmentLink = assignmentElement.selectFirst("a[title]").attr("href");
                            String courseDetails = assignmentElement.selectFirst("small").text();
                            String dueTime = assignmentElement.selectFirst("small.text-right").text();
                            String iconLink = assignmentElement.selectFirst("img").attr("src");

                            // Populate assignment JSON object
                            assignmentJson.put("title", title);
                            assignmentJson.put("assignmentLink", assignmentLink);
                            assignmentJson.put("courseDetails", courseDetails);
                            assignmentJson.put("dueTime", dueTime);
                            assignmentJson.put("iconLink", iconLink);

                            assignments.put(assignmentJson);
                        }
                    }

                    // Combine assignments with their date
                    JSONObject dateJson = new JSONObject();
                    dateJson.put("date", dateText);
                    dateJson.put("timestamp", dateTimestamp);
                    dateJson.put("assignments", assignments);

                    assignmentsByDate.put(dateJson);
                }
            } else {
                // Assignment content with less detail - dynamic loaded data is not included
                LocalDateTime now = LocalDateTime.now();
                int currDate = now.getDayOfMonth();

                // Select the calendar month table body
                Elements eventMonth = doc.select("table.calendarmonth tbody");

                for (Element eventWeek : eventMonth) {
                    // Select all days with events
                    Elements eventDays = eventWeek.select("td.clickable.hasevent");

                    for (Element eventDay : eventDays) {
                        Element eventDateInfo = eventDay.selectFirst("a[data-action='view-day-link']");
                        int dateDay = Integer.parseInt(eventDateInfo.attr("data-day"));
                        if (dateDay < currDate) // Skip past dates
                            continue;

                        // Extract assignments for the day
                        Elements eventAssignments = eventDay.select("div[data-region='day-content'] ul li[data-event-component='mod_assign'] a");
                        if (eventAssignments == null || eventAssignments.size() == 0)
                            continue;

                        // Prepare date details
                        int dateMonth = Integer.parseInt(eventDateInfo.attr("data-month"));
                        int dateYear = Integer.parseInt(eventDateInfo.attr("data-year"));
                        String dueDate = dateDay + "/" + dateMonth + "/" + dateYear;

                        // JSON Array to hold assignments for this date
                        JSONArray assignments = new JSONArray();

                        for (Element eventAssignment : eventAssignments) {
                            // Extract assignment details
                            String title = eventAssignment.attr("title");
                            if (!title.isEmpty() && title.contains("is due"))
                                title = title.substring(0, title.indexOf("is due")).trim();
                            String assignmentLink = eventAssignment.attr("href");
                            String submissionLink = assignmentLink + "&action=editsubmission";

                            // Add assignment to JSON object
                            JSONObject assignmentJson = new JSONObject();
                            assignmentJson.put("title", title.isEmpty() ? "Not Available" : title);
                            assignmentJson.put("assignmentLink", assignmentLink.isEmpty() ? "Not Available" : assignmentLink);
                            assignmentJson.put("submissionLink", submissionLink.isEmpty() ? "Not Available" : submissionLink);
                            assignmentJson.put("dueDate", dueDate); // Add the due date to each assignment

                            assignments.put(assignmentJson);
                        }

                        // Add the date and its assignments to the result
                        JSONObject dateJson = new JSONObject();
                        dateJson.put("date", dueDate);
                        dateJson.put("timestamp", eventDateInfo.attr("data-day-timestamp"));
                        dateJson.put("assignments", assignments);

                        assignmentsByDate.put(dateJson);

                    }
                }
            }

            // Combine results
            result.put("type", "Moodle API");
            result.put("courses", courses);
            result.put("assignmentsByDate", assignmentsByDate);

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String transformTimeStamp (String strTimestamp) {
        long timestamp = Long.parseLong(strTimestamp);
        Date date = new Date(timestamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format.format(date);
    }
}
