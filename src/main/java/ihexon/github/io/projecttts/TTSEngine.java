package ihexon.github.io.projecttts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TTSEngine extends WebSocketClient {
    final static Logger logger = LoggerFactory.getLogger(TTSEngine.class);


    private static TTSEngine instance = null;
    int marker = 0;
    String str = null;


    public static synchronized TTSEngine getInstance() {
        if (instance == null) {
            logger.info("Initialize TTSEngine once");
            instance = new TTSEngine();
        }
        return instance;
    }

    public TTSEngine() {
        super(Config.getInstance().getWss_url());
    }

    public void setTextToBeRead(String s) {
        Config.getInstance().setTextToBeRead(s);
    }

    public void doRead() throws InterruptedException {
        this.connectBlocking();
    }

    @Override

    public void onOpen(ServerHandshake handshakedata) {
        logger.info("new connection opened, send first payload:");
        this.send(Config.getInstance().getPayloadFirst());
        logger.info("send second payload");
        this.send(Config.getInstance().getPayloadHttpHeaderSecond());
    }

    @Override
    public void onMessage(String message) {
        logger.debug("Revived message: \n" + message.trim());
        if (message.contains("Path:turn.end")) {
            logger.info("Close connect !");
            this.close();
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {

        // Important Notes :
        // Parse the Binary Message
        // Defore you change code you should capture
        // Edge's websocket packets and analyze algorithms first, and then optimize the algorithms !
        // YOU SHOULD KNOW WHAT YOU ARE DOING :)

        ByteBuf messageByteBuffer = Unpooled.wrappedBuffer(message);
        messageByteBuffer.readerIndex(2); // Skip first 2 bytes
        messageByteBuffer.discardReadBytes(); // delete first 2 bytes
        while (true) {
            marker = messageByteBuffer.forEachByte(ByteProcessor.FIND_LF) + 1;
            str = (String) messageByteBuffer.getCharSequence(0, marker, CharsetUtil.UTF_8); // Text headers
            messageByteBuffer.readerIndex(marker);
            messageByteBuffer.discardReadBytes();

            if (str.trim().equalsIgnoreCase("Path:audio".trim())) {
                try {
                    logger.info("received audio data successful !");
                    marker = messageByteBuffer.readableBytes();
                    messageByteBuffer.getBytes(0, Config.getInstance().getOutFile(), marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
    }

    public Config getConfig() {
        return Config.getInstance();
    }
}