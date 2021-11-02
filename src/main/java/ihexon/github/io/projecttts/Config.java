package ihexon.github.io.projecttts;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Config {

    private static Config instance = null;
    private ArrayList<String> payload_http_header = null;
    private ArrayList<String> payload_http_header_second = null;
    private JsonNode json_payload = null;
    private FileOutputStream out = null;
    private URI wss_uri = null;

    private StringBuilder stringBuilder = new StringBuilder();

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() {
        payload_http_header = new ArrayList<>();
        payload_http_header.clear();
        payload_http_header.add("Content-Type:application/json; charset=utf-8\n");
        payload_http_header.add("Path:speech.config\n");
        payload_http_header.trimToSize();

        payload_http_header_second = new ArrayList<>();
        payload_http_header_second.add("X-RequestId:fe83fbefb15c7739fe674d9f3e81d38f\r\n");
        payload_http_header_second.add("Content-Type:application/ssml+xml\r\n");
        payload_http_header_second.add("Path:ssml\r\n\r\n");
        payload_http_header_second.add("<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='en-US'>\n");
        payload_http_header_second.add("<voice  name='Microsoft Server Speech Text to Speech Voice (zh-TW, HsiaoChenNeural)'>\n");
        payload_http_header_second.add("<prosody pitch='+0Hz' rate ='+0%' volume='+0%'>\n");
        // flag{fuck_me} is a placeholder !!
        // it will be replace into real text which need to be read
        payload_http_header_second.add("flag{fuck_me}\n");
        payload_http_header_second.add("</prosody>\n");
        payload_http_header_second.add(" </voice>\n");
        payload_http_header_second.add("</speak>\n");
        payload_http_header_second.trimToSize();

        try {
            ObjectMapper mapper = new ObjectMapper();
            json_payload = mapper.readTree(this.getClass().getResource("/payload.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/wss_url.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            wss_uri = new URI(bufferedReader.readLine());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileOutputStream getOutFile() {
        return this.out;
    }

    public URI getWss_url() {
        return this.wss_uri;
    }

    private JsonNode getJson_payload() {
        return json_payload;
    }


    public StringBuilder setTextToBeRead(String s) {
        stringBuilder.append(s + "\n");
        return stringBuilder;
    }

    public String getTextToBeRead() {
        if (stringBuilder.toString().isEmpty()) {
            return stringBuilder.append("Nothing to read !").toString();
        }
        return stringBuilder.toString();
    }

    public String getPayloadFirst() {
        StringBuilder s = new StringBuilder();
        payload_http_header.forEach((pay_load) -> s.append(pay_load));
        s.append(getJson_payload());
        return s.toString();
    }

    public void setSavePath(String save_to) {
        try {
            out = new FileOutputStream(save_to, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FileOutputStream getOut() {
        return out;
    }

    public String getPayloadHttpHeaderSecond() {
        StringBuilder s = new StringBuilder();
        payload_http_header_second.forEach((pay_load) -> s.append(pay_load));
        return s.toString().replace("flag{fuck_me}", getTextToBeRead());
    }
}
