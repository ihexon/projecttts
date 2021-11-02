package ihexon.github.io.projecttts;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = {"--help","-h","-help"}, help = true, description = "Show Help message")
    private boolean help = false;

    @Parameter(names = {"-debug","--debug"}, hidden = true, description = "Level of debug verbosity")
    private Integer verbose = 0;

    @Parameter(names = {"-text","--text"}, description = "Text to be speech")
    private String text = "Boy next door";


    @Parameter(names = {"-save","--save","-s"}, description = "Where to save the wav file")
    private String save_to = "./BoyNextDoor.wav";

    public String get_text_tobe_read() {
        return text;
    }

    public Integer get_debug_verbose() {
        return verbose;
    }

    public String getSave_to(){
        return save_to;
    }

    public boolean if_need_help() {
        return help;
    }
}
