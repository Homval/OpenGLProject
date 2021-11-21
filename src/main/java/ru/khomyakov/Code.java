package ru.khomyakov;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;

import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Code extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;

    public Code() {
        setTitle("Chapter2 - program1");
        setSize(600, 400);
        setLocation(200, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Code();
    }



    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] bkg = {1.0f, 0.0f, 0.0f, 1.0f};
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {}

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {}

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {}
}
