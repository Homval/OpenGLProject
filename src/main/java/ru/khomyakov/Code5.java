package ru.khomyakov;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.GLSLUtils;
import graphicslib3D.Matrix3D;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.Vector;

import static com.jogamp.opengl.GL2ES2.*;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Code5 extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int[] vao = new int[1];
    private int[] vbo = new int[2];
    private float cameraX, cameraY, cameraZ;
    private float cubeLocX, cubeLocY, cubeLocZ;
    private GLSLUtils utils = new GLSLUtils();
    private Matrix3D pMat;

    public Code5() {
        setTitle("Chapter4 - program1");
        setSize(600, 600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
        FPSAnimator animator = new FPSAnimator(myCanvas, 50);
        animator.start();
    }

    public static void main(String[] args) {
        new Code5();
    }



    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        float[] bkg = {0.0f, 0.0f, 0.0f, 1.0f};
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        gl.glUseProgram(rendering_program);

        //build view matrix
        Matrix3D vMat = new Matrix3D();
        vMat.translate(-cameraX, -cameraY, -cameraZ);
        //build model matrix
        Matrix3D mMat = new Matrix3D();
        // use system time to generate slowly-increasing sequence of floating-point values
        double t = (double)(System.currentTimeMillis()) / 10000.0;
        mMat.translate(Math.sin(2 * t) * 2.0, Math.sin(3 * t) * 2.0, Math.sin(4 * t) * 2.0);
        mMat.rotate(1000 * t, 1000 * t, 1000 * t);
//        mMat.translate(cubeLocX, cubeLocY, cubeLocZ);
        // concatenate view and model matrix to create vm matrix
        Matrix3D mvMat = new Matrix3D();
        mvMat.concatenate(vMat);
        mvMat.concatenate(mMat);

        // copy vm and perspective matrices to uniform variables
        int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvMat.getFloatValues(), 0);

        //associate VBO with the corresponding vertex attribute in the vertex shader
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        //adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {}

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        setupVertices();
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 8.0f;
        cubeLocX = 0.0f;
        cubeLocY = -2.0f;
        cubeLocZ = 0.0f;
        //create perspective matrix, this one has fovy = 60.0f and aspect ratio matches screen window
        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
    }

    private void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        // 36 vertices of the 12 triangles making up a 2 x 2 x 2 cube centered at the origin
        float[ ] vertex_positions =
                { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
                };
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertBuff = Buffers.newDirectFloatBuffer(vertex_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuff.limit() * 4L, vertBuff, GL_STATIC_DRAW);
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];

        String[] vShaderSource = readShaderSource("src/main/resources/vert5.shader");

        String[] fShaderSource = readShaderSource("src/main/resources/frag5.shader");

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
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
        gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
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

    private String[] readShaderSource(String fileName) {
        Vector<String> lines = new Vector<>();
        Scanner sc;
        try {
            sc = new Scanner(new File(fileName));
        } catch (IOException e) {
            System.err.println("IOException reading file: " + e);
            return null;
        }

        while (sc.hasNext()) {
            lines.addElement(sc.nextLine());
        }

        String[] program = new String[lines.size()];
        for (int i = 0; i < program.length; i++) {
            program[i] = lines.elementAt(i) + "\n";
        }
        return program;
    }

    private Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float q=1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0,0, A);
        r.setElementAt(1,1, q);
        r.setElementAt(2,2, B);
        r.setElementAt(3,2, -1.0f);
        r.setElementAt(2,3, C);
        r.setElementAt(3,3, 0.0f);
        return r;

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
            for (byte b : log) {
                System.out.print((char) b);
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
            for (byte b : log) {
                System.out.print((char) b);
            }
        }
    }

    void checkOpenGLError() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {}
}
