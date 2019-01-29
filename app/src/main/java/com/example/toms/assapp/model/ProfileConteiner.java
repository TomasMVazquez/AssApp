package com.example.toms.assapp.model;

import java.util.List;

public class ProfileConteiner {

    //Atributos
    private List<Profile> profileList;

    //constructor
    public ProfileConteiner(List<Profile> profileList) {
        this.profileList = profileList;
    }

    //getter
    public List<Profile> getProfileList() {
        return profileList;
    }

    //toString
    @Override
    public String toString() {
        return "ProfileConteiner{" +
                "profileList=" + profileList +
                '}';
    }
}
