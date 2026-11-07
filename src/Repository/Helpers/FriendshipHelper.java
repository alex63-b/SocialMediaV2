package Repository.Helpers;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;

import java.io.InvalidObjectException;
import java.security.KeyException;
import java.util.Optional;

/**
 * This class is made to provide helping functions for the concrete friendship repositories with observers, so the
 * validity of the data can be kept.
 */
public class FriendshipHelper {
    private static FriendshipHelper helper = null;

    private FriendshipHelper(){}

    /**
     * This static function returns the current friendship helper, and creates a new one if it doesn't exist.
     * @return A new friendship helper that is ready for use.
     */
    static public FriendshipHelper getFriendshipHelper(){
        if(helper == null){
            helper = new FriendshipHelper();
        }
        return helper;
    }

    private void checkEqualFriendships(Friendship f1, Friendship f2) throws RepositoryException {
        if (f1.getFriend1().equals(f2.getFriend1()) && f1.getFriend2().equals(f2.getFriend2())) {
            throw new RepositoryException("There can't be more than one friendship with the same friends!",new KeyException());
        }
        if (f1.getFriend1().equals(f2.getFriend2()) && f1.getFriend2().equals(f2.getFriend1())) {
            throw new RepositoryException("There can't be more than one friendship with the same friends!",new KeyException());
        }
    }

    /**
     * This function checks if a friendship is not in the repository yet, if it is made between two existing users and if the
     * friends that are in the new friendship have the same data as those in the repository. Otherwise, an exception will be
     * thrown.
     * @param new_element the new friendship which is validated;
     * @param user_repo a user repository in which the users are checked if they are the ones in the friendship;
     * @param friendshipRepo a friendship repository which represents the repository in which the new friendship is validated;
     * @throws RepositoryException if the friendship already is in the repository, if the friendship isn't between 2 users in the
     * user repository, or if the users in the friendship aren't the same as the users in the user repository.
     */
    public void validateRelations(Friendship new_element, Repository<Long, User> user_repo, Repository<Pair<Long>, Friendship> friendshipRepo){
        friendshipRepo.findAll(DefaultFilters::filterTrue)
                .forEach(f1 -> checkEqualFriendships(f1, new_element));
        User friend1 = new_element.getFriend1();
        User friend2 = new_element.getFriend2();
        if(user_repo.findElement(friend1.getId()).isPresent() && user_repo.findElement(friend2.getId()).isPresent()){
            User trueFriend1 = user_repo.findElement(friend1.getId()).get();
            User trueFriend2 = user_repo.findElement(friend2.getId()).get();
            if(!trueFriend1.equalsEveryField(friend1)){
                throw new RepositoryException("The first friend in the friendship has wrong data for its ID!",new KeyException());
            }
            if(!trueFriend2.equalsEveryField(friend2)){
                throw new RepositoryException("The second friend in the friendship has wrong data for its ID!",new KeyException());
            }
        }
        else{
            throw new RepositoryException("The added friendship isn't between 2 existing users!",new InvalidObjectException(""));
        }
    }

    /**
     * This function updates the friends of the old users and of the new users when a friendship is modified.
     * @param new_element the new friendship;
     * @param oldFriend1 the first old friend;
     * @param oldFriend2 the second old friend;
     */
    public void updateFriends(Friendship new_element, User oldFriend1, User oldFriend2){
        if(!oldFriend1.equals(new_element.getFriend1())){
            oldFriend1.removeFriend(oldFriend2);
            new_element.getFriend1().addFriend(new_element.getFriend2());
        }
        if(!oldFriend2.equals(new_element.getFriend2())){
            oldFriend2.removeFriend(oldFriend1);
            new_element.getFriend2().addFriend(new_element.getFriend1());
        }
    }

    /**
     * This function is used to help with the update after a notification from the observable object. It handles deletion and
     * modification anomalies(when modification/deletion happens in the user repository).
     * @param userRepo a user repository from where the friendships take their users;
     * @param friendshipRepo a friendship repository where the anomalies are handled;
     */
    public void updateHelper(Repository<Long,User> userRepo, Repository<Pair<Long>,Friendship> friendshipRepo){
        friendshipRepo.findAll(DefaultFilters::filterTrue).stream()
                .map((f) -> {
                    Long friendId1 = f.getFriend1().getId();
                    Long friendId2 = f.getFriend2().getId();
                    if(userRepo.findElement(friendId1).isPresent() && userRepo.findElement(friendId2).isPresent()){
                        return Optional.<Pair<Long>>empty();
                    }
                    else{
                        return Optional.of(f.getId());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(friendshipRepo::deleteElement);
        //deleteList.stream().forEach(friendshipRepo::deleteElement);
        friendshipRepo.findAll(DefaultFilters::filterTrue).forEach((f) -> {
            Long friendId1 = f.getFriend1().getId();
            Long friendId2 = f.getFriend2().getId();
            if(userRepo.findElement(friendId1).isPresent() && userRepo.findElement(friendId2).isPresent()){
                User newFriend1 = userRepo.findElement(friendId1).get();
                User newFriend2 = userRepo.findElement(friendId2).get();
                if(!f.getFriend1().equalsEveryField(newFriend1) || !f.getFriend2().equalsEveryField(newFriend2)){
                    friendshipRepo.modifyElement(new Friendship(newFriend1,newFriend2,f.getDate()));
                }
            }
        });

    }
}
