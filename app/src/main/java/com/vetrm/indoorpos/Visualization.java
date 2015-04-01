package com.vetrm.indoorpos;

import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


public class Visualization extends ActionBarActivity {
    private static App app;
    private static DeviceMan devman;

    private static Visualization master = null;

    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;
    private FrameBuffer fb = null;
    private World world = null;
    private RGBColor back = new RGBColor(50, 50, 100);

    private float touchTurn = 0;
    private float touchTurnUp = 0;

    private float xpos = -1;
    private float ypos = -1;

    private Object3D cube = null;
    private Object3D ground = null;
    private int fps = 0;
    private boolean gl2 = true;

    private Light sun = null;

    private Camera cam;
    private OrbitControls controls;

    private XYZ[] PL_XYZ = new XYZ[]{
            new XYZ(5.0f, 5.0f, 8.000000f),
            new XYZ(-5.0f, -5.0f, 8.000000f),
            new XYZ(-5.0f, 5.0f, 8.000000f)
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visualization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("onCreate");

        if (master != null) {
            copy(master);
        }

        super.onCreate(savedInstanceState);
        mGLView = new GLSurfaceView(getApplication());

        app = App.getInstance();
        devman = app.getDevman();

        if (gl2) {
            mGLView.setEGLContextClientVersion(2);
        } else {
            mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
                public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                    // Ensure that we get a 16bit framebuffer. Otherwise, we'll
                    // fall back to Pixelflinger on some device (read: Samsung
                    // I7500). Current devices usually don't need this, but it
                    // doesn't hurt either.
                    int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] result = new int[1];
                    egl.eglChooseConfig(display, attributes, configs, 1, result);
                    return configs[0];
                }
            });

        }

        renderer = new MyRenderer();
        mGLView.setRenderer(renderer);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void copy(Object src) {
        try {
            Logger.log("Copying data from master Activity!");
            Field[] fs = src.getClass().getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void log(Object obj) {
        Log.d("Visual", "" + obj);
    }

    public boolean onTouchEvent(MotionEvent me) {

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;

            xpos = me.getX();
            ypos = me.getY();

            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            return true;
        }

        try {
            Thread.sleep(15);
        } catch (Exception e) {
            // No need for this...
        }

        return super.onTouchEvent(me);
    }


    protected boolean isFullscreenOpaque() {
        return true;
    }

    class MyRenderer implements GLSurfaceView.Renderer {

        private long logTimer = System.currentTimeMillis();
        private long updateTimer = System.currentTimeMillis();

        public MyRenderer() {
        }

        public SimpleVector transfer(float x, float y, float z) {
            final float factor = 1.2f;
            final XYZ move = new XYZ(0f, 0f, 0f);
            return new SimpleVector(factor * y + move.y, factor * -z - move.z, factor * -x - move.x);
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            if (fb != null) {
                fb.dispose();
            }

            if (gl2) {
                fb = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
            } else {
                fb = new FrameBuffer(gl, w, h); // OpenGL ES 1.x constructor
            }

            if (master == null) {

                world = new World();
                world.setAmbientLight(20, 20, 20);

                sun = new Light(world);
                sun.setIntensity(250, 250, 250);

                // Create a texture out of the icon...:-)
                Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
                TextureManager.getInstance().addTexture("texture", texture);
                // grass gound
                Texture grassGround = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.grass_ground)), 1024, 1024));
                TextureManager.getInstance().addTexture("grass ground", grassGround);

                cube = Primitives.getSphere(1);
                cube.calcTextureWrapSpherical();
                cube.setTexture("grass ground");
                cube.strip();
                cube.build();

                ground = Primitives.getPlane(30, 1);
                ground.calcTextureWrapSpherical();
                ground.strip();
                ground.build();
                ground.translate(0, 0, 0);
                ground.rotateX((float) Math.PI / 2f);

                world.addObject(cube);
                world.addObject(ground);

                for (int i = 0; i < PL_XYZ.length; i++) {
                    Object3D pl = Primitives.getDoubleCone(0.5f);
                    pl.calcTextureWrapSpherical();
                    pl.strip();
                    pl.build();
                    pl.translate(transfer(PL_XYZ[i].x, PL_XYZ[i].y, PL_XYZ[i].z));
                    world.addObject(pl);
                }

                cam = world.getCamera();
                cam.setPosition(0.0f, -15.0f, -30.0f);
                cam.lookAt(cube.getTransformedCenter());

                controls = new OrbitControls(cam);

                SimpleVector sv = new SimpleVector();
                sv.set(cube.getTransformedCenter());
                sv.y -= 100;
                sv.z -= 100;
                sun.setPosition(sv);
                MemoryHelper.compact();

                if (master == null) {
                    Logger.log("Saving master Activity!");
                    master = Visualization.this;
                }
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }

        public void onDrawFrame(GL10 gl) {
            cube.rotateY(0.01f);
            cube.rotateX(0.01f);

            if (touchTurn != 0) {
                controls.spinY(touchTurn / 10);
                touchTurn = 0;
            }

            if (touchTurnUp != 0) {
                controls.spinX(touchTurnUp / 10);
                touchTurnUp = 0;
            }

            fb.clear(back);
            world.renderScene(fb);
            world.draw(fb);
            fb.display();

            if (System.currentTimeMillis() - logTimer >= 10000) {
                logTimer = System.currentTimeMillis();
                // Log
                Logger.log(fps / 10 + "fps");
                fps = 0;
                Logger.log(cube.getTranslation().toString());
            }
            fps++;

            if (System.currentTimeMillis() - updateTimer >= 500) {
                updateTimer = System.currentTimeMillis();
                float[] ranges = devman.readDists();
                XYZ res =  Compute.Solve3d(PL_XYZ, ranges);
                log(res.toString());

                if (!Float.isNaN(res.x) && !Float.isNaN(res.y) && !Float.isNaN(res.y)) {
                    SimpleVector cubePos = cube.getTranslation();
                    SimpleVector toZero = new SimpleVector(0f, 0f, 0f);
                    toZero.sub(cubePos);

                    cube.translate(toZero);
                    cube.translate(transfer(res.x, res.y, res.z));
                    // cube.
                } else {
                    log("Error in solving: " + Arrays.toString(ranges));
                }
                //log(cam.getPosition());
                //log(cam.getXAxis());
                //log(cam.getYAxis());
                //log(cam.getZAxis());
            }
        }
    }
}
/*
if (touchTurn != 0) {
        cube.rotateY(touchTurn);
        touchTurn = 0;
        }

        if (touchTurnUp != 0) {
        cube.rotateX(touchTurnUp);
        touchTurnUp = 0;
        }
        */

class OrbitControls {
    private Camera camera;

    OrbitControls(Camera cam) {
        camera = cam;
    }

    public void spinY(float deg) {
        SimpleVector position = camera.getPosition();
        position.rotateY(deg);
        camera.setPosition(position);
        camera.lookAt(new SimpleVector(0f, 0f, 0f));
    }

    public void spinX(float deg) {
        SimpleVector position = camera.getPosition();
        position.rotateX(deg);
        camera.setPosition(position);
        camera.lookAt(new SimpleVector(0f, 0f, 0f));
    }
}
