package org.QTS.stresstest;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class LoginTracker {
    public ConcurrentHashMap<String, Integer> loginRequestCounts;
    public ConcurrentHashMap<String, LocalDateTime> LoginRequestTime;
    public ConcurrentHashMap<String, Boolean> loginResponseResults;
    public ConcurrentHashMap<String, LocalDateTime> LoginResponeTime;
    public LocalDateTime StartRequestTime;
    public LocalDateTime LastResponeTime;
    @EzyAutoBind
    @Setter
    @Getter
    public boolean LoginDone;

    public LoginTracker() {
        loginRequestCounts = new ConcurrentHashMap<>();
        loginResponseResults = new ConcurrentHashMap<>();
        LoginRequestTime = new ConcurrentHashMap<>();
        LoginResponeTime = new ConcurrentHashMap<>();
    }
    public void trackLoginRequest(String username) {
        loginRequestCounts.merge(username, 1, Integer::sum);
        if(!LoginRequestTime.contains(username)) {
            LocalDateTime usertimelogin = LocalDateTime.now();
            if (StartRequestTime == null) StartRequestTime = usertimelogin;

            double seconds = Duration.between(StartRequestTime, usertimelogin).toNanos();

            if (seconds > 0) StartRequestTime = usertimelogin;
            LoginRequestTime.put(username,usertimelogin);
            double requestPerS = calculateRequestsPerSecond(this,username);
            System.out.println("=================RequestSeconds=====================");
            System.out.println(requestPerS);
        }
    }

    public void trackLoginResponse(String username, boolean isSuccess) {
        if(!loginResponseResults.containsKey(username)) loginResponseResults.put(username, isSuccess);
    }

    public void trackTimeLoginRespone(String username){
        Boolean isSuccess =  loginResponseResults.get(username);
        if (isSuccess) return;
        LoginResponeTime.put(username,LocalDateTime.now());
        loginResponseResults.replace(username,isSuccess, !isSuccess);
        double responePerS = calculateResponsesPerSecond(this,username);
        System.out.println("================ResponsesPerSecond===========================");
        System.out.println(responePerS);

        int totalRequests = loginRequestCounts.size();
        int totalRespone = LoginResponeTime.size();

        double PercentOfRespone = totalRespone/totalRequests;
        System.out.println("================ResponsesPerRequests===========================");
        System.out.println(PercentOfRespone);

    }

    public int getLoginRequestCount(String username) {
        return loginRequestCounts.getOrDefault(username, 0);
    }

    public LocalDateTime getLoginRequestTime(String username) {
        return LoginRequestTime.get(username);
    }
    public LocalDateTime getLoginResponseTime(String username) {
        return LoginResponeTime.get(username);
    }

    public boolean getLoginResponseResult(String username) {
        return loginResponseResults.getOrDefault(username, false);
    }

    public static double calculateRequestsPerSecond(LoginTracker loginTracker, String username) {
        int totalRequests = loginTracker.loginRequestCounts.size();
        LocalDateTime startTime = loginTracker.StartRequestTime;
        LocalDateTime endTime = LocalDateTime.now();
        double seconds = Duration.between(startTime, endTime).toNanos() / 1_000_000_000.0;

        System.out.println("===================RequestSecond===========================");
        System.out.println(seconds);
        return totalRequests / seconds;
    }

    public static double calculateResponsesPerSecond(LoginTracker loginTracker, String username) {
        int totalResponses = loginTracker.LoginResponeTime.size();
        LocalDateTime startTime = loginTracker.StartRequestTime;
        LocalDateTime endTime =loginTracker.getLoginResponseTime(username);
        double seconds = Duration.between(startTime, endTime).toNanos() / 1_000_000_000.0;
        System.out.println("================ResponsesSecond===========================");
        System.out.println(seconds);
        return totalResponses / seconds;
    }
}

