package com.andersenlab.assesment.exception.model;

public interface ErrorMessage {

    String ERR001_MESSAGE = "Invalid size";
    String ERR002_MESSAGE = "This field is mandatory and can't be empty or null";
    String ERR003_MESSAGE = "Invalid request parameter";
    String ERR004_MESSAGE = "Resource can not be found";
    String ERR005_MESSAGE = "Resource already exists";
    String ERR006_MESSAGE = "Date of birth should not be in the future";
    String ERR007_MESSAGE = "Registration failed";
    String ERR008_MESSAGE = "Email format is invalid";
    String ERR009_MESSAGE = "Permission denied. Action not allowed.";
    String ERR010_MESSAGE = "User deletion failed";
}
