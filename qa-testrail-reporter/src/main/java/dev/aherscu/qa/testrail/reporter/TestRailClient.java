/*
 * Copyright 2023 Adrian Herscu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// see https://support.gurock.com/hc/en-us/articles/7077074577044-Binding-Java#01G68HHNMFWZVK25DD1SX36NK7
package dev.aherscu.qa.testrail.reporter;

import static java.net.HttpURLConnection.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import org.json.simple.*;

import lombok.*;

public class TestRailClient {

    private final String m_url;
    private String       m_user;
    private String       m_password;

    public TestRailClient(String base_url) {
        if (!base_url.endsWith("/")) {
            base_url += "/";
        }

        this.m_url = base_url + "index.php?/api/v2/";
    }

    private static String getAuthorization(String user, String password) {
        try {
            return new String(
                Base64.getEncoder().encode((user + ":" + password).getBytes()));
        } catch (IllegalArgumentException e) {
            // Not thrown
        }

        return "";
    }

    /**
     * Get/Set Password
     * <p>
     * Returns/sets the password used for authenticating the API requests.
     */
    public String getPassword() {
        return this.m_password;
    }

    public void setPassword(String password) {
        this.m_password = password;
    }

    /**
     * Get/Set User
     * <p>
     * Returns/sets the user used for authenticating the API requests.
     */
    public String getUser() {
        return this.m_user;
    }

    public void setUser(String user) {
        this.m_user = user;
    }

    /**
     * Send Get
     * <p>
     * Issues a GET request (read) against the API and returns the result (as
     * Object, see below).
     * <p>
     * Arguments:
     * <p>
     * uri The API method to call including parameters (e.g. get_case/1)
     * <p>
     * Returns the parsed JSON response as standard object which can either be
     * an instance of JSONObject or JSONArray (depending on the API method). In
     * most cases, this returns a JSONObject instance which is basically the
     * same as java.util.Map.
     * <p>
     * If 'get_attachment/:attachment_id', returns a String
     */
    public Object sendGet(String uri, String data) {
        return this.sendRequest("GET", uri, data);
    }

    public Object sendGet(String uri) {
        return this.sendRequest("GET", uri, null);
    }

    /**
     * Send POST
     * <p>
     * Issues a POST request (write) against the API and returns the result (as
     * Object, see below).
     * <p>
     * Arguments:
     * <p>
     * uri The API method to call including parameters (e.g. add_case/1) data
     * The data to submit as part of the request (e.g., a map) If adding an
     * attachment, must be the path to the file
     * <p>
     * Returns the parsed JSON response as standard object which can either be
     * an instance of JSONObject or JSONArray (depending on the API method). In
     * most cases, this returns a JSONObject instance which is basically the
     * same as java.util.Map.
     */
    public Object sendPost(String uri, Object data) {
        return this.sendRequest("POST", uri, data);
    }

    @SneakyThrows
    private Object sendRequest(String method, String uri, Object data) {
        URL url = new URL(this.m_url + uri);
        // Create the connection object and set the required HTTP method
        // (GET/POST) and headers (content type and basic auth).
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String auth = getAuthorization(this.m_user, this.m_password);
        conn.addRequestProperty("Authorization", "Basic " + auth);

        if (method.equals("POST")) {
            conn.setRequestMethod("POST");
            // Add the POST arguments, if any. We just serialize the passed
            // data object (i.e. a dictionary) and then add it to the
            // request body.
            if (data != null) {
                if (uri.startsWith(
                    "add_attachment")) // add_attachment API requests
                {
                    String boundary =
                        "TestRailAPIAttachmentBoundary"; // Can be any random
                                                         // string
                    File uploadFile = new File((String) data);

                    conn.setDoOutput(true);
                    conn.addRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);

                    OutputStream ostreamBody = conn.getOutputStream();
                    BufferedWriter bodyWriter =
                        new BufferedWriter(new OutputStreamWriter(ostreamBody));

                    bodyWriter.write("\n\n--" + boundary + "\r\n");
                    bodyWriter.write(
                        "Content-Disposition: form-data; name=\"attachment\"; filename=\""
                            + uploadFile.getName() + "\"");
                    bodyWriter.write("\r\n\r\n");
                    bodyWriter.flush();

                    // Read file into request
                    InputStream istreamFile = new FileInputStream(uploadFile);
                    int bytesRead;
                    byte[] dataBuffer = new byte[1024];
                    while ((bytesRead = istreamFile.read(dataBuffer)) != -1) {
                        ostreamBody.write(dataBuffer, 0, bytesRead);
                    }

                    ostreamBody.flush();

                    // end of attachment, add boundary
                    bodyWriter.write("\r\n--" + boundary + "--\r\n");
                    bodyWriter.flush();

                    // Close streams
                    istreamFile.close();
                    ostreamBody.close();
                    bodyWriter.close();
                } else // Not an attachment
                {
                    conn.addRequestProperty("Content-Type", "application/json");
                    byte[] block =
                        JSONValue.toJSONString(data).getBytes(
                            StandardCharsets.UTF_8);

                    conn.setDoOutput(true);
                    OutputStream ostream = conn.getOutputStream();
                    ostream.write(block);
                    ostream.close();
                }
            }
        } else // GET request
        {
            conn.addRequestProperty("Content-Type", "application/json");
        }

        // Execute the actual web request (if it wasn't already initiated
        // by getOutputStream above) and record any occurred errors (we use
        // the error stream in this case).
        int status = conn.getResponseCode();

        InputStream istream;
        if (status != HTTP_OK) {
            istream = conn.getErrorStream();
            if (istream == null) {
                throw new RuntimeException(
                    "TestRail API return HTTP " + status +
                        " (No additional error message received)");
            }
        } else {
            istream = conn.getInputStream();
        }

        // If 'get_attachment' (not 'get_attachments') returned valid status
        // code, save the file
        if ((istream != null)
            && (uri.startsWith("get_attachment/"))) {
            FileOutputStream outputStream = new FileOutputStream((String) data);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = istream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            istream.close();
            return data;
        }

        // Not an attachment received
        // Read the response body, if any, and deserialize it from JSON.
        String text = "";
        if (istream != null) {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    istream,
                    StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                text += line;
                text += System.getProperty("line.separator");
            }

            reader.close();
        }

        Object result;
        if (!text.equals("")) {
            result = JSONValue.parse(text);
        } else {
            result = new JSONObject();
        }

        // Check for any occurred errors and add additional details to
        // the exception message, if any (e.g. the error message returned
        // by TestRail).
        if (status != HTTP_OK) {
            String error = "No additional error message received";
            if (result instanceof JSONObject) {
                JSONObject obj = (JSONObject) result;
                if (obj.containsKey("error")) {
                    error = '"' + (String) obj.get("error") + '"';
                }
            }

            throw new RuntimeException(
                "TestRail API returned HTTP " + status +
                    "(" + error + ")");
        }

        return result;
    }
}
