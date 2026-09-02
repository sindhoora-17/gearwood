package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.dtos.RegisterDto;

public interface AuthService {

    void registerUser(RegisterDto registerDto);
}