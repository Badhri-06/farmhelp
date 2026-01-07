package farmhelp.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Allocation {
    private static int nextAllocationId = 1;
    private int allocationId;
    private int requestId;
    private int farmerId;
    private List<Integer> workerIds;
    private Date allocationDate;
    private String status;

    public Allocation(int requestId, int farmerId, List<Integer> workerIds) {
        this.allocationId = nextAllocationId++;
        this.requestId = requestId;
        this.farmerId = farmerId;
        this.workerIds = new ArrayList<>(workerIds);
        this.allocationDate = new Date();
        this.status = "ALLOCATED";
    }

    public Allocation(int allocationId, int requestId, int farmerId, List<Integer> workerIds, Date allocationDate, String status) {
        this.allocationId = allocationId;
        this.requestId = requestId;
        this.farmerId = farmerId;
        this.workerIds = new ArrayList<>(workerIds);
        this.allocationDate = allocationDate;
        this.status = status;
        if (allocationId >= nextAllocationId) {
            nextAllocationId = allocationId + 1;
        }
    }

    public static void setNextAllocationId(int id) {
        nextAllocationId = id;
    }

    public int getAllocationId() { return allocationId; }
    public int getRequestId() { return requestId; }
    public int getFarmerId() { return farmerId; }
    public List<Integer> getWorkerIds() { return new ArrayList<>(workerIds); }
    public Date getAllocationDate() { return allocationDate; }
    public String getStatus() { return status; }
}