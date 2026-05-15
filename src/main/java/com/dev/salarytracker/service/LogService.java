package com.dev.salarytracker.service;

import com.dev.salarytracker.entity.ActivityLog;
import com.dev.salarytracker.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    @Autowired
    private ActivityLogRepository logRepository;

    public void saveLog(String username, String action, String status, String details, String ip){
        ActivityLog log = new ActivityLog();
        log.setUsername(username);
        log.setAction(action);
        log.setStatus(status);
        log.setDetails(details);
        log.setIpAddress(ip);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }
}
