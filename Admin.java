package farmhelp.admin;

import farmhelp.model.Farmer;
import farmhelp.model.Worker;
import farmhelp.model.Request;
import farmhelp.model.Allocation;
import farmhelp.model.Person;
import farmhelp.exception.FarmerException;
import farmhelp.exception.WorkerException;
import farmhelp.exception.LandSizeViolationException;
import farmhelp.exception.SkillMismatchException;
import farmhelp.exception.InsufficientWorkersException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Admin {
    private Map<Integer, Farmer> farmers;
    private Map<Integer, Worker> workers;
    private Map<Integer, Request> requests;
    private Map<Integer, Allocation> allocations;

    public Admin() {
        this.farmers = new HashMap<>();
        this.workers = new HashMap<>();
        this.requests = new HashMap<>();
        this.allocations = new HashMap<>();
        loadAllData();
    }

    public void registerFarmer(String name, String location, String crop, double landSize, double income) throws FarmerException {
        Farmer farmer = new Farmer(name, location, crop, landSize, income);
        farmers.put(farmer.getId(), farmer);
        saveFarmersToFile();
    }

    public void registerWorker(String name, String location, List<String> skills) throws WorkerException {
        Worker worker = new Worker(name, location, skills);
        workers.put(worker.getId(), worker);
        saveWorkersToFile();
    }

    public void createWorkerRequest(int farmerId, String skillRequired, int workersRequested) throws FarmerException, LandSizeViolationException {
        if (!farmers.containsKey(farmerId)) {
            throw new FarmerException("Farmer with ID " + farmerId + " not found");
        }

        Farmer farmer = farmers.get(farmerId);
        int maxAllowed = farmer.getMaxWorkersAllowed();

        if (workersRequested > maxAllowed) {
            throw new LandSizeViolationException(
                    "Requested " + workersRequested + " workers exceeds maximum allowed " +
                            maxAllowed + " for " + farmer.getLandSize() + " acres"
            );
        }

        Request request = new Request(farmerId, skillRequired, workersRequested);
        requests.put(request.getRequestId(), request);
        saveRequestsToFile();
    }

    public String allocateWorkers() throws SkillMismatchException, InsufficientWorkersException {
        StringBuilder result = new StringBuilder();
        if (requests.isEmpty()) {
            return "No pending requests found.";
        }

        List<Request> pendingRequests = new ArrayList<>(requests.values());
        pendingRequests.sort((r1, r2) -> {
            Farmer f1 = farmers.get(r1.getFarmerId());
            Farmer f2 = farmers.get(r2.getFarmerId());
            return Double.compare(f1.getIncome(), f2.getIncome());
        });

        result.append("=== WORKER ALLOCATION ===\n\n");
        int successfulAllocations = 0;
        List<Integer> processedRequestIds = new ArrayList<>();

        for (Request request : pendingRequests) {
            try {
                String msg = processRequest(request);
                result.append(msg).append("\n");
                successfulAllocations++;
                processedRequestIds.add(request.getRequestId());
            } catch (Exception e) {
                result.append("✗ Failed: Request ID ").append(request.getRequestId()).append(" | ").append(e.getMessage()).append("\n\n");
            }
        }

        for (int requestId : processedRequestIds) {
            requests.remove(requestId);
        }

        saveRequestsToFile();
        result.append("\n=== SUMMARY ===\n");
        result.append("Total Attempts: ").append(pendingRequests.size()).append("\n");
        result.append("Successful: ").append(successfulAllocations);

        if (successfulAllocations > 0) {
            saveAllocationsToFile();
            saveWorkersToFile();
        }

        return result.toString();
    }

    private String processRequest(Request request) throws SkillMismatchException, InsufficientWorkersException, FarmerException {
        Farmer farmer = farmers.get(request.getFarmerId());
        if (farmer == null) {
            throw new FarmerException("Farmer not found for request.");
        }
        String skillRequired = request.getSkillRequired();
        int workersRequested = request.getWorkersRequested();

        StringBuilder msg = new StringBuilder();
        msg.append("✓ Processing Farmer: ").append(farmer.getName())
                .append(" (Income: ₹").append(String.format("%.0f", farmer.getIncome())).append(")\n");
        msg.append("  Skill: ").append(skillRequired).append(", Requested: ").append(workersRequested).append("\n");

        List<Worker> availableWorkers = workers.values().stream()
                .filter(w -> w.isAvailable() && w.hasSkill(skillRequired) && w.getLocation().equalsIgnoreCase(farmer.getLocation()))
                .collect(Collectors.toList());

        if (availableWorkers.isEmpty()) {
            throw new SkillMismatchException("  No available workers with skill '" + skillRequired + "' in " + farmer.getLocation());
        }

        int maxAllowed = farmer.getMaxWorkersAllowed();
        int workersToAllocate = Math.min(workersRequested, Math.min(maxAllowed, availableWorkers.size()));

        if (workersToAllocate == 0) {
            throw new InsufficientWorkersException("  Not enough workers available to satisfy minimum requirements (min: 1)");
        }

        List<Integer> allocatedWorkerIds = new ArrayList<>();
        for (int i = 0; i < workersToAllocate; i++) {
            Worker worker = availableWorkers.get(i);
            worker.setAvailable(false);
            allocatedWorkerIds.add(worker.getId());
        }

        Allocation allocation = new Allocation(request.getRequestId(), request.getFarmerId(), allocatedWorkerIds);
        allocations.put(allocation.getAllocationId(), allocation);

        msg.append("  SUCCESS: Allocated ").append(workersToAllocate).append(" workers\n");
        msg.append("  Worker IDs: ").append(allocatedWorkerIds).append("\n");

        return msg.toString();
    }

    public Map<Integer, Farmer> getFarmers() { return farmers; }
    public Map<Integer, Worker> getWorkers() { return workers; }
    public Map<Integer, Request> getRequests() { return requests; }
    public Map<Integer, Allocation> getAllocations() { return allocations; }

    private void saveFarmersToFile() {
        try (PrintWriter w = new PrintWriter(new FileWriter("farmers.txt"))) {
            for (Farmer f : farmers.values()) {
                w.println(f.getId() + "," + f.getName() + "," + f.getLocation() + "," +
                        f.getCrop() + "," + f.getLandSize() + "," + f.getIncome());
            }
        } catch (IOException e) {
            System.err.println("Error saving farmers: " + e.getMessage());
        }
    }

    private void loadFarmersFromFile() {
        try (Scanner sc = new Scanner(new File("farmers.txt"))) {
            int maxId = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] p = line.split(",");
                    if (p.length == 6) {
                        int id = Integer.parseInt(p[0]);
                        Farmer f = new Farmer(id, p[1], p[2], p[3], Double.parseDouble(p[4]), Double.parseDouble(p[5]));
                        farmers.put(id, f);
                        maxId = Math.max(maxId, id);
                    }
                }
            }
            if (maxId > 0) Person.setNextId(maxId + 1);
        } catch (FileNotFoundException e) {
            // OK to not find file initially
        }
    }

    private void saveWorkersToFile() {
        try (PrintWriter w = new PrintWriter(new FileWriter("workers.txt"))) {
            for (Worker worker : workers.values()) {
                w.println(worker.getId() + "," + worker.getName() + "," + worker.getLocation() + "," +
                        String.join("|", worker.getSkills()) + "," + worker.isAvailable());
            }
        } catch (IOException e) {
            System.err.println("Error saving workers: " + e.getMessage());
        }
    }

    private void loadWorkersFromFile() {
        try (Scanner sc = new Scanner(new File("workers.txt"))) {
            int maxId = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] p = line.split(",");
                    if (p.length == 5) {
                        int id = Integer.parseInt(p[0]);
                        List<String> skills = Arrays.asList(p[3].split("\\|"));
                        Worker w = new Worker(id, p[1], p[2], skills, Boolean.parseBoolean(p[4]));
                        workers.put(id, w);
                        maxId = Math.max(maxId, id);
                    }
                }
            }
            if (maxId > 0) Person.setNextId(maxId + 1);
        } catch (FileNotFoundException e) {
            // OK
        }
    }

    private void saveRequestsToFile() {
        try (PrintWriter w = new PrintWriter(new FileWriter("requests.txt"))) {
            for (Request r : requests.values()) {
                w.println(r.getRequestId() + "," + r.getFarmerId() + "," +
                        r.getSkillRequired() + "," + r.getWorkersRequested() + "," +
                        r.getRequestDate().getTime());
            }
        } catch (IOException e) {
            System.err.println("Error saving requests: " + e.getMessage());
        }
    }

    private void loadRequestsFromFile() {
        try (Scanner sc = new Scanner(new File("requests.txt"))) {
            int maxId = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] p = line.split(",");
                    if (p.length == 5) {
                        int rid = Integer.parseInt(p[0]);
                        Request r = new Request(rid, Integer.parseInt(p[1]), p[2], Integer.parseInt(p[3]), new Date(Long.parseLong(p[4])));
                        requests.put(rid, r);
                        maxId = Math.max(maxId, rid);
                    }
                }
            }
            if (maxId > 0) Request.setNextRequestId(maxId + 1);
        } catch (FileNotFoundException e) {
            // OK
        }
    }

    private void saveAllocationsToFile() {
        try (PrintWriter w = new PrintWriter(new FileWriter("allocations.txt"))) {
            for (Allocation a : allocations.values()) {
                w.println(a.getAllocationId() + "," + a.getRequestId() + "," + a.getFarmerId() + "," +
                        a.getWorkerIds().stream().map(String::valueOf).collect(Collectors.joining("|")) + "," +
                        a.getAllocationDate().getTime() + "," + a.getStatus());
            }
        } catch (IOException e) {
            System.err.println("Error saving allocations: " + e.getMessage());
        }
    }

    // FIX: Correctly calls the Allocation constructor with the status string as the last argument.
    private void loadAllocationsFromFile() {
        try (Scanner sc = new Scanner(new File("allocations.txt"))) {
            int maxId = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] p = line.split(",");
                    if (p.length == 6) {
                        int aid = Integer.parseInt(p[0]);
                        List<Integer> wids = Arrays.stream(p[3].split("\\|")).map(Integer::parseInt).collect(Collectors.toList());

                        // Corrected line
                        Allocation a = new Allocation(
                                aid,
                                Integer.parseInt(p[1]),
                                Integer.parseInt(p[2]),
                                wids,
                                new Date(Long.parseLong(p[4])), // Date object created correctly
                                p[5]                           // Status string
                        );

                        allocations.put(aid, a);
                        maxId = Math.max(maxId, aid);
                    }
                }
            }
            if (maxId > 0) Allocation.setNextAllocationId(maxId + 1);
        } catch (FileNotFoundException e) {
            // OK
        }
    }

    private void loadAllData() {
        loadFarmersFromFile();
        loadWorkersFromFile();
        loadRequestsFromFile();
        loadAllocationsFromFile();
    }
}