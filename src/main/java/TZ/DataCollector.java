package TZ;

import java.util.Date;

public class DataCollector {
    private String ip = "";
    private String uri = "";
    private int recievedBytes = 0;
    private double speed = 0;
    private int sentBytes = 0;
    private Date date = new Date();

    DataCollector(String ip, String uri, int sb, int rb, double speed) {
        this.ip = ip;
        this.uri = uri;
        this.recievedBytes = rb;
        this.speed = speed;
        this.sentBytes = sb;
        this.date = new Date();
    }

    public String toString() {
        return  "" + ip
                + " " + uri
                + " " + recievedBytes
                + " " + speed
                + " " + sentBytes
                + " " + date;

    }

    public String getIP() {
        return ip + "";
    }
    public String getURL() {
        return uri + "";
    }
    public String getRecieviedBytes() {
        return recievedBytes + "";
    }
    public String getSpeed() {
        return speed + "";
    }
    public String getSentBytes() {
        return sentBytes + "";
    }
    public String getDate() {
        return date + "";
    }


}