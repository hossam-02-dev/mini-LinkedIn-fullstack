package com.example.miniLinkedin.dtos;

import com.example.miniLinkedin.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class AuthResponseDto {
	private Long id;	
private String token;
private String firstName;
private String refreshToken;
private String lastName;
private String email;
private Role role;
}
