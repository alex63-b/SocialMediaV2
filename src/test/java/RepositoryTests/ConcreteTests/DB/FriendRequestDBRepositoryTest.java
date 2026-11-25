package RepositoryTests.ConcreteTests.DB;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendRequestValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.PagingRepository;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.RepositoryFactory;
import Repository.Helpers.DefaultFilters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.factory.PropertyFactory;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestDBRepositoryTest {
    private static final String db_config_path = "test_data/Resources/db_test.properties";
    @BeforeEach
    public void setUp(){
        reloadDB();
    }
    @AfterEach
    public void tearDown(){
        reloadDB();
    }
    private void reloadDB(){
        String url = "jdbc:postgresql://localhost:5432/SMTestDB";
        String user = "postgres";
        String password = "postgres";

        List<String> nameList = List.of("John Doe","Phil Smith","Luke Scott");
        List<String> firstNameList = List.of("johndoe@gmail.com","philsmith@gmail.com","lukescott@gmail.com");

        List<Long> id1List = List.of(1L,2L);
        List<Long> id2List = List.of(3L,3L);
        List<LocalDateTime> dateList = List.of(LocalDateTime.parse("2025-12-13T17:02:22"),LocalDateTime.parse("2024-07-01T21:03:24"));

        List<Long> friendRequestId1List = List.of(1L,1L,2L);
        List<Long> friendRequestId2List = List.of(2L,3L,3L);
        List<FriendRequestStates> friendRequestStates = List.of(FriendRequestStates.REJECTED,FriendRequestStates.APPROVED,FriendRequestStates.APPROVED);
        List<LocalDateTime> friendRequestDateList = List.of(LocalDateTime.parse("2024-07-13T15:02:22"),LocalDateTime.parse("2025-12-13T15:02:22"),LocalDateTime.parse("2024-07-01T19:03:24"));

        String insertSql = "INSERT INTO users(first_name,last_name) VALUES (?,?)";
        String friendshipSql = "INSERT INTO friendships(id1,id2,friendshipDate) VALUES (?,?,?)";
        String insertFriendRequestSql = "INSERT INTO friend_requests(from_id, to_id, status, date) VALUES (?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users");
            statementDelete.executeUpdate();
            PreparedStatement statementResetId = connection.prepareStatement("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statementResetId.executeUpdate();
            PreparedStatement statementDelete2 = connection.prepareStatement("DELETE FROM friendships");
            statementDelete2.executeUpdate();
            PreparedStatement statementDeleteFriendRequests = connection.prepareStatement("DELETE FROM friend_requests");
            statementDeleteFriendRequests.executeUpdate();
            PreparedStatement statementResetFriendRequestId = connection.prepareStatement("ALTER SEQUENCE friend_requests_id_seq RESTART WITH 1");
            statementResetFriendRequestId.executeUpdate();
            IntStream.range(0,3).forEach((i) -> {
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSql);
                    statement.setString(2, nameList.get(i));
                    statement.setString(1, firstNameList.get(i));
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });
            IntStream.range(0,2).forEach((i) -> {
                try {
                    PreparedStatement statementFriend = connection.prepareStatement(friendshipSql);
                    statementFriend.setLong(1, id1List.get(i));
                    statementFriend.setLong(2, id2List.get(i));
                    statementFriend.setTimestamp(3, Timestamp.valueOf(dateList.get(i)));
                    statementFriend.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });
            IntStream.range(0,3).forEach((i) -> {
                try{
                    PreparedStatement statementFriendRequest = connection.prepareStatement(insertFriendRequestSql);
                    statementFriendRequest.setLong(1,friendRequestId1List.get(i));
                    statementFriendRequest.setLong(2,friendRequestId2List.get(i));
                    statementFriendRequest.setString(3,friendRequestStates.get(i).toString());
                    statementFriendRequest.setTimestamp(4, Timestamp.valueOf(friendRequestDateList.get(i)));
                    statementFriendRequest.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });

        }
        catch (SQLException e){
            throw new RepositoryException("Something went wrong!",e);
        }
    }
    private PagingRepository<Long,FriendRequest> setupRepo(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(db_config_path));

        Validator<User> userValidator = new UserValidator();
        Validator<FriendRequest> friendRequestValidator = new FriendRequestValidator();

        Repository<Long,User> userRepository = factory.createUserRepository(userValidator);
        return (PagingRepository<Long, FriendRequest>) factory.createFriendRequestRepository(friendRequestValidator);
    }
    @Test
    void findAllOnPage() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        assertThrows(RepositoryException.class, () -> friendRequestRepository.findAllOnPage(null));
        assertThrows(IllegalArgumentException.class, () -> friendRequestRepository.findAllOnPage(new Pageable(-1,-2)));

        Page<FriendRequest> firstPage = friendRequestRepository.findAllOnPage(new Pageable(0,2));
        assertEquals(3, firstPage.getTotalNumberOfElements());

        List<FriendRequest> friendRequests = (List<FriendRequest>) firstPage.getElementsOnPage();
        assertEquals(2, friendRequests.size());

        assertEquals(1L,friendRequests.getFirst().getFrom());
        assertEquals(2L,friendRequests.getFirst().getTo());

        assertEquals(1L,friendRequests.getLast().getFrom());
        assertEquals(3L,friendRequests.getLast().getTo());

        Page<FriendRequest> secondPage = friendRequestRepository.findAllOnPage(new Pageable(1,2));

        List<FriendRequest> friendRequests2 = (List<FriendRequest>) secondPage.getElementsOnPage();
        assertEquals(1, friendRequests2.size());

        assertEquals(2L,friendRequests2.getLast().getFrom());
        assertEquals(3L,friendRequests2.getLast().getTo());

        Page<FriendRequest> thirdPage = friendRequestRepository.findAllOnPage(new Pageable(3,1));

        List<FriendRequest> friendRequests3 = (List<FriendRequest>) thirdPage.getElementsOnPage();
        assertEquals(0,friendRequests3.size());

        Page<FriendRequest> testPage = friendRequestRepository.findAllOnPage(new Pageable(1,1));

        List<FriendRequest> friendRequests4 = (List<FriendRequest>) testPage.getElementsOnPage();

        assertEquals(1,friendRequests4.size());

        assertEquals(1L,friendRequests4.getFirst().getFrom());
        assertEquals(3L,friendRequests4.getFirst().getTo());
    }

    @Test
    void findAll() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        List<FriendRequest> fullRepo = friendRequestRepository.findAll(DefaultFilters::filterTrue);

        assertEquals(3,fullRepo.size());

        assertEquals(1L,fullRepo.getFirst().getFrom());
        assertEquals(2L,fullRepo.getFirst().getTo());

        assertEquals(1L,fullRepo.get(1).getFrom());
        assertEquals(3L,fullRepo.get(1).getTo());

        assertEquals(2L,fullRepo.getLast().getFrom());
        assertEquals(3L,fullRepo.getLast().getTo());

        List<FriendRequest> filterRepo = friendRequestRepository.findAll((f) -> f.getTo() == 2L);

        assertEquals(1,filterRepo.size());

        assertEquals(1L,filterRepo.getFirst().getFrom());
        assertEquals(2L,filterRepo.getFirst().getTo());

        assertThrows(RepositoryException.class,() -> friendRequestRepository.findAll(null));
    }

    @Test
    void findElement() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        assertThrows(RepositoryException.class,() -> friendRequestRepository.findElement(null));
        assertTrue(friendRequestRepository.findElement(-2L).isEmpty());

        assertTrue(friendRequestRepository.findElement(1L).isPresent());
        assertEquals(LocalDateTime.parse("2024-07-13T15:02:22"),friendRequestRepository.findElement(1L).get().getDate());
    }

    @Test
    void addElement() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        FriendRequest goodFriendRequest = new FriendRequest(4L,1L,3L,FriendRequestStates.REJECTED,LocalDateTime.parse("2022-07-13T13:07:22"));

        assertEquals(Optional.of(goodFriendRequest),friendRequestRepository.addElement(goodFriendRequest));
        assertEquals(4,friendRequestRepository.size());

        if(friendRequestRepository.findElement(4L).isPresent()) {
            assertEquals(goodFriendRequest, friendRequestRepository.findElement(4L).get());
        }
        else{
            fail();
        }

        FriendRequest wrongFriendRequest = new FriendRequest(6L,1L,99L,FriendRequestStates.REJECTED,LocalDateTime.parse("2021-07-13T13:07:22"));

        assertThrows(RepositoryException.class,() -> friendRequestRepository.addElement(wrongFriendRequest));
        assertThrows(RepositoryException.class,() -> friendRequestRepository.addElement(null));
        assertFalse(friendRequestRepository.addElement(goodFriendRequest).isPresent());
    }

    @Test
    void deleteElement() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        Optional<FriendRequest> testFriendRequest = friendRequestRepository.findElement(2L);

        assertFalse(friendRequestRepository.deleteElement(5L).isPresent());

        assertEquals(testFriendRequest,friendRequestRepository.deleteElement(2L));

        assertEquals(2,friendRequestRepository.size());
        assertFalse(friendRequestRepository.findElement(2L).isPresent());
    }

    @Test
    void modifyElement() {
        PagingRepository<Long, FriendRequest> friendRequestRepository = setupRepo();

        FriendRequest newFriendRequest = new FriendRequest(1L,1L,3L,FriendRequestStates.REJECTED,LocalDateTime.parse("2022-07-13T13:07:22"));
        FriendRequest wrongFriendRequest = new FriendRequest(1L,1L,99L,FriendRequestStates.REJECTED,LocalDateTime.parse("2021-07-13T13:07:22"));

        assertThrows(RepositoryException.class,() -> friendRequestRepository.modifyElement(null));
        assertThrows(ValidationException.class,() -> friendRequestRepository.modifyElement(new FriendRequest(-2L,1L,2L,FriendRequestStates.PENDING,LocalDateTime.parse("2022-07-13T13:07:22"))));

        assertTrue(friendRequestRepository.modifyElement(newFriendRequest).isEmpty());
        assertThrows(RepositoryException.class, () -> friendRequestRepository.modifyElement(wrongFriendRequest));

        if(friendRequestRepository.findElement(newFriendRequest.getId()).isPresent()) {
            assertEquals(LocalDateTime.parse("2022-07-13T13:07:22"),friendRequestRepository.findElement(newFriendRequest.getId()).get().getDate());
            assertEquals(FriendRequestStates.REJECTED,friendRequestRepository.findElement(newFriendRequest.getId()).get().getStatus());
        }
        else{
            fail();
        }
    }
}