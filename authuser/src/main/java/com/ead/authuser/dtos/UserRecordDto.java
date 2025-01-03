package com.ead.authuser.dtos;

import com.ead.authuser.validations.PasswordConstraint;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRecordDto(

        @Size(groups = UserView.RegistrationPost.class,
                min = 4, max = 50, message = "Size must be between 4 and 50")
        @NotBlank(groups = UserView.RegistrationPost.class, message = "Username is mandatory")
        @JsonView(UserView.RegistrationPost.class)
        String username,

        @Email(groups = UserView.RegistrationPost.class, message = "Email must be in the expected format")
        @NotBlank(groups = UserView.RegistrationPost.class, message = "Email is mandatory")
        @JsonView(UserView.RegistrationPost.class)
        String email,

        @NotBlank(groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class}, message = "Password is mandatory")
        @Size(groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class},
                min = 6, max = 20, message = "Size must be between 6 and 20")
        @PasswordConstraint(groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class})
        @JsonView({UserView.RegistrationPost.class, UserView.PasswordPut.class})
        String password,

        @NotBlank(groups = UserView.PasswordPut.class, message = "Old Password is mandatory")
        @Size(groups = UserView.PasswordPut.class,
                min = 6, max = 20, message = "Size must be between 6 and 20")
        @PasswordConstraint(groups = UserView.PasswordPut.class)
        @JsonView(UserView.PasswordPut.class)
        String oldPassword,

        @NotBlank(groups = {UserView.RegistrationPost.class, UserView.UserPut.class}, message = "Full Name is mandatory")
        @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
        String fullName,

        @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
        String phoneNumber,

        @NotBlank(groups = UserView.ImagePut.class, message = "Image URL is mandatory")
        @JsonView(UserView.ImagePut.class)
        String imageUrl) {

    public interface UserView {

        interface RegistrationPost {
        }

        interface UserPut {
        }

        interface PasswordPut {
        }

        interface ImagePut {
        }

    }

}
