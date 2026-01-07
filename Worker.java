package farmhelp.model;

import farmhelp.exception.WorkerException;
import java.util.List;
import java.util.ArrayList;

public class Worker extends Person {
    private List<String> skills;
    private boolean isAvailable;

    public Worker(String name, String location, List<String> skills) throws WorkerException {
        super(name, location);
        this.skills = new ArrayList<>(skills);
        this.isAvailable = true;
        if (!validate()) {
            throw new WorkerException("Invalid worker data");
        }
    }

    public Worker(int id, String name, String location, List<String> skills, boolean isAvailable) {
        super(id, name, location);
        this.skills = new ArrayList<>(skills);
        this.isAvailable = isAvailable;
    }

    @Override
    public void displayInfo() {
        System.out.println("=== WORKER INFO ===");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Location: " + location);
        System.out.println("Skills: " + String.join(", ", skills));
    }

    @Override
    public boolean validate() {
        return name != null && !name.trim().isEmpty() &&
                location != null && !location.trim().isEmpty() &&
                skills != null && !skills.isEmpty();
    }

    public boolean hasSkill(String skill) {
        return skills.contains(skill);
    }

    public List<String> getSkills() { return new ArrayList<>(skills); }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}