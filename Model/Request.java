package farmhelp.model;

import java.util.Date;

public class Request {
    private static int nextRequestId = 1;
    private int requestId;
    private int farmerId;
    private String skillRequired;
    private int workersRequested;
    private Date requestDate;

    public Request(int farmerId, String skillRequired, int workersRequested) {
        this.requestId = nextRequestId++;
        this.farmerId = farmerId;
        this.skillRequired = skillRequired;
        this.workersRequested = workersRequested;
        this.requestDate = new Date();
    }

    public Request(int requestId, int farmerId, String skillRequired, int workersRequested, Date requestDate) {
        this.requestId = requestId;
        this.farmerId = farmerId;
        this.skillRequired = skillRequired;
        this.workersRequested = workersRequested;
        this.requestDate = requestDate;
        if (requestId >= nextRequestId) {
            nextRequestId = requestId + 1;
        }
    }

    public static void setNextRequestId(int id) {
        nextRequestId = id;
    }

    public int getRequestId() { return requestId; }
    public int getFarmerId() { return farmerId; }
    public String getSkillRequired() { return skillRequired; }
    public int getWorkersRequested() { return workersRequested; }
    public Date getRequestDate() { return requestDate; }
}
