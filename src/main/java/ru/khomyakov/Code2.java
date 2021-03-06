package ru.khomyakov;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;

import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL2ES2.*;

public class Code2 extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int[] vao = new int[1];

    public Code2() {
        setTitle("Chapter2 - program1");
        setSize(600, 400);
        setLocation(200, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Code2();
    }



    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program);
        gl.glPointSize(30.0f);
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {}

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);

    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];

        String[] vShaderSource = {
                "#version 430 \n",
                "void main(void) \n",
                "{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n"
        };

        String[] fShaderSource = {
                "#version 430 \n",
                "out vec4 color; \n",
                "void main(void) \n",
                "{ if (gl_FragCoord.x < 300) color = vec4(1.0, 0.0, 0.0, 1.0); else color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
        };

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vShaderSource, null, 0);
        gl.glCompileShader(vShader);
        checkOpenGLError();
        gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] == 1) {
            System.out.println("... vertex compilation success.");
        } else {
            System.out.println("... vertex compilation failed.");
            printShaderLog(vShader);
        }

        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fShaderSource, null, 0);
        gl.glCompileShader(fShader);
        checkOpenGLError();
        gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] == 1) {
            System.out.println("... fragment compilation success.");
        } else {
            System.out.println("... fragment compilation failed.");
            printShaderLog(fShader);
        }

        if (vertCompiled[0] != 1 || fragCompiled[0] != 1) {
            System.out.println("\nCompilation error; return flags: ");
            System.out.println("vertCompiled = " + vertCompiled[0] + " fragCompiled = " + fragCompiled[0]);
        } else {
            System.out.println("Successful compilation.");
        }

        int vfProgram = gl.glCreateProgram();
        gl.glAttachShader(vfProgram, vShader);
        gl.glAttachShader(vfProgram, fShader);
        gl.glLinkProgram(vfProgram);
        checkOpenGLError();
        gl.glGetProgramiv(vfProgram, GL_LINK_STATUS, linked, 0);
        if (linked[0] == 1) {
            System.out.println("... linking succeeded.");
        } else {
            System.out.println("... linking failed.");
            printProgramLog(vfProgram);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);

        return vfProgram;
    }

    private void printShaderLog(int shader) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len,0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    void printProgramLog(int prog) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    boolean checkOpenGLError() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {}
}
