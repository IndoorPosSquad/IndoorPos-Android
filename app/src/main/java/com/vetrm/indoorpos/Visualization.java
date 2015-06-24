package com.vetrm.indoorpos;

import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

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

    private int mode = 0;
    private float xpos = -1;
    private float ypos = -1;
    private float oldDist;

    private Object3D target = null;
    private Object3D cube = null;
    private Object3D ground = null;

    private Object3D wall1 = null;
    private Object3D wall2 = null;


    private int fps = 0;
    private boolean gl2 = true;

    private Light sun = null;

    private Camera cam;
    private TwoDConstrols controls;

    private XYZ[] currentPos = new XYZ[]{
            new XYZ(0f, 4.8f, 3.000000f),
            new XYZ(0f, 0f, 3.000000f),
            new XYZ(4.8f, 0f, 3.000000f)
    };

    private Object3D[] anchors = new Object3D[3];


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

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xpos = event.getX();
                ypos = event.getY();
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                xpos = -1;
                ypos = -1;
                touchTurn = 0;
                touchTurnUp = 0;
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                log("Old Dist:" + oldDist);
                mode += 1;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode >= 2) {
                    float newDist = spacing(event);
                    log("New Dist:" + newDist);
                    if (newDist > oldDist + 1) {
                        controls.moveZ(newDist / oldDist / 20);
                        oldDist = newDist;
                    }
                    if (newDist < oldDist - 1) {
                        controls.moveZ(- newDist / oldDist / 20);
                        oldDist = newDist;
                    }
                } else {
                    float xd = event.getX() - xpos;
                    float yd = event.getY() - ypos;

                    xpos = event.getX();
                    ypos = event.getY();

                    touchTurn = xd / -100f;
                    touchTurnUp = yd / -100f;
                }
                break;
            default:
                try {
                    Thread.sleep(15);
                } catch (Exception e) {
                    // No need for this...
                }
                return super.onTouchEvent(event);
        }

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
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
            final float factor = app.getDisplayFactor();
            final XYZ offset = app.getDisplayOffset();
            return new SimpleVector(factor * y + offset.y, factor * -z - offset.z, factor * -x - offset.x);
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
                world.setAmbientLight(0, 0, 20);

                sun = new Light(world);
                sun.setIntensity(250, 250, 250);

                // Create a texture out of the icon...:-)
                Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
                TextureManager.getInstance().addTexture("texture", texture);
                // grass gound
                Texture grassGround = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.grass_ground)), 1024, 1024));
                TextureManager.getInstance().addTexture("grass ground", grassGround);

                Texture safeArea = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.safe_area)), 1024, 1024));
                TextureManager.getInstance().addTexture("safe area", safeArea);

                Texture wallWithPic = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.pic_wall)), 1024, 1024));
                TextureManager.getInstance().addTexture("ground", wallWithPic);

                Texture ground_color = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.ground_color)), 1024, 1024));
                TextureManager.getInstance().addTexture("pic wall", ground_color);

                Texture table_color = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.table_color)), 1024, 1024));
                TextureManager.getInstance().addTexture("table", table_color);

                Texture red_color = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.red_color)), 1024, 1024));
                TextureManager.getInstance().addTexture("red", red_color);

                target = Primitives.getSphere(0.1f);
                target.calcTextureWrap();
                target.setTexture("grass ground");
                target.strip();
                target.build();
                world.addObject(target);

//                cube = Primitives.getCube(0.1f);
//                cube.calcTextureWrap();
//                cube.setTexture("grass ground");
//                cube.strip();
//                cube.build();
//                world.addObject(cube);
//                cube.translate(transfer(1.2f, 0f, 0.2f));
////
////
//                Object3D cube2 = null;
//                cube2 = Primitives.getCube(0.1f);
//                cube2.calcTextureWrap();
//                cube2.setTexture("grass ground");
//                cube2.strip();
//                cube2.build();
//                world.addObject(cube2);
//                cube2.translate(transfer(0f, 0f, 0.2f));


                Object3D table1, table2;
                table1 = Primitives.getPlane((int) (0.75 * 2 * app.getDisplayFactor()), 1f);
                table1.calcTextureWrap();
                table1.setTexture("table");
                table1.strip();
                table1.build();
                table1.translate(transfer(0f, -0.45f, 0.8f));
                table1.rotateX((float) Math.PI / 2f);
                world.addObject(table1);

                table2 = Primitives.getPlane((int) (0.75 * 2 * app.getDisplayFactor()), 1f);
                table2.calcTextureWrap();
                table2.setTexture("table");
                table2.strip();
                table2.build();
                table2.translate(transfer(0f, 0.45f, 0.8f));
                table2.rotateX((float) Math.PI / 2f);
                world.addObject(table2);

                ground = Primitives.getPlane((int) (3 * 2 * app.getDisplayFactor()), 1.06f);
                ground.calcTextureWrap();
                ground.setTexture("ground");
                ground.strip();
                ground.build();
                ground.translate(0, 0, 0);
                ground.rotateX((float) Math.PI / 2f);
                world.addObject(ground);

                currentPos = app.getANCHOR_XYZ();

                for (int i = 0; i < currentPos.length; i++) {
                    Object3D pl = Primitives.getDoubleCone(0.05f);
                    anchors[i] = pl;
                    pl.calcTextureWrapSpherical();
                    pl.strip();
                    pl.build();
                    pl.translate(transfer(currentPos[i].x, currentPos[i].y, currentPos[i].z));
                    world.addObject(pl);
                }

                cam = world.getCamera();
                cam.setPosition(transfer(0.2f, 0f, 8f));
                cam.lookAt(target.getTransformedCenter());

                controls = new TwoDConstrols(cam);

                sun.setPosition(new SimpleVector(transfer(100,100,100)));
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
            target.rotateY(0.01f);
            target.rotateX(0.01f);

            if (touchTurn != 0) {
                controls.moveX(touchTurn / 2);
                touchTurn = 0;
            }

            if (touchTurnUp != 0) {
                controls.moveY(-touchTurnUp / 2);
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
                Logger.log(target.getTranslation().toString());
            }
            fps++;

            if (System.currentTimeMillis() - updateTimer >= 283) {
                updateTimer = System.currentTimeMillis();
                float[] ranges = devman.readDists();
                XYZ res =  Compute.Solve3d(app.getANCHOR_XYZ(), ranges);
                log(res.toString());

                if (!Float.isNaN(res.x) && !Float.isNaN(res.y) && !Float.isNaN(res.y)) {
                    SimpleVector cubePos = target.getTranslation();
                    SimpleVector toZero = new SimpleVector(0f, 0f, 0f);
                    toZero.sub(cubePos);

                    target.translate(toZero);
                    target.translate(transfer(res.x, res.y, res.z));
                    // target.
                } else {
                    log("Error in solving: " + Arrays.toString(ranges));
                }

                currentPos = app.getANCHOR_XYZ();
                for (int i = 0; i < currentPos.length; i++) {
                    SimpleVector pos = anchors[i].getTranslation();
                    SimpleVector toZero = new SimpleVector(0f, 0f, 0f);
                    toZero.sub(pos);
                    anchors[i].translate(toZero);
                    anchors[i].translate(transfer(currentPos[i].x, currentPos[i].y, currentPos[i].z));
                }
//                ranges = devman.readDists();
//                log(ranges);
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
        target.rotateY(touchTurn);
        touchTurn = 0;
        }

        if (touchTurnUp != 0) {
        target.rotateX(touchTurnUp);
        touchTurnUp = 0;
        }
        */
class TwoDConstrols {
    private Camera camera;
    public TwoDConstrols(Camera cam) {
        camera = cam;
    }

    public void moveX(float scale) {
        SimpleVector pos = new SimpleVector();
        camera.getPosition(pos);
        pos.x += scale;
        camera.setPosition(pos);
    }

    public void moveY(float scale) {
        SimpleVector pos = new SimpleVector();
        camera.getPosition(pos);
        pos.z += scale;
        camera.setPosition(pos);
    }

    public void moveZ(float scale) {
        SimpleVector pos = new SimpleVector();
        camera.getPosition(pos);
        pos.y += scale;
        camera.setPosition(pos);
    }
}

class OrbitControls {
    private Camera camera;

    OrbitControls(Camera cam) {
        camera = cam;
    }

    public void spinHorizontal(float deg) {
        SimpleVector position = camera.getPosition();

        position.rotateY(deg);
        camera.setPosition(position);
        camera.lookAt(new SimpleVector(0f, 0f, 0f));
    }

    public void spinVertical(float deg) {
        SimpleVector position = camera.getPosition();

        // calc the axis that spin with
        float x = position.x, y = position.z;
        float a = (float) Math.abs(position.z/(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)))),  b = (float) Math.sqrt(1 - Math.pow(a, 2));

        if (x >= 0 && y >= 0) {a = -a; b = b;}
        if (x < 0 && y >= 0) {a = -a; b = -b;}
        if (x < 0 && y < 0) {a = a; b = -b;}
        if (x >= 0 && y < 0) {a = a; b = b;}
        SimpleVector axis = new SimpleVector(a, 0f, b);

        // don't spin vertically when exceed given tangent range, currently (0, 3)
        float tan = -position.y / (float) (Math.sqrt(Math.pow(position.x, 2) + Math.pow(position.z, 2)));
        if (!((tan < 0 && deg < 0) || (tan > 8 && deg > 0))) {
            position.rotateAxis(axis, deg);
            camera.setPosition(position);
            camera.lookAt(new SimpleVector(0f, 0f, 0f));
        }
        log(tan);
    }

    public void switch2d(boolean switchOrNot) {
        camera.setPosition(0f, 50f, 0f);
        camera.lookAt(new SimpleVector(0f, 0f, 0f));
    }

    private void log(Object obj) {
        Log.d("OrbitControl", "" + obj);
    }
}
//        if (me.getAction() == MotionEvent.)
//        if (me.getAction() == MotionEvent.)
