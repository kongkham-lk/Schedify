package com.example.schedify;

import android.content.Context;
import android.icu.util.LocaleData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        extractCourseDataFromMoodle(); // Bind data and save to courseList
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

        // Initialize JSON objects
        JSONObject result = new JSONObject();
        JSONArray courses = new JSONArray();
        JSONArray assignments = new JSONArray();

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
            Elements eventWrappers = doc.select("div.pb-2[data-region='event-list-wrapper']");
//            LocalDateTime now = LocalDateTime.now();
//            int currDate = now.getDayOfMonth();
            for (Element eventAssignment : eventWrappers) {

                Elements eventDays = eventAssignment.select("td.clickable.hasevent");
                for (Element eventDay : eventDays) {
                    int date = Integer.parseInt(eventDay.attr("data-day"));
//                    if (date < currDate)
//                        continue;

//                    Elements eventAssignment = eventDay.select("ul li[data-event-component='mod_assign'] a[data-action='view-event']");
                    String eventTitle = eventAssignment.attr("title");
                    String assignmentLink = eventAssignment.attr("href");
                    String submissionLink = assignmentLink + "&action=editsubmission";

                }
            }
            Elements events = doc.select("td.clickable.hasevent ul li[data-region='event-item']");
            for (Element event : events) {
                String eventTitle = event.select("a[data-action='view-event'] span.eventname").text();
                String assignmentLink = event.select("a[data-action='view-event']").attr("href");
                String courseId = event.attr("data-courseid");
                String courseName = event.attr("data-eventtype-course");
                String dueDate = event.closest("td").attr("data-day-timestamp");

                // Extract the submission time
                String dueTime = null;
                Element timeTag = event.selectFirst("small.text-right.text-nowrap.align-self-center");
                if (timeTag != null) {
                    dueTime = timeTag.text().trim();
                }

                // Extract submission link if available
                String submissionLink = null;
                Element submissionTag = event.selectFirst("a[title*='submission']");
                if (submissionTag != null) {
                    submissionLink = submissionTag.attr("href");
                }

                JSONObject assignment = new JSONObject();
                assignment.put("title", eventTitle.isEmpty() ? "Not Available" : eventTitle);
                assignment.put("courseId", courseId.isEmpty() ? "Not Available" : courseId);
                assignment.put("courseName", courseName.isEmpty() ? "Not Available" : courseName);
                assignment.put("dueDate", dueDate.isEmpty() ? "Not Available" : dueDate);
                assignment.put("dueTime", dueTime != null ? dueTime : "Not Available");
                assignment.put("assignmentLink", assignmentLink.isEmpty() ? "Not Available" : assignmentLink);
                assignment.put("submissionLink", submissionLink != null ? submissionLink : "Not Available");

                assignments.put(assignment);
            }

            // Combine results
            result.put("courses", courses);
            result.put("assignments", assignments);

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
