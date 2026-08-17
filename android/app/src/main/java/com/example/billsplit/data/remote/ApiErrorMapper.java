package com.example.billsplit.data.remote;

import org.json.JSONObject;

import retrofit2.Response;

//Shared file by all the repos to handle API errors
public final class ApiErrorMapper {

    private ApiErrorMapper() {
    }

    public static String extractServerErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                JSONObject json = new JSONObject(response.errorBody().string());
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception e) {
            // fall through to generic message below 
            //Exception handling for JSON parsing error
        }
        return "Something went wrong (HTTP " + response.code() + ")";
    }

    public static String networkErrorMessage(Throwable t) {
        if (t instanceof java.net.SocketTimeoutException) {
            return "Request timed out : the server may be waking up, try again in a moment";
        }
        return "Could not reach the server. Check your connection.";
    }
}
