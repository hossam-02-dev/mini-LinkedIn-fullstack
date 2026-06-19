package com.example.miniLinkedin.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor  @NoArgsConstructor @Getter @Setter @Builder

public class UserResponseDto {
	
	private Long id;
	private String lastName;
	private String firstName;
	private String email;
	private String roleName;
	private Boolean isActive;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

}
