package model;

import java.time.LocalDate;

public class User {

    // fields
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String gender;
    private String country;
    private String email;
    private String phone;
    private LocalDate birthday;
    private LocalDate registered;
    private GamerListElement gamerList;
    private boolean admin;

    private String DUMMY;

    public User(){


    }

    public User(String username){
        this.username=username;
        this.admin = false;
        //TO_DO populate other fields querying db with username
    }

    // setter e getter

    public void setId(int id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setGender(String gender) {this.gender = gender;}
    public void setCountry(String country) {this.country = country;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setBirthday(LocalDate birthday) {this.birthday = birthday;}
    public void setRegistered(LocalDate registered) {this.registered = registered;}
    public void setAdmin(boolean admin) {this.admin = admin;}

    public void setDUMMY(String DUMMY) {this.DUMMY = DUMMY;}

    public int getId() {return id;}
    public String getUsername() {return username;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getGender() {return gender;}
    public String getCountry() {return country;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public LocalDate getBirthday() {return birthday;}
    public LocalDate getRegistered() {return registered;}
    public boolean getAdmin(){return admin;}
    public String getDUMMY() {return DUMMY;}

    // metodi statici che comunicano con il backend


}
