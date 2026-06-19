package com.example.miniLinkedin.dtos;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProfileStatsDto {
    private long totalViewsLast30Days;
    private List<ViewerDto> recentViewers;

    @Data
    @Builder
    public static class ViewerDto {
        private String firstName;
        private String lastName;
        private String title; // "name" dans votre ProfilEntity
        private String timeAgo;
    }
}