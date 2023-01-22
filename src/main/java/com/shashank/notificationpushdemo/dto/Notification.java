package com.shashank.notificationpushdemo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
    private long id;
    private String message;
}
