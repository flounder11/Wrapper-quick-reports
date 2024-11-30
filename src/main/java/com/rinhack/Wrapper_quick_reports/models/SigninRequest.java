package com.rinhack.Wrapper_quick_reports.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {
    private String username;
    private String password;
}
