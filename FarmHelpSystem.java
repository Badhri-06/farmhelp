package farmhelp;

import farmhelp.model.Farmer;
import farmhelp.model.Worker;
import farmhelp.admin.Admin;
import farmhelp.gui.FarmHelpGUI;
import farmhelp.exception.FarmerException;
import farmhelp.exception.WorkerException;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

public class FarmHelpSystem {
    public static void main(String[] args) {
        Admin admin = new Admin();
        initializeSampleData(admin);

        // Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new FarmHelpGUI(admin));
    }

    private static void initializeSampleData(Admin admin) {
        try {
            if (admin.getFarmers().isEmpty() && admin.getWorkers().isEmpty()) {
                // Farmers
                admin.registerFarmer("Anbu", "Chennai", "Rice", 1.0, 30000);
                admin.registerFarmer("Ravi", "Chennai", "Wheat", 2.0, 45000);
                admin.registerFarmer("Priya", "Chennai", "Sugarcane", 0.5, 25000);
                admin.registerFarmer("Kumar", "Chennai", "Cotton", 3.0, 60000);

                // Workers
                List<String> skills1 = Arrays.asList("Harvesting", "Ploughing");
                List<String> skills2 = Arrays.asList("Harvesting", "Irrigation");

                admin.registerWorker("Rajesh", "Chennai", skills1);
                admin.registerWorker("Suresh", "Chennai", skills2);
                admin.registerWorker("Mahesh", "Chennai", Arrays.asList("Ploughing", "Seeding"));
                admin.registerWorker("Ganesh", "Chennai", Arrays.asList("Ploughing", "Weeding"));
                admin.registerWorker("Ramesh", "Chennai", skills2);
                admin.registerWorker("Dinesh", "Chennai", skills1);
                admin.registerWorker("Nitesh", "Chennai", Arrays.asList("Seeding", "Weeding"));
                admin.registerWorker("Ritesh", "Chennai", skills1);

                System.out.println("✓ Sample data initialized (4 Farmers, 8 Workers)");
            }
        } catch (FarmerException | WorkerException e) {
            System.out.println("Note: Could not initialize sample data: " + e.getMessage());
        }
    }
}