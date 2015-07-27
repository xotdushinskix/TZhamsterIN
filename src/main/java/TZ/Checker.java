package TZ;

import io.netty.handler.codec.http.FullHttpResponse;

public class Checker {

    //check URL responce
    public FullHttpResponse dataCheck(String value) throws InterruptedException {
        String url = "";
        if (value.contains("%3C")) {
            url = value.substring(value.length());
            RequestStatistics.getInstance().putURLandCountHim(url);
        }
        return null;
    }



}
