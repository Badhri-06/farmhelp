package farmhelp.model;

public abstract class Person {
    protected int id;
    protected String name;
    protected String location;

    public Person(String name, String location) {
        this.id = generateId();
        this.name = name;
        this.location = location;
    }

    public Person(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    private static int nextId = 1;

    public static int generateId() {
        return nextId++;
    }

    public static void setNextId(int id) {
        nextId = id;
    }

    public abstract void displayInfo();
    public abstract boolean validate();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Location: " + location;
    }
}