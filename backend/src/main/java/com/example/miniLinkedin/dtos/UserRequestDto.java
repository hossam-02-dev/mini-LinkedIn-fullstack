package com.example.miniLinkedin.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder

public class UserRequestDto {
	
	@NotBlank(message = "Last name is required")
	private String lastName;
	
	@NotBlank(message = "First name is required")
	private String firstName;
	
	@NotBlank(message = "Email is required")
	@Email
	private String email;
	
	@NotBlank(message = "password   is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	@Pattern(
		    regexp = "^[a-zA-Z0-9^:/=!\\.\\s]+$",
		    message = "Format invalide"
		)
	private String password;
	
	
	@NotBlank(message = "Role is required")
	private String roleName;
	
	

}
