package com.guoshengkai.reverseapistudio.utils.script;

import com.alibaba.fastjson.JSON;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * JavaScript HTTP 模块
 */
@Slf4j
public class HTTP {

    public static HttpSender request(String url, Object option){
        RequestOptions requestOptions = JSON.parseObject(JSON.toJSONString(option), RequestOptions.class);
        HttpSender sender = new HttpSender(url, requestOptions);
        return sender;

    }

    @Setter
    public static class RequestOptions {
        private String method;
        private Object body;
        private Map<String, String> headers;

        public RequestOptions method(String method) {
            this.method = method;
            return this;
        }

        public RequestOptions body(Object body) {
            this.body = body;
            return this;
        }

        public RequestOptions headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
    }

    public static class HttpSender{
        private String url;
        private RequestOptions option;

        public HttpSender(String url, RequestOptions option) {
            this.url = url;
            this.option = option;
        }
        public HttpSender() { }

        public Object stream(HttpStreamCallback callback) {
            int status = 0;
            try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(15000))
                    .version(HttpClient.Version.HTTP_2)
                    .build()){
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().timeout(Duration.ofMillis(9000))
                        .uri(URI.create(url));
                requestBuilder.setHeader("Content-Type", "application/json;charset=utf-8");
                if (option.headers != null) {
                    for (Map.Entry<String, String> entry : option.headers.entrySet()) {
                        requestBuilder.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                requestBuilder.POST(
                        HttpRequest.BodyPublishers.ofString(JSON.toJSONString(option.body), StandardCharsets.UTF_8)
                );
                // 这里用 ofLines()，每次到一行就触发

                BufferedReader reader = null;
                HttpResponse<InputStream> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
                status = response.statusCode();
                InputStream in = response.body();
                if (status != 200) {
                    String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        return JSON.parseObject(body, Map.class);
                    }catch (Exception e){
                        return body;
                    }
                }
                reader = new BufferedReader(new InputStreamReader(in), 1);
                // 一行一行的读
                String tempLine;
                while ((tempLine = reader.readLine()) != null) {
                    if (tempLine.trim().isEmpty()){
                        continue;
                    }
                    String[] lines = new String[]{tempLine};
                    if (tempLine.trim().contains("\n")){
                        lines = tempLine.split("\n");
                    }
                    for (String line: lines){
                        if (line.trim().isEmpty()){
                            continue;
                        }
                        callback.handler(false, line);
                    }
                }
            }catch (Exception e){
                throw new RuntimeException("HTTP Request Error: " + e.getMessage(), e);
            }
            return null;
        }
    }

    public interface HttpStreamCallback {
        void handler(boolean done, Object data);
    }

    @SneakyThrows
    public static void main(String[] args) {
        String userMessageId = UUID.randomUUID().toString();
        String chatId = UUID.randomUUID().toString();
        String modelMessageId = UUID.randomUUID().toString();
        String cfClearance = "NWXEMvyQ2BwnwUvj8uUrookb.nlUi2ZbJOE1X8mLs1U-1750830437-1.2.1.1-pYGNkD8aq.bhR1AEeofQxMDwRviWjyiqABM6goFOjBB0Ur358SvU8iteQ.PybYj74tQ8CSd80bhPYWh2tUPu8vlTn45J_eHMoETFY4p5.Pa716LDLjFuz27LrYgW0ddtu6mYV7TuxAI8wHeTdnYtkRroy.714OKxfLz.2pKYclCrJLd5eQWkFfcC3_9kj65hxuaSLc.qaImIVe0ODJ4Lr63DdNKVO.Aw8EfdiWGuBIj8Afua6.vBVGjmXmFcT7aN8y3L8GUBf8wBHg1Qp.9weA4l7SyeUhIKuh3yB8eD7Ma2PJ8iip3Zhtp6_Hd40CTJDFAcF6uBEd9pCoZUWZxZdxOQOoPBzni1UCbvj1mOHu0";
        String arenaAuth = "base64-eyJhY2Nlc3NfdG9rZW4iOiJleUpoYkdjaU9pSklVekkxTmlJc0ltdHBaQ0k2SWtOVFQwNHhkM05uU0hkRlNFTkNNbGNpTENKMGVYQWlPaUpLVjFRaWZRLmV5SnBjM01pT2lKb2RIUndjem92TDJoMWIyZDZiMlZ4ZW1OeVpIWnJkM1IyYjJScExuTjFjR0ZpWVhObExtTnZMMkYxZEdndmRqRWlMQ0p6ZFdJaU9pSm1NR1l3TURrd09TMW1NRGM0TFRRek1UZ3RZalkxT0MxalpESTRaRGhqTURCak9EWWlMQ0poZFdRaU9pSmhkWFJvWlc1MGFXTmhkR1ZrSWl3aVpYaHdJam94TnpVd09ETTBNRGc1TENKcFlYUWlPakUzTlRBNE16QTBPRGtzSW1WdFlXbHNJam9pSWl3aWNHaHZibVVpT2lJaUxDSmhjSEJmYldWMFlXUmhkR0VpT250OUxDSjFjMlZ5WDIxbGRHRmtZWFJoSWpwN0ltbGtJam9pTVRJMlltUTFNV1V0WXpoak1DMDBOMlprTFRsbE1tUXRZakJpTXpBMlpHVmhOelV3SW4wc0luSnZiR1VpT2lKaGRYUm9aVzUwYVdOaGRHVmtJaXdpWVdGc0lqb2lZV0ZzTVNJc0ltRnRjaUk2VzNzaWJXVjBhRzlrSWpvaVlXNXZibmx0YjNWeklpd2lkR2x0WlhOMFlXMXdJam94TnpVd09ETXdORGc1ZlYwc0luTmxjM05wYjI1ZmFXUWlPaUprWmpObE1HRmlZUzAzTURZMUxUUmpaakl0T0dNME1TMHpZakEyWXpoa016WTBaR1FpTENKcGMxOWhibTl1ZVcxdmRYTWlPblJ5ZFdWOS5yMU1yWDBfYUFmNlQ0blVGMHZ5TnM5X2VxRmh2TFI4SXpuRTRicjAyNl93IiwidG9rZW5fdHlwZSI6ImJlYXJlciIsImV4cGlyZXNfaW4iOjM2MDAsImV4cGlyZXNfYXQiOjE3NTA4MzQwODksInJlZnJlc2hfdG9rZW4iOiIzaGR2ZjN4cnZmdXkiLCJ1c2VyIjp7ImlkIjoiZjBmMDA5MDktZjA3OC00MzE4LWI2NTgtY2QyOGQ4YzAwYzg2IiwiYXVkIjoiYXV0aGVudGljYXRlZCIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiZW1haWwiOiIiLCJwaG9uZSI6IiIsImxhc3Rfc2lnbl9pbl9hdCI6IjIwMjUtMDYtMjVUMDU6NDg6MDkuMjgxNTA5MjA1WiIsImFwcF9tZXRhZGF0YSI6e30sInVzZXJfbWV0YWRhdGEiOnsiaWQiOiIxMjZiZDUxZS1jOGMwLTQ3ZmQtOWUyZC1iMGIzMDZkZWE3NTAifSwiaWRlbnRpdGllcyI6W10sImNyZWF0ZWRfYXQiOiIyMDI1LTA2LTI1VDA1OjQ4OjA5LjI3OTkyOVoiLCJ1cGRhdGVkX2F0IjoiMjAyNS0wNi0yNVQwNTo0ODowOS4yODI5NzNaIiwiaXNfYW5vbnltb3VzIjp0cnVlfX0";
        Map<String, String> headers = new HashMap<>(Map.of(
                "accept",                "*/*",
                "accept-language",       "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
                "content-type",          "text/plain;charset=UTF-8",
                "origin",                "https://lmarena.ai",
                "priority",              "u=1, i",
                "referer",               "https://lmarena.ai/c/ba01b35c-3607-453f-9fba-c1700d490793",
                "sec-ch-ua",             "\"Microsoft Edge\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
                "user-agent",            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0",
                "sec-ch-ua-mobile",      "?0",
                "sec-ch-ua-platform",    "\"macOS\""
        ));
        headers.putAll(Map.of(
                "sec-fetch-dest",        "empty",
                "sec-fetch-mode",        "cors",
                "sec-fetch-site",        "same-origin",
                "cookie",                "_ga=GA1.1.1500452886.1734063060; _ga_N6MCLSNCLZ=GS1.1.1734604716.3.0.1734604716.0.0.0; _gcl_au=1.1.515125668.1744704265; _ga_82JVGLVRQH=GS1.1.1744712044.9.1.1744713852.0.0.0; sidebar=true; cf_clearance=__CF_CLEARANCE; __cf_bm=JczbWiXpeHaz2xLa.6S768BgFuxYdzk2p7YMJiK.L.c-1750820228-1.0.1.1-KzjltmMbpYWJFXYjckiXtiYljmwX0VNbQFVgmol5IoW.o5Redn.ajcQ5aBFqSq8m35xl8nS_ai.3OXVRH7fErjE2aRPN49.w9P18rr32Z9s; arena-auth-prod-v1=__ARENA_AUTH_PROD; ph_phc_LG7IJbVJqBsk584rbcKca0D5lV2vHguiijDrVji7yDM_posthog=%7B%22distinct_id%22%3A%22e78a10c9-94c3-430f-992b-b9dd5189f4d2%22%2C%22%24sesid%22%3A%5B1750820468625%2C%220197a504-7a14-7035-9f20-b82ad33dec9a%22%2C1750820223508%5D%2C%22%24epp%22%3Atrue%2C%22%24initial_person_info%22%3A%7B%22r%22%3A%22%24direct%22%2C%22u%22%3A%22https%3A%2F%2Flmarena.ai%2F%22%7D%7D; _ga_72FK1TMV06=GS2.1.s1750820466$o2$g0$t1750820468$j58$l0$h0".replace("__CF_CLEARANCE", cfClearance).replace("__ARENA_AUTH_PROD", arenaAuth)
        ));
        testHTTP(headers, "https://lmarena.ai/api/stream/create-evaluation", """
                {
                    "id":"__SESSION_ID",
                    "mode":"direct",
                    "modelAId":"c5a11495-081a-4dc6-8d9a-64a4fd6f7bbc",
                    "userMessageId":"__USER_MESSAGE_ID",
                    "modelAMessageId":"__MODEL_MESSAGE_ID",
                    "messages":[
                        {"id":"__USER_MESSAGE_ID","role":"user","content":"你好","experimental_attachments":[],"parentMessageIds":[],"participantPosition":"a","modelId":null,"evaluationSessionId":"__SESSION_ID","status":"pending","failureReason":null},
                        {"id":"__MODEL_MESSAGE_ID","role":"assistant","content":"","experimental_attachments":[],"parentMessageIds":["__USER_MESSAGE_ID"],"participantPosition":"a","modelId":"c5a11495-081a-4dc6-8d9a-64a4fd6f7bbc","evaluationSessionId":"__SESSION_ID","status":"pending","failureReason":null}
                    ],
                    "modality":"chat"}
                """.replace("__USER_MESSAGE_ID", userMessageId).replace("__MODEL_MESSAGE_ID", modelMessageId).replace("__SESSION_ID", chatId));

    }

    private static String testHTTP(Map<String, String> headers, String url, String body) throws IOException {
        List<String> cmds = new ArrayList<>(List.of(
                "curl", url
        ));
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().toLowerCase().contains("cookie")) {
                    cmds.add("-b");
                    cmds.add(entry.getValue());
                }else {
                    cmds.add("-H");
                    cmds.add(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
        cmds.add("--data-raw");
        cmds.add(body);
        Process process = Runtime.getRuntime().exec(cmds.toArray(new String[0]));
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return null;
    }

    // _ga=GA1.1.1500452886.1734063060;
    // _ga=GA1.1.1500452886.1734063060;

    // _ga_N6MCLSNCLZ=GS1.1.1734604716.3.0.1734604716.0.0.0;
    // _ga_N6MCLSNCLZ=GS1.1.1734604716.3.0.1734604716.0.0.0;

    // _gcl_au=1.1.515125668.1744704265;
    // _gcl_au=1.1.515125668.1744704265;

    // _ga_82JVGLVRQH=GS1.1.1744712044.9.1.1744713852.0.0.0;
    // _ga_82JVGLVRQH=GS1.1.1744712044.9.1.1744713852.0.0.0;

    // sidebar=true;
    // sidebar=true;

    // cf_clearance=jvIiNAlH3vAii6kJ8AqVbFX8MFvrX89eZIUnaiZmbw8-1750815518-1.2.1.1-lB3OshO2WO9O5gn0GICv3wtV40vTPp4.zRg1LEhEimnJu86i4nGXmIZCMX3CkLBwk44aFqNxaoz18NaZh9Maczblq8SAhKQFNBEaT2VtPv.Ylo6k7jQR0W.e3gl1D3StlWFRRICwa4UFn8F_96XHP6BH0dhdR_J56RQlR8808CVSQ11dAiylc9Viic_xQ0FxwAr1hsKkHcYMBwaQMz18RCMv8.ZnmZfVGLv4ErIDLEWPAATmMyU92wikpGuHTpDH931Prwg6DZLP.XgKmVHdy0ngCTCcRXjTqO_FaLW4axSKJDjCb6sRKHa3V4rzbekraIS8uPDrNKR67yNCG3c4ph4CrJVxnpvJUS2XmpXhtzg;
    // cf_clearance=wwBDbc8t0QdHFmvLDjOkQw0bbzBIR825b5sCaRSZ7ss-1750812084-1.2.1.1-c10YMvvEyhGoeWtNC1wXoajl6ZJH1OSOyglHkGzSyACMLDhATkW_sYWSEwV5mWI496Hi5RI.ekfnft4ytKjCfYBcv_z2q4vcrdNeoGVzhfA1May1eWqt295vJdO.FHqsItVCEL7q263UMR7YWCqzXseVaNGEhXrcManS3wCBd3XgedGQqJDq3loqJWRSLzNYEpolhTmFXYrKhu2JaM8zn61xWlM9jz0ntTXG3a4trlb8CNWiLMEJavtd26aNnP8azbmVaOVT1PqpAs_aIpErrgABe4adPdHQNRSVwcA5jyoPUpXyRz0SymAgQNQMZ4s4ctmkEWZnBSGnx75b1OjGEYLxbHPobrufdBqhP2Ffxk8wkt86ca20H7wgL0lJ5VX.;

    // __cf_bm=JczbWiXpeHaz2xLa.6S768BgFuxYdzk2p7YMJiK.L.c-1750820228-1.0.1.1-KzjltmMbpYWJFXYjckiXtiYljmwX0VNbQFVgmol5IoW.o5Redn.ajcQ5aBFqSq8m35xl8nS_ai.3OXVRH7fErjE2aRPN49.w9P18rr32Z9s;
    // __cf_bm=Nf3eYudQgkX34EEs7j7DmcmlIqJSZgKVvELIs.mnA6c-1750814963-1.0.1.1-jD_5VVFMXllQxREBWQYceh9sEGU8IElK6viBVspFJMLk8H9QqJ9w_kAizfsBCVGr5OctqoHLrw_OpytRWS7jZFcs9icbuAIFhk6N17AbwFQ; ph_phc_LG7IJbVJqBsk584rbcKca0D5lV2vHguiijDrVji7yDM_posthog=%7B%22distinct_id%22%3A%22e78a10c9-94c3-430f-992b-b9dd5189f4d2%22%2C%22%24sesid%22%3A%5B1750815482373%2C%220197a485-2b60-745a-907d-1dc48a37586b%22%2C1750811880288%5D%2C%22%24epp%22%3Atrue%2C%22%24initial_person_info%22%3A%7B%22r%22%3A%22%24direct%22%2C%22u%22%3A%22https%3A%2F%2Flmarena.ai%2F%22%7D%7D;

    // arena-auth-prod-v1=base64-eyJhY2Nlc3NfdG9rZW4iOiJleUpoYkdjaU9pSklVekkxTmlJc0ltdHBaQ0k2SWtOVFQwNHhkM05uU0hkRlNFTkNNbGNpTENKMGVYQWlPaUpLVjFRaWZRLmV5SnBjM01pT2lKb2RIUndjem92TDJoMWIyZDZiMlZ4ZW1OeVpIWnJkM1IyYjJScExuTjFjR0ZpWVhObExtTnZMMkYxZEdndmRqRWlMQ0p6ZFdJaU9pSXlNekl4WkRFME1DMWlOakEyTFRSbVpqVXRPR1UwWmkwME5tSTFOR0UzT1dFMU1UUWlMQ0poZFdRaU9pSmhkWFJvWlc1MGFXTmhkR1ZrSWl3aVpYaHdJam94TnpVd09ESTBNRFkxTENKcFlYUWlPakUzTlRBNE1qQTBOalVzSW1WdFlXbHNJam9pSWl3aWNHaHZibVVpT2lJaUxDSmhjSEJmYldWMFlXUmhkR0VpT250OUxDSjFjMlZ5WDIxbGRHRmtZWFJoSWpwN0ltbGtJam9pWlRjNFlURXdZemt0T1RSak15MDBNekJtTFRrNU1tSXRZamxrWkRVeE9EbG1OR1F5SW4wc0luSnZiR1VpT2lKaGRYUm9aVzUwYVdOaGRHVmtJaXdpWVdGc0lqb2lZV0ZzTVNJc0ltRnRjaUk2VzNzaWJXVjBhRzlrSWpvaVlXNXZibmx0YjNWeklpd2lkR2x0WlhOMFlXMXdJam94TnpVd09ERXhPRGcxZlYwc0luTmxjM05wYjI1ZmFXUWlPaUkyTkRRMU1qaGlOaTAyTnpCakxUUmpZakl0WWpSaE5pMDNZelJsWVRRNVltUmhabU1pTENKcGMxOWhibTl1ZVcxdmRYTWlPblJ5ZFdWOS5UZGhGLVg1V1hxaHZKZlFNaFJTUjFjRVBzcnlXdnlobFRfUE5qZ0lEd3JrIiwidG9rZW5fdHlwZSI6ImJlYXJlciIsImV4cGlyZXNfaW4iOjM2MDAsImV4cGlyZXNfYXQiOjE3NTA4MjQwNjUsInJlZnJlc2hfdG9rZW4iOiJ6ZDNqbGlwM2ZydW4iLCJ1c2VyIjp7ImlkIjoiMjMyMWQxNDAtYjYwNi00ZmY1LThlNGYtNDZiNTRhNzlhNTE0IiwiYXVkIjoiYXV0aGVudGljYXRlZCIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiZW1haWwiOiIiLCJwaG9uZSI6IiIsImxhc3Rfc2lnbl9pbl9hdCI6IjIwMjUtMDYtMjVUMDA6Mzg6MDUuNzk4MjUyWiIsImFwcF9tZXRhZGF0YSI6e30sInVzZXJfbWV0YWRhdGEiOnsiaWQiOiJlNzhhMTBjOS05NGMzLTQzMGYtOTkyYi1iOWRkNTE4OWY0ZDIifSwiaWRlbnRpdGllcyI6W10sImNyZWF0ZWRfYXQiOiIyMDI1LTA2LTI1VDAwOjM4OjA1Ljc5NjcwNVoiLCJ1cGRhdGVkX2F0IjoiMjAyNS0wNi0yNVQwMzowMTowNS4wMTI5NDFaIiwiaXNfYW5vbnltb3VzIjp0cnVlfX0; ph_phc_LG7IJbVJqBsk584rbcKca0D5lV2vHguiijDrVji7yDM_posthog=%7B%22distinct_id%22%3A%22e78a10c9-94c3-430f-992b-b9dd5189f4d2%22%2C%22%24sesid%22%3A%5B1750820468625%2C%220197a504-7a14-7035-9f20-b82ad33dec9a%22%2C1750820223508%5D%2C%22%24epp%22%3Atrue%2C%22%24initial_person_info%22%3A%7B%22r%22%3A%22%24direct%22%2C%22u%22%3A%22https%3A%2F%2Flmarena.ai%2F%22%7D%7D; _ga_72FK1TMV06=GS2.1.s1750820466$o2$g0$t1750820468$j58$l0$h0
    // arena-auth-prod-v1=base64-eyJhY2Nlc3NfdG9rZW4iOiJleUpoYkdjaU9pSklVekkxTmlJc0ltdHBaQ0k2SWtOVFQwNHhkM05uU0hkRlNFTkNNbGNpTENKMGVYQWlPaUpLVjFRaWZRLmV5SnBjM01pT2lKb2RIUndjem92TDJoMWIyZDZiMlZ4ZW1OeVpIWnJkM1IyYjJScExuTjFjR0ZpWVhObExtTnZMMkYxZEdndmRqRWlMQ0p6ZFdJaU9pSXlNekl4WkRFME1DMWlOakEyTFRSbVpqVXRPR1UwWmkwME5tSTFOR0UzT1dFMU1UUWlMQ0poZFdRaU9pSmhkWFJvWlc1MGFXTmhkR1ZrSWl3aVpYaHdJam94TnpVd09ERTFORGcxTENKcFlYUWlPakUzTlRBNE1URTRPRFVzSW1WdFlXbHNJam9pSWl3aWNHaHZibVVpT2lJaUxDSmhjSEJmYldWMFlXUmhkR0VpT250OUxDSjFjMlZ5WDIxbGRHRmtZWFJoSWpwN0ltbGtJam9pWlRjNFlURXdZemt0T1RSak15MDBNekJtTFRrNU1tSXRZamxrWkRVeE9EbG1OR1F5SW4wc0luSnZiR1VpT2lKaGRYUm9aVzUwYVdOaGRHVmtJaXdpWVdGc0lqb2lZV0ZzTVNJc0ltRnRjaUk2VzNzaWJXVjBhRzlrSWpvaVlXNXZibmx0YjNWeklpd2lkR2x0WlhOMFlXMXdJam94TnpVd09ERXhPRGcxZlYwc0luTmxjM05wYjI1ZmFXUWlPaUkyTkRRMU1qaGlOaTAyTnpCakxUUmpZakl0WWpSaE5pMDNZelJsWVRRNVltUmhabU1pTENKcGMxOWhibTl1ZVcxdmRYTWlPblJ5ZFdWOS43S1JvejBxNlFGSnBUM2VMR0U5SThDbEIwSVdYRU1BRlBDV2ZPbnpCaVJFIiwidG9rZW5fdHlwZSI6ImJlYXJlciIsImV4cGlyZXNfaW4iOjM2MDAsImV4cGlyZXNfYXQiOjE3NTA4MTU0ODUsInJlZnJlc2hfdG9rZW4iOiJ4eGNhNjJ0YjdleDMiLCJ1c2VyIjp7ImlkIjoiMjMyMWQxNDAtYjYwNi00ZmY1LThlNGYtNDZiNTRhNzlhNTE0IiwiYXVkIjoiYXV0aGVudGljYXRlZCIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiZW1haWwiOiIiLCJwaG9uZSI6IiIsImxhc3Rfc2lnbl9pbl9hdCI6IjIwMjUtMDYtMjVUMDA6Mzg6MDUuNzk4MjUyMTQ0WiIsImFwcF9tZXRhZGF0YSI6e30sInVzZXJfbWV0YWRhdGEiOnsiaWQiOiJlNzhhMTBjOS05NGMzLTQzMGYtOTkyYi1iOWRkNTE4OWY0ZDIifSwiaWRlbnRpdGllcyI6W10sImNyZWF0ZWRfYXQiOiIyMDI1LTA2LTI1VDAwOjM4OjA1Ljc5NjcwNVoiLCJ1cGRhdGVkX2F0IjoiMjAyNS0wNi0yNVQwMDozODowNS44MDA4NjdaIiwiaXNfYW5vbnltb3VzIjp0cnVlfX0; _ga_72FK1TMV06=GS2.1.s1750811880$o1$g1$t1750815482$j9$l0$h0
}
