package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsonUtils {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter D_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override public void write(JsonWriter out, LocalDateTime value) throws IOException {
                out.value(value != null ? value.format(DT_FMT) : null);
            }
            @Override public LocalDateTime read(JsonReader in) throws IOException {
                String s = in.nextString();
                return s != null ? LocalDateTime.parse(s, DT_FMT) : null;
            }
        })
        .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
            @Override public void write(JsonWriter out, LocalDate value) throws IOException {
                out.value(value != null ? value.format(D_FMT) : null);
            }
            @Override public LocalDate read(JsonReader in) throws IOException {
                String s = in.nextString();
                return s != null ? LocalDate.parse(s, D_FMT) : null;
            }
        })
        .create();

    private JsonUtils() {
    }

    public static void enviarJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush();
        }
    }

    public static void enviarError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(Map.of("error", mensaje)));
            out.flush();
        }
    }

    public static <T> T parsearJson(HttpServletRequest request, Class<T> clase) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), clase);
    }

    public static Map<String, Object> parsearMapa(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(sb.toString(), type);
    }
}
