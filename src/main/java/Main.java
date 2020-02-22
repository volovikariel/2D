import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {

        glfwInit();
        glfwDefaultWindowHints();
        // Set window to invisible to recenter it
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long window = glfwCreateWindow(WIDTH, HEIGHT, "Simulator", NULL, NULL);
        if(window == 0) {
            throw new IllegalStateException("Failed to initialize GLFW!");
        }
        // Contains information about our monitor
        GLFWVidMode glfwVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (glfwVidMode.width() - WIDTH)/2, (glfwVidMode.height() - HEIGHT)/2);
        glfwShowWindow(window);
        // Context is the image that is on the graphics card, it's what gets drawn
        // Sends a request for a context
        glfwMakeContextCurrent(window);
        // Creates the requested context
        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);
        // Creates texture and binds it
        Texture tex = new Texture("256x256_texture.png");

        // Enables V-Sync
        glfwSwapInterval(1);

        // Create Model
        float[] vertices = new float[] {
                -0.5f, 0.5f, 0, // TL
                 0.5f, 0.5f, 0, // TR
                 0.5f,-0.5f, 0, // BR
                -0.5f,-0.5f, 0  // BL
        };
        float[] texture = new float[] {
                0, 0, // TL
                1, 0, // TR
                1, 1, // BR
                0, 1, // BL
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };
        Model model = new Model(vertices, texture, indices);

        Shader shader = new Shader("shader");

        Camera camera = new Camera(WIDTH, HEIGHT);

        Matrix4f target = new Matrix4f();
        Matrix4f scale = new Matrix4f().scale(128);

        // Sets the clear colour when buffers are swapped (instead of black)
        glClearColor(0.1f,0.1f,0.1f,0);

        while(!glfwWindowShouldClose(window)) {
            target = scale;
            // Inputs
            if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
            if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
                camera.addPosition(new Vector3f(0, 5f, 0));
            }
            if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
                camera.addPosition(new Vector3f(0, -5f, 0));
            }
            if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
                camera.addPosition(new Vector3f(5, 0, 0));
            }
            if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
                camera.addPosition(new Vector3f(-5, 0, 0));
            }
            if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
                // Reset Position to 0,0
                camera.setPosition(new Vector3f());
            }
            // Check for events
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);

            shader.bind();

            shader.setUniform("sampler", 0);

            shader.setUniform("projection", camera.getProjection().mul(target));
            tex.bind(0);
            model.render();

            // Clears the color
            glfwSwapBuffers(window);
        }
        // Terminate window
        glfwTerminate();
    }

}
