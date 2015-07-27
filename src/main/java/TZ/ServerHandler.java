package TZ;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.FullHttpResponse;


public class ServerHandler extends ChannelHandlerAdapter {
    private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
    private String uri;
    private String ip;
    private int sentBytes;
    private int recievedBytes;
    private double speed;

    ServerHandler(String ip) {
        this.ip = ip;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        speed = System.currentTimeMillis();

        ServerInitializer.rs.setCount(); //open connections

        ServerInitializer.rs.setNumberAcvite(); //acvite connections

        RequestStatistics.getInstance().setFirstIP(ip); //first ip for first connection


        recievedBytes += msg.toString().length(); //take received bytes

        if (!(msg instanceof HttpRequest))
            return;

        uri = ((HttpRequest) msg).uri();
        FullHttpResponse response = null;

        //take URL
        if (uri.contains("%3C")) {
            this.uri = uri.substring(17, uri.length() - 3);
        }

        ServerInitializer.rs.setCountUniqueConnection(uri); //unique connection

        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            if (req.uri().contains("hello")) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                if (HttpHeaderUtil.is100ContinueExpected(req)) {
                    ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
                }

                boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                if (response != null) {

                    this.sentBytes = response.content().writerIndex(); //take sentBytes

                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }

                    if (!keepAlive) {
                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                        ctx.write(response);
                    }
            }

            if (req.uri().contains("/redirect")) {

                QueryStringDecoder urlDecoder = new QueryStringDecoder(req.uri());
                response = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY);
                response.headers().set(LOCATION, urlDecoder.parameters().get("url").get(0));
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                if(response != null) {

                    this.sentBytes = response.content().writerIndex(); // take sentBytes
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }
                ServerInitializer.rs.putURLandCountHim(urlDecoder.parameters().get("url").get(0));
                ServerInitializer.rs.setCountUniqueConnection(urlDecoder.parameters().get("url").get(0));
                boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(response);
                }

            }

            if (req.uri().contains("/status")) {
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(ServerInitializer.rs.getReport(), CharsetUtil.US_ASCII));
                response.headers().set(CONTENT_TYPE, "html");
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                if(response != null) {

                    this.sentBytes = response.content().writerIndex();
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }

                boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(response);
                }
            }

        }
            //drop the number of active connections
            ServerInitializer.rs.dropNumberActive();
            ServerInitializer.rs.setCountUniqueConnection(uri);

            DataCollector ipc = new DataCollector(ip, uri, sentBytes, recievedBytes, speed);
            ServerInitializer.rs.addConnection(ipc);
    }
}
