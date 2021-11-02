package ihexon.github.io.projecttts;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String... argv) {

        Args args = new Args();
        JCommander jCommander = JCommander.newBuilder().addObject(args).build();
        try{
            jCommander.parse(argv);
        }catch (ParameterException e){
            System.err.println("Pleaszzz read the f**king document and help message.");
            logger.info(e.getLocalizedMessage());
            System.exit(-1);
        }
        if (argv.length ==0){
            jCommander.usage();
            System.exit(-1);
        }

        if (args.if_need_help()) {
            jCommander.usage();
        } else {
            TTSEngine.getInstance().getConfig().setSavePath(args.getSave_to());
            TTSEngine.getInstance().setTextToBeRead(args.get_text_tobe_read());
            try {
                TTSEngine.getInstance().doRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // YOU CAN Call setTextToBeRead("Strings") multi-time , and call doRead() once. like:
//            TTSEngine.getInstance().setTextToBeRead("Fuck Me");
//            TTSEngine.getInstance().setTextToBeRead("Fuck Me again");
//            TTSEngine.getInstance().setTextToBeRead("Fuck Me one more time");
//            and call doRead()
//            TTSEngine.getInstance().doRead();
            // If you have many text to be read, 我劝你最好这样做
        }
    }
}