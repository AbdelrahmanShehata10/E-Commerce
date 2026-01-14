package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;




    @Data
    @Builder
    public class Register_DTO {

        private String username;
        private String age;
        private String email;
        private String password;
        private String gender;


    }

