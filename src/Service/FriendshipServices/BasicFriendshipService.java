package Service.FriendshipServices;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;
import Repository.Helpers.DefaultSuppliers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class provides operations for managing friendships and analyzing user communities.
 */
public class BasicFriendshipService {
    protected final Repository<Pair<Long>, Friendship> friendshipRepo;
    private final Repository<Long, User> userRepo;

    /**
     * This function creates a friendship service using the specified repositories.
     * @param repo the repository used to store and retrieve friendships;
     * @param userRepo the repository used to store and retrieve users;
     */
    public BasicFriendshipService(Repository<Pair<Long>, Friendship> repo, Repository<Long, User> userRepo) {
        this.friendshipRepo = repo;
        this.userRepo = userRepo;
    }

    /**
     * This function creates a friendship between two existing users using the current date and time.
     * @param userId1 the ID of the first user;
     * @param userId2 the ID of the second user;
     * @throws ValidationException if either user does not exist;
     * @throws RepositoryException if the friendship cannot be added to the repository;
     */
    public void addFriendship(Long userId1, Long userId2){
        if(userRepo.findElement(userId1).isEmpty() || userRepo.findElement(userId2).isEmpty()){
            throw new ValidationException("The users with these ID's don't exist in the list of users!");
        }
        User friend1 = userRepo.findElement(userId1).get();
        User friend2 = userRepo.findElement(userId2).get();
        Friendship new_friendship = new Friendship(friend1,friend2,LocalDateTime.now());
        friendshipRepo.addElement(new_friendship);
    }

    /**
     * This function removes the friendship between two users.
     * @param friendId1 the ID of the first user;
     * @param friendId2 the ID of the second user;
     * @throws ValidationException if the friendship does not exist;
     */
    public void deleteFriendship(Long friendId1, Long friendId2){
        if(friendshipRepo.deleteElement(new Pair<>(friendId1,friendId2)).isEmpty()){
            throw new ValidationException("The ID's of the friends don't exist!");
        }
    }

    private void DFS(Map<Long,Long> idTracker, Long startID, Long componentCount){
        idTracker.put(startID,componentCount);
        if(userRepo.findElement(startID).isPresent()) {
            User currentUser = userRepo.findElement(startID).get();
            currentUser.getFriends().stream()
                    .filter((friend) -> idTracker.get(friend.getId()) == 0)
                    .forEach((friend) -> DFS(idTracker, friend.getId(), componentCount));
        }
    }

    /**
     * This function counts the connected communities in the user network.
     * @return the number of connected communities;
     * @throws RepositoryException if a user referenced by a friendship cannot be found;
     */
    public int communityCount(){
        Map<Long,Long> idTracker = userRepo.findAll(DefaultFilters::filterTrue).stream()
                                              .collect(Collectors.toMap(User::getId, DefaultSuppliers::get0));
        idTracker.keySet()
                .forEach((id) -> {
                    if(idTracker.get(id) == 0) {
                        DFS(idTracker, id, id);
                    }
                });
        return idTracker.values().stream()
                .collect(Collectors.groupingBy(DefaultSuppliers::getIdentity)).size();
    }

    /**
     * This function returns the community with the greatest shortest-path distance between its members.
     * @return the users in the most sociable community, or an empty list if no friendships exist;
     * @throws RepositoryException if a user referenced by a friendship cannot be found;
     */
    public ArrayList<User> getMostSociableCommunity(){
        Map<Long,Integer> idToPosition = new HashMap<>();
        Map<Integer,Long> positionToId = new HashMap<>();

        List<User> users = userRepo.findAll(DefaultFilters::filterTrue);

        IntStream.range(0,users.size()).forEach(position -> {
            User u = users.get(position);
            idToPosition.put(u.getId(),position);
            positionToId.put(position, u.getId());
        });
        int position = users.size();

        int maxLenRoad = 0;
        int maxPosition = -1;
        final int finalPosition = position;
        int[][] adjacencyMatrix = new int[position][position];
        if(friendshipRepo.size() > 0){
            maxLenRoad = 1;
            maxPosition = idToPosition.get(friendshipRepo.findAll(DefaultFilters::filterTrue).getFirst().getFriend1().getId());
        }
        friendshipRepo.findAll(DefaultFilters::filterTrue).forEach((f) -> {
            int i = idToPosition.get(f.getFriend1().getId());
            int j = idToPosition.get(f.getFriend2().getId());
            adjacencyMatrix[i][j] = 1;
            adjacencyMatrix[j][i] = 1;
        });
        int len_max = 1000000000;
        IntStream.range(0,finalPosition).forEach((i) -> {
            IntStream.range(0,finalPosition)
                    .filter((j) -> i != j && adjacencyMatrix[i][j] != 1)
                    .forEach((j) -> adjacencyMatrix[i][j] = len_max);
        });
        if(maxLenRoad == 0) {
            return new ArrayList<>();
        }
        for(int k = 0; k < position; k++){
            for(int i = 0; i < position; i++){
                for(int j = 0; j < position; j++){
                    if(adjacencyMatrix[i][j] > adjacencyMatrix[i][k] + adjacencyMatrix[k][j]) {
                        adjacencyMatrix[i][j] = adjacencyMatrix[i][k] + adjacencyMatrix[k][j];
                        if(adjacencyMatrix[i][j] > maxLenRoad){
                            maxLenRoad = adjacencyMatrix[i][j];
                            maxPosition = i;
                        }
                    }
                }
            }
        }

        Long idStart = positionToId.get(maxPosition);
        Map<Long,Long> idTracker = userRepo.findAll(DefaultFilters::filterTrue).stream()
                        .collect(Collectors.toMap(User::getId,DefaultSuppliers::get0));

        DFS(idTracker,idStart,idStart);
        return new ArrayList<>(idTracker.keySet().stream()
                .filter((id) -> idTracker.get(id) != 0)
                .map(userRepo::findElement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }
}
