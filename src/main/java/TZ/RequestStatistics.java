package TZ;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public final class RequestStatistics {

    private static RequestStatistics instance = null; //value "instance" hold a reference to a single instance

    private List<DataCollector> listIP = new ArrayList<DataCollector>(); //accumulated info about IP stored in ArrayList
    private Set<String> uniqueIP = new HashSet<String>(); //accumulated info about IP stored in ArrayList(no repeats)

    //save url, number him and sort
    private Map<String, Integer> countURL = new TreeMap<String, Integer>();
    private long numberQuery = 0;
    private long numberActive = 0;

    private String firstIP;

    private RequestStatistics() {
    }

    //take link to this instance
    public synchronized static RequestStatistics getInstance() {
        if (instance == null) {
            instance = new RequestStatistics();
        }
        return instance;
    }

    public synchronized String getReport() {
        String lastDate = (String) (listIP.size() > 0 ? listIP.get(listIP.size()-1).getDate(): new Date()+"");
        String lastIP = getFirstIP();
        if(listIP.size() >= 1) {
            lastIP = listIP.get(listIP.size()-1).getIP();
        }
        String result = "<html><head><center><h1><ins>Statistics:<ins></h1></center></head><body><center>"
                .concat("<table border = \"0\" cellpadding=\"6\" cellspacing=\"0\"><tbody><tr><th>IP</th><th>Last Connections</th><th>Active connection</th><th>Total connection</th>")
                .concat("<th>Unique Connection</th></tr></tbody><tr><th>" + lastIP + "</th><th>" + lastDate + "</th><th>" + numberActive)
                .concat("</th><th>" + numberQuery + "</th><th>" + uniqueIP.size() + "</th></tr></table>")
                .concat("<table border = \"0\" cellpadding=\"6\" cellspacing=\"0\"><tbody><tr><th>URL</th><th>CountURL</th></tr>");
        for(Entry<String, Integer> k : countURL.entrySet()) {
            result += "<tr><th>" + k.getKey() + "</th>" +
                    "<th>" + k.getValue() + "</th></tr>";
        }
        result.concat("</table>");
        result += ("<table border = \"0\" cellpadding=\"6\" cellspacing=\"0\"><tbody><tr><th>IP</th><th>URI</th><th>Time Connection</th><th>Sent bytes</th><th>Recieved bytes</th>")
                .concat("<th>Speed(bytes/sec)</th></tr></tbody>");
        for(DataCollector ipc: listIP) {
            result +="<tr><th>" + ipc.getIP() + "</th>" +
                    "<th>" + ipc.getURL() + "</th>" +
                    "<th>" + ipc.getDate()+ "</th>" +
                    "<th>" + ipc.getSentBytes() + "</th>" +
                    "<th>" + ipc.getRecieviedBytes() + "</th>" +
                    "<th>" + ipc.getSpeed() + "</th></tr>";
        }
        result.concat("</tbody></table></center></body></html>");
        return result;
    }

    //sets of the synchronized methods, which grant access to information each of threads in specific time space:
    public synchronized void addConnection(DataCollector ipc) {
        if(listIP.size() > 20)
            listIP.remove(0);
        listIP.add(ipc);
    }

    public synchronized void setCount() {
        numberQuery++;
    }

    public synchronized void setFirstIP(String ip) {
        firstIP = ip;
    }

    public synchronized String getFirstIP() {
        return firstIP;
    }


    public synchronized void putURLandCountHim(String url) {
        if(countURL.containsKey(url)) {
            countURL.put(url, new Integer(countURL.get(url)+1));
        } else {
            countURL.put(url, new Integer(1));
        }
    }

    //unique connection are considered by the unique query

    public synchronized void setCountUniqueConnection(String s) {
        if(!s.equals("/favicon.ico"))
            uniqueIP.add(s);
    }

    public synchronized void setNumberAcvite() {
        numberActive++;
    }

    public synchronized void dropNumberActive() {
        numberActive--;
    }


}