package UI.Console;

import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Repository.Exceptions.RepositoryException;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.UserServices.ConsoleUserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the application's interactive console menu.
 */
public class BasicConsoleMenu extends ConsoleMenu{
    /**
     * This function creates a menu using the specified user and friendship services.
     * @param userService the service used for user operations;
     * @param friendshipService the service used for friendship operations;
     */
    public BasicConsoleMenu(ConsoleUserService userService, ConsoleFriendshipService friendshipService){
        super(userService,friendshipService);
    }

    @Override
    protected void printMenu(){
        System.out.println("Choose an option from the menu:");
        System.out.println("1. Show all users;");
        System.out.println("2. Add a new user;");
        System.out.println("3. Delete an existing user;");
        System.out.println("4. Show all friendships;");
        System.out.println("5. Add a new friendship;");
        System.out.println("6. Delete an existing friendship;");
        System.out.println("7. Show community count;");
        System.out.println("8. Show the most sociable community;");
        System.out.println("9. Filter the users by name;");
        System.out.println("10. Show a user's friendships;");
        System.out.println("11. Exit the application;");
    }
    @Override
    protected void showUsers(){
        System.out.println(((ConsoleUserService) userService).showAllUsers());
    }

    @Override
    protected void showFriendships() {
        System.out.println(((ConsoleFriendshipService) friendshipService).showAllFriendships());
    }

    @Override
    protected void showUserFriendships(){
        Long idUser = readNumber("Enter the ID of the user: ","The ID must be a number, please try again!");
        List<User> friends = friendshipService.getUserFriendships(idUser);
        if(friends.isEmpty()){
            System.out.println("You don't have any friends :(");
        }
        else{
            System.out.println("Your friends are:");
            friends.forEach(System.out::println);
        }
    }
}
