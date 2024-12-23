package com.reliaquest.api.client;

import static com.reliaquest.api.common.Constants.APPLICATION_JSON;
import static com.reliaquest.api.common.Constants.BASE_URL;

import com.reliaquest.api.config.OkHttpConfig;
import java.io.IOException;
import okhttp3.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceClient {

    private final OkHttpClient client;

    public EmployeeServiceClient() {
        this.client = OkHttpConfig.configureClient();
    }

    public Response get(@NonNull final String path) throws IOException {
        Request request = new Request.Builder().url(BASE_URL + path).get().build();
        return client.newCall(request).execute();
    }

    public Response post(@NonNull final String path, @NonNull final String body) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .post(RequestBody.create(body, MediaType.parse(APPLICATION_JSON)))
                .build();
        return client.newCall(request).execute();
    }

    public Response delete(@NonNull final String path, @NonNull final String body) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .delete(RequestBody.create(body, MediaType.parse(APPLICATION_JSON)))
                .build();
        return client.newCall(request).execute();
    }
}
