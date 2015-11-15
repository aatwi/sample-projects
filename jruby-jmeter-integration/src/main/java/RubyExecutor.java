import org.jruby.embed.jsr223.JRubyEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class RubyExecutor {
    public static File run(String rubyFile) throws FileNotFoundException, ScriptException {
        final ScriptEngine scriptEngine = new JRubyEngineFactory().getScriptEngine();
        scriptEngine.eval(new FileReader(rubyFile));
        return new File(rubyFile.replace(".rb", ".jmx"));
    }
}