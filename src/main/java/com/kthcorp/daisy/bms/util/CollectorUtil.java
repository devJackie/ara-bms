package com.kthcorp.daisy.bms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Component
public class CollectorUtil {

    private final ApplicationContext context;

    @Autowired
    public CollectorUtil(ApplicationContext context) {
        this.context = context;
    }

    public static void quietlyClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void quietlyClose(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void quietlyClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static long getRowCount(File file) throws IOException {
        int count = 0;
        int ch;
        if (file.length() != 0) {
            count++;
        }
        try (BufferedReader rdr = new BufferedReader(new FileReader(file))) {
            while ((ch = rdr.read()) > -1) {
                Character c = new Character((char) ch);
                if (c.equals('\n')) {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static String createJsonArray(List<String> stringList) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String string : stringList) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder.build().toString();
    }

    public <T> T getInstanceWithContext(Class<T> cls, Map<T, T> config) throws Exception {
        T metrics;
        Constructor<? extends T> constructor = cls.getConstructor(Map.class);
        if (constructor != null)
            metrics = constructor.newInstance(config);
        else
            metrics = cls.getConstructor().newInstance();
        return metrics;
    }
}
