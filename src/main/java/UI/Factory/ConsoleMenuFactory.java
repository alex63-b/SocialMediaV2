package UI.Factory;

import util.datatypes.Pair;
import Repository.Exceptions.ConfigException;
import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.FriendshipServices.PageFriendshipService;
import Service.UserServices.BasicUserService;
import Service.UserServices.ConsoleUserService;
import Service.UserServices.PageUserService;
import UI.Console.BasicConsoleMenu;
import UI.Console.ConsoleMenu;
import UI.Console.PageConsoleMenu;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Creates a console menu whose type matches the supplied services.
 */
public class ConsoleMenuFactory {
    private final static Map<MenuType, BiFunction<BasicUserService, BasicFriendshipService, ConsoleMenu>> consoleFactory= Map.of(
            MenuType.BASIC, (userService, friendshipService) -> {
                if(userService instanceof ConsoleUserService && friendshipService instanceof ConsoleFriendshipService){
                    return new BasicConsoleMenu((ConsoleUserService) userService, (ConsoleFriendshipService) friendshipService);
                }
                else{
                    throw new ConfigException("The menu doesn't have the same type as the service!");
                }
            },
            MenuType.PAGE, (userService, friendshipService) -> {
                if(userService instanceof PageUserService && friendshipService instanceof PageFriendshipService){
                    return new PageConsoleMenu((PageUserService) userService, (PageFriendshipService) friendshipService);
                }
                else{
                    throw new ConfigException("The menu doesn't have the same type as the service!");
                }
            }
    );
    private final static Map<Pair<Class<? extends BasicUserService>, Class<? extends BasicFriendshipService>>,MenuType> userMenuBridge = Map.of(
            new Pair<>(ConsoleUserService.class, ConsoleFriendshipService.class), MenuType.BASIC,
            new Pair<>(PageUserService.class, PageFriendshipService.class), MenuType.PAGE
    );
    /**
     * Creates a basic or paged console menu for the supplied services.
     *
     * @param userService       the user service
     * @param friendshipService the friendship service
     * @return a matching console menu
     * @throws ConfigException if the service types do not have a matching menu
     */
    public static ConsoleMenu createMenu(BasicUserService userService, BasicFriendshipService friendshipService){
        Pair<Class<? extends BasicUserService>, Class<? extends BasicFriendshipService>> serviceClassPair =
                new Pair<>(userService.getClass(),friendshipService.getClass());
        if(userMenuBridge.containsKey(serviceClassPair)){
            MenuType menuType = userMenuBridge.get(serviceClassPair);
            return consoleFactory.get(menuType).apply(userService,friendshipService);
        }
        else{
            throw new ConfigException("The service types do not match with a configuration of the menu!");
        }
    }
}
