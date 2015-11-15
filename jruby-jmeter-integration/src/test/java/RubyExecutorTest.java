import org.fest.assertions.Assertions;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RubyExecutorTest {
    @Test
    public void
    it_should_generate_a_file_from_ruby_code() throws IOException, ScriptException {
        Assertions.assertThat(RubyExecutor.run(this.getClass().getResource("/RubyExecutor/simpleCode.rb").getFile())).exists();
    }

    @Test
    public void
    it_should_generate_a_jmx_file_from_ruby_code() throws FileNotFoundException, ScriptException {
        Assertions.assertThat(RubyExecutor.run(this.getClass().getResource("/RubyExecutor/simpleJmx.rb").getFile())).exists();
    }
}
