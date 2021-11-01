import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;


public class TestClient extends WebSocketClient {
    private static String text_data = "The example below uses a do/while loop. The loop will always be executed at least once, even if the condition is false, because the code block is executed before the condition is tested";
    FileOutputStream out = new FileOutputStream("/tmp/output.wav", true);

    public static String url = "wss://speech.platform.bing.com/" +
            "consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=" +
            "6A5AA1D4EAFF4E9FB37E23D68491D6F4";

    public TestClient(URI serverUri) throws FileNotFoundException {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("Content-Type:application/json; charset=utf-8\r\n\r\n" +
                "Path:speech.config\r\n\r\n" +
                "{\"context\":" +
                "{\"synthesis\":{\"audio\":{\"metadataoptions\":" +
                "{\"sentenceBoundaryEnabled\":\"false\",\"wordBoundaryEnabled\":\"true\"}," +
                "\"outputFormat\":\"webm-24khz-16bit-mono-opus\"}}}}\r\n");
        System.out.println("new connection opened");
    }

    int marker = 0;

    @Override
    public void onMessage(String message) {
//        System.out.println("received message: \n" + message);
        if (message.contains("Path:turn.end")) {
            System.out.println("Close connect !");
            this.close();
        }
    }

    String str= null;
    @Override
    public void onMessage(ByteBuffer message) {
        ByteBuf messageByteBuffer = Unpooled.wrappedBuffer(message);
        messageByteBuffer.readerIndex(2); // Skip first 2 bytes
        messageByteBuffer.discardReadBytes(); // delete first 2 bytes
        while (true){
            marker = messageByteBuffer.forEachByte(ByteProcessor.FIND_LF) + 1;
            str= (String) messageByteBuffer.getCharSequence(0, marker, CharsetUtil.UTF_8); // Text headers
            messageByteBuffer.readerIndex(marker);
            messageByteBuffer.discardReadBytes();

            if (str.trim().equalsIgnoreCase("Path:audio".trim())){
                try {
                    System.out.println("received audio data successful !");
                    marker = messageByteBuffer.readableBytes();
                    messageByteBuffer.getBytes(0, out, marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

    }

    @Override
    public void onError(Exception e) {
    }

    public static void main(String[] args) {
        try {
            TestClient testClient = new TestClient(new URI(url));
            testClient.connectBlocking();
            testClient.send(
                    "X-RequestId:fe83fbefb15c7739fe674d9f3e81d38f\r\n" +
                            "Content-Type:application/ssml+xml\r\n" +
                            "Path:ssml\r\n\r\n" +
                            "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='en-US'>" +
                            "<voice  name='Microsoft Server Speech Text to Speech Voice (zh-TW, HsiaoChenNeural)'>" +
                            "<prosody pitch='+0Hz' rate ='+0%' volume='+0%'>"
                            + text_data +
                            "</prosody>" +
                            "</voice>" +
                            "</speak>\r\n"
            );
        } catch (URISyntaxException | InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}