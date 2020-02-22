import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int program;
    private int vs;
    private int fs;

    public Shader(String filename) {
        program = glCreateProgram();

        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(filename + ".vs"));
        glCompileShader(vs);
        // If 0, there's an error, output the error
        if(glGetShaderi(vs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vs));
            System.exit(1);
        }

        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(filename + ".fs"));
        glCompileShader(fs);
        // If 0, there's an error, output the error
        if(glGetShaderi(fs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(fs));
            System.exit(1);
        }

        glAttachShader(program, vs);
        glAttachShader(program, fs);

        // Linking shader to specific attribute in shader file
        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");
        glBindAttribLocation(program, 2, "projection");
        glBindAttribLocation(program, 3, "scale");

        glLinkProgram(program);

        // If 0, there's an error, output the error
        if(glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }

        glValidateProgram(program);

        // If 0, there's an error, output the error
        if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
    }

    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(program, name);
        // Check if the location is valid
        if(location != -1) {
            glUniform1i(location, value);
        }
    }
    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(program, name);
        // 16 for the Matrix4f (4x4)
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        // Check if the location is valid
        if(location != -1) {
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    private String readFile(String filename) {
        StringBuilder string = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File("./shaders/" + filename)));
            String line;
            while((line = br.readLine()) != null) {
                string.append(line);
                // Formatting requires you to have a \n between "lines"
                string.append("\n");
            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return string.toString();
    }
}
