package UI.Console;

import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Repository.Exceptions.RepositoryException;
import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.UserServices.BasicUserService;
import Service.UserServices.ConsoleUserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface defines the operations of a console menu.
 */
public abstract class ConsoleMenu {
    protected final static BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
    protected final BasicUserService userService;
    protected final BasicFriendshipService friendshipService;
    /**
     * This function prints the available menu options.
     */
    public ConsoleMenu(BasicUserService userService, BasicFriendshipService friendshipService){
        this.userService = userService;
        this.friendshipService = friendshipService;
    }
    protected abstract void printMenu();
    protected Long readNumber(String message, String errorMessage){
        while(true) {
            try {
                System.out.println(message);
                String id = read.readLine();
                return Long.parseLong(id);
            }
            catch (NumberFormatException e){
                System.out.println(errorMessage);
            }
            catch (IOException e) {
                System.out.println("Oops, something went wrong with the read.");
            }
        }
    }

    /**
     * This function prints the available menu options.
     */
    protected abstract void showUsers();

    private void addUser(){
        try {
            String name,firstName;
            System.out.println("Enter the name of the new user: ");
            name = read.readLine();
            System.out.println("Enter the first name of the new user: ");
            firstName = read.readLine();
            userService.addUser(name,firstName);
            System.out.println("The user has been added!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
        catch (IOException e){
            System.out.println("Oops, something went wrong with the read.");
        }
    }
    private void modifyUser(){
        try {
            String name,firstName;
            Long idNumber = readNumber("Enter the id of the user to update: ","The ID must be a number, please try again!");
            System.out.println("Enter the name of the new user: ");
            name = read.readLine();
            System.out.println("Enter the first name of the new user: ");
            firstName = read.readLine();
            userService.updateUser(idNumber,name,firstName);
            System.out.println("The user has been updated!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
        catch (IOException e){
            System.out.println("Oops, something went wrong with the read.");
        }
    }
    private void deleteUser(){
        try{
            Long idNumber = readNumber("Enter the id of the user to delete: ","The ID must be a number, please try again!");
            userService.deleteUser(idNumber);
            System.out.println("The user has been deleted!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
    }
    protected abstract void showFriendships();
    private void addFriendship(){
        try {
            Long id1 = readNumber("Enter the ID of the first friend: ", "The ID must be a number, please try again!");
            Long id2 = readNumber("Enter the ID of the second friend: ", "The ID must be a number, please try again!");
            friendshipService.addFriendship(id1,id2);
            System.out.println("The friendship has been added!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
    }
    private void modifyFriendship(){
        try {
            Long id1 = readNumber("Enter the ID of the first friend: ", "The ID must be a number, please try again!");
            Long id2 = readNumber("Enter the ID of the second friend: ", "The ID must be a number, please try again!");
            System.out.println("Insert the date in ISO format:");
            String date = read.readLine();
            friendshipService.updateFriendship(id1,id2,date);
            System.out.println("The friendship has been modified!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
        catch (IOException e){
            System.out.println("Something went wrong with the console!");
        }
    }
    private void deleteFriendship(){
        try{
            System.out.println("To delete a friendship, you must introduce the ID's of the friends in any order.");
            Long idFriend1 = readNumber("Enter the ID of the first friend: ","The ID must be a number, please try again!");
            Long idFriend2 = readNumber("Enter the ID of the second friend: ","The ID must be a number, please try again!");
            friendshipService.deleteFriendship(idFriend1,idFriend2);
            System.out.println("The friendship has been deleted!");
        }
        catch (ValidationException | RepositoryException e){
            System.out.println(e.getMessage());
        }
    }
    private void showCommunityCount(){
        System.out.println("There are " + friendshipService.communityCount() + " communities in this network.");
    }
    private void showMostSociableCommunity(){

        ArrayList<User> community = friendshipService.getMostSociableCommunity();
        if(community.isEmpty()){
            System.out.println("There aren't any communities in this social media!");
        }
        else {
            System.out.println("The most sociable community is:\n");
            community.forEach(System.out::println);
        }
    }
    private void showUsersWithName(){
        try {
            System.out.println("The users will be filtered after their name, their name must contain the given string from the console.");
            System.out.println("Insert the string after which the users will be filtered: ");
            String nameFragment = read.readLine();
            List<User> community = userService.searchByName(nameFragment);
            if(community.isEmpty()){
                System.out.println("No users have been found!");
            }
            else {
                community.forEach(System.out::println);
            }
        }
        catch (IOException e) {
            System.out.println("Something went wrong with the read!");
        }
    }
    protected abstract void showUserFriendships();
    /**
     * This function runs the menu's main interaction loop.
     */
    public void mainLoop(){
        boolean appStopped = false;
        String input = "";
        while(!appStopped) {
            printMenu();
            System.out.println("Enter the desired option: ");
            try {
                input = read.readLine();
            } catch (IOException e) {
                System.out.println("Oops, something went wrong with the read.");
                input = "";
            }
            switch (input) {
                case "1" -> showUsers();
                case "2" -> addUser();
                case "3" -> modifyUser();
                case "4" -> deleteUser();
                case "5" -> showFriendships();
                case "6" -> addFriendship();
                case "7" -> modifyFriendship();
                case "8" -> deleteFriendship();
                case "9" -> showCommunityCount();
                case "10" -> showMostSociableCommunity();
                case "11" -> showUsersWithName();
                case "12" -> showUserFriendships();
                case "13" -> appStopped = true;
                default -> System.out.println("You did not enter a valid option, please try again.");
            }
        }
    }
}
