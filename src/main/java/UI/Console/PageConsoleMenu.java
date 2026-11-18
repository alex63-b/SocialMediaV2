package UI.Console;

import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.PageFriendshipService;
import Service.UserServices.BasicUserService;
import Service.UserServices.PageUserService;

import java.io.IOException;
import java.util.Objects;

/**
 * Console menu that displays users and friendships in configurable pages.
 */
public class PageConsoleMenu extends ConsoleMenu{
    private final PageFriendshipService fService;
    private final PageUserService uService;
    /**
     * This function creates a menu using the specified user and friendship services.
     * @param userService the service used for user operations, must have page support;
     * @param friendshipService the service used for friendship operations, must have page support;
     */
    public PageConsoleMenu(PageUserService userService, PageFriendshipService friendshipService) {
        super(userService, friendshipService);
        uService = userService;
        fService = friendshipService;
    }

    @Override
    protected void printMenu(){
        System.out.println("Choose an option from the menu:");
        System.out.println("1. Show all users(separated by pages);");
        System.out.println("2. Add a new user;");
        System.out.println("3. Delete an existing user;");
        System.out.println("4. Show all friendships(separated by pages);");
        System.out.println("5. Add a new friendship;");
        System.out.println("6. Delete an existing friendship;");
        System.out.println("7. Show community count;");
        System.out.println("8. Show the most sociable community;");
        System.out.println("9. Filter the users by name;");
        System.out.println("10. Show a user's friendships(separated by pages);");
        System.out.println("11. Exit the application;");
    }
    @Override
    protected void showUsers() {
        try {
            int pageNumber = 0;
            String option = "?";
            while (!Objects.equals(option, "q")) {
                System.out.println("You are on page " + pageNumber);
                uService.findOnPage(pageNumber).forEach(System.out::println);
                option = "";
                while(option.isEmpty()) {
                    System.out.println("Insert the next option: n(next page), p(previous page), or q(quit)");
                    option = read.readLine();
                    switch (option){
                        case "n" -> {
                            pageNumber += 1;
                        }
                        case "p" -> {
                            if(pageNumber > 0){
                                pageNumber -= 1;
                            }
                            else{
                                System.out.println("You can't go to negative pages!");
                            }
                        }
                        case "q" -> {

                        }
                        default -> {
                            System.out.println("Please try again!");
                            option = "";
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Oops, something went wrong with the console!");
        }
    }

    @Override
    protected void showFriendships() {
        try {
            int pageNumber = 0;
            String option = "?";
            while (!Objects.equals(option, "q")) {
                System.out.println("You are on page " + pageNumber);
                fService.findOnPage(pageNumber).forEach(System.out::println);
                option = "";
                while(option.isEmpty()) {
                    System.out.println("Insert the next option: n(next page), p(previous page), or q(quit)");
                    option = read.readLine();
                    switch (option){
                        case "n" -> {
                            pageNumber += 1;
                        }
                        case "p" -> {
                            if(pageNumber > 0){
                                pageNumber -= 1;
                            }
                            else{
                                System.out.println("You can't go to negative pages!");
                            }
                        }
                        case "q" -> {

                        }
                        default -> {
                            System.out.println("Please try again!");
                            option = "";
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Oops, something went wrong with the console!");
        }
    }

    @Override
    protected void showUserFriendships() {
        try {
            Long idNumber = readNumber("Insert the ID of the user for which you want to see their friends: ","The ID must be a number, please try again!");
            int pageNumber = 0;
            String option = "?";
            while (!Objects.equals(option, "q")) {
                System.out.println("You are on page " + pageNumber);
                fService.getUserFriendships(idNumber,pageNumber).forEach(System.out::println);
                option = "";
                while(option.isEmpty()) {
                    System.out.println("Insert the next option: n(next page), p(previous page), or q(quit)");
                    option = read.readLine();
                    switch (option){
                        case "n" -> {
                            pageNumber += 1;
                        }
                        case "p" -> {
                            if(pageNumber > 0){
                                pageNumber -= 1;
                            }
                            else{
                                System.out.println("You can't go to negative pages!");
                            }
                        }
                        case "q" -> {

                        }
                        default -> {
                            System.out.println("Please try again!");
                            option = "";
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Oops, something went wrong with the console!");
        }
    }
}
