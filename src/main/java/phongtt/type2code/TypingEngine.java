package phongtt.type2code;


/**
 *
 * @author Phong
 */
public class TypingEngine {

    private final String target;

    public TypingEngine(String t) {
        this.target = normalize(t);
    }

    public String getTarget() {
        return target;
    }

    private static String normalize(String s) {
        return s.replace("\r\n", "\n")
                .replace("\t", "    ");
    }
}
