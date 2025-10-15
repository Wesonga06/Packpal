/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

public class ProfileModel {
    private static String name = "John Doe";  // Static for simple global access
    private static String email = "user@example.com";
    private static String avatarPath = "";

    // Static Getters/Setters
    public static String getName() { return name; }
    public static void setName(String name) { ProfileModel.name = name; }

    public static String getEmail() { return email; }
    public static void setEmail(String email) { ProfileModel.email = email; }

    public static String getAvatarPath() { return avatarPath; }
    public static void setAvatarPath(String avatarPath) { ProfileModel.avatarPath = avatarPath; }
}