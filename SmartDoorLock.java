import java.util.ArrayList;

// Base class for both Admin and User
class Person {
    protected String name;
    protected String id;

    public Person(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

// Admin class
class Admin extends Person {
    public Admin(String name, String id) {
        super(name, id);
    }

    public void openDoor(DoorSystem doorSystem) {
        doorSystem.unlockDoor("admin", this);
    }

    public void registerUser(UserList userList, User user) {
        userList.addUser(user);
    }
}

// User class
class User extends Person {
    private String pin;

    public User(String name, String id, String pin) {
        super(name, id);
        this.pin = pin;
    }

    public boolean isPinCorrect(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public void openDoor(DoorSystem doorSystem, String enteredPin) {
        if (isPinCorrect(enteredPin)) {
            doorSystem.unlockDoor("user", this);
        } else {
            System.out.println("\nAccess Denied: Wrong PIN by User " + name + " (" + id + ")");
        }
    }

    public void tryRemoteAccess(DoorSystem doorSystem) {
        doorSystem.remoteAccessNotAllowed(this);
    }
}

// Door system that handles locking/unlocking
class DoorSystem {
    private DoorLock doorLock;
    private UserList userList;

    public DoorSystem() {
        this.doorLock = new DoorLock();
        this.userList = new UserList();
    }

    public void unlockDoor(String role, Person person) {
        doorLock.unlock(role, person);
    }

    public void remoteAccessNotAllowed(Person person) {
        System.out.println("\nAccess Denied: Remote access is not allowed for User " + person.getName() + " (" + person.getId() + ")");
    }

    public UserList getUserList() {
        return userList;
    }

    public void lockDoorAgain() {
        doorLock.lock();
    }
}

// Lock mechanism
class DoorLock {
    private boolean isLocked = true;

    public void unlock(String role, Person person) {
        if (isLocked) {
            if (role.equals("admin")) {
                System.out.println("Admin Access: " + person.getName() + " (" + person.getId() + ")"+"\nPIN entered.\nPIN verified.\nAccess granted");
                System.out.println("Motor rotating ANTICLOCKWISE. Door UNLOCKED.");
                isLocked = false;

                try {
                    Thread.sleep(3000);  // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock();
            } else {
                System.out.println("\nUser Access: " + person.getName() + "(" + person.getId() + ")"+"\nPIN entered.\nPIN Verified.\nAccess granted ");
                System.out.println("Motor rotating ANTICLOCKWISE. Door UNLOCKED.");
                isLocked = false;
            }
        } else {
            System.out.println("Ignored: Door is already UNLOCKED.");
        }
    }

    public void lock() {
        if (!isLocked) {
            System.out.println("Motor rotating CLOCKWISE. Door LOCKED.");
            isLocked = true;
        }
    }
}

// Holds user records
class UserList {
    private ArrayList<User> userRecords = new ArrayList<>();

    public void addUser(User user) {
        userRecords.add(user);
    }

    public User findUserById(String id) {
        for (User user : userRecords) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}

// Main testing
public class SmartDoorLock {
    public static void main(String[] args) {
        DoorSystem doorSystem = new DoorSystem();

        // Create Admin and Users
        Admin admin = new Admin("Sarim", "S001");
        User user1 = new User("Zara", "Z101", "8585");
        User user2 = new User("Sara", "S102", "1234");

        // Admin adds users
        admin.registerUser(doorSystem.getUserList(), user1);
        admin.registerUser(doorSystem.getUserList(), user2);

        // Admin opens the door
        admin.openDoor(doorSystem);

        // Lock the door again
        doorSystem.lockDoorAgain();

        // User 1 tries with correct PIN
        user1.openDoor(doorSystem, "8585");

        // User 2 tries with wrong PIN
        user2.openDoor(doorSystem, "5678");

        // User 2 tries to access remotely
        user2.tryRemoteAccess(doorSystem);
    }
}
