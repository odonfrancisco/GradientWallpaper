package c.odonfrancisco.gradientwallpaper;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment.ColorPickerDialogListener;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GradientPage extends AppCompatActivity implements ColorPickerDialogListener {
    private static final String TAG = GradientPage.class.getSimpleName();

    private ConstraintLayout constraintLayout;
    private Paint p;
    private ImageView backgroundImgView;
    private ImageView miniImageView;
    private Display display;
    private WallpaperManager wallpaperManager;
    private Bitmap backgroundBitmap;
    private Bitmap miniBitmap;
    private long color0;
    private long color1;
    private Random rand = new Random();
    private int x0;
    private int y0;
    private int x1;
    private int y1;

    private final int color0_Dialog = 0;
    private final int color1_Dialog = 1;


//     Good Gradients
//     0xFFEB2aee 0xff29ede1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_page);

        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setViews();
    }

    private void setViews(){
        constraintLayout = findViewById(R.id.buttonsLayer);
        backgroundImgView = findViewById(R.id.image);

        display = getWindowManager().getDefaultDisplay();
        wallpaperManager = WallpaperManager.getInstance(GradientPage.this);

        color0 = generateRandomHex();
        color1 = generateRandomHex();

        x0 = getWindowSize()[0];
        x1 = getWindowSize()[0];
        y0 = 0;
        y1 = getWindowSize()[1];

        createGradient(color0, color1);

        Button topColorButt = findViewById(R.id.topColorButton);
        Button bottomColorButt = findViewById(R.id.bottomColorButton);

        setColorClickListeners(topColorButt);
        setColorClickListeners(bottomColorButt);
    }

    private void setColorClickListeners(Button button0){
        button0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("MotionEvent", String.valueOf(event));

                if(event.getAction() == MotionEvent.ACTION_UP){
                    long downTime = event.getEventTime() - event.getDownTime();
                    Log.i("MotionEvent Downtime", String.valueOf(downTime));
                    if(downTime > ViewConfiguration.getLongPressTimeout()){
                        constraintLayout.setVisibility(View.VISIBLE);
                    } else {
                        onClickColorPickerDialog(v);
                    }

                }
                return false;
            }

        });

        button0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(GradientPage.this, "Long click enabled", Toast.LENGTH_SHORT).show();
                constraintLayout.setVisibility(View.INVISIBLE);
                return true;
            }

        });
    }


    private int[] getWindowSize(){
        Point size = new Point();

        display.getSize(size);

        int width = size.x;
        int height = size.y;

        return new int[] {width, height};
    }


    public void onClickColorPickerDialog(View view){
        int tag = Integer.parseInt((String) view.getTag());
        long currentColor = Color.GREEN;
        switch(tag){
            case 0: currentColor = color0;
                    break;
            case 1: currentColor = color1;
                    break;
        }

        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(tag, null, null, (int) currentColor, true);

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
            f.show(getFragmentManager(), "d");
    }

    @Override
    public void onColorSelected(int dialogId, int color){
        switch(dialogId) {
            case color1_Dialog:
                Toast.makeText(GradientPage.this, "Selected Color: " + colorToHexString(color), Toast.LENGTH_SHORT).show();
                Log.d(TAG, colorToHexString(color));
                color1 = color;
                break;
            case color0_Dialog:
                color0 = color;
                Toast.makeText(GradientPage.this, "Selected Color: " + colorToHexString(color), Toast.LENGTH_SHORT).show();
                Log.d(TAG, colorToHexString(color));
                break;
        }

        createGradient(color0, color1);
    }

    @Override
    public void onDialogDismissed(int dialogId){
        switch(dialogId){
            case color1_Dialog:
                // Waht do
                break;
        }
    }

    private static String colorToHexString(int color){
        return String.format("#%06x", 0xFFFFFFFF & color);
    }


    private void createGradient(long color1, long color2){

        LinearGradient gradient = new LinearGradient(x0, y0, x1, y1, (int) color1, (int) color2, Shader.TileMode.CLAMP);

        p = new Paint();
        p.setDither(true);
        p.setShader(gradient);

        createGradientBitmap();

    }

    private Paint createPaint(long color0, long color1, int width, int height, LinearGradient gradient){
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);

        return p;
    }

    private void createGradientBitmap(){
        backgroundBitmap = Bitmap.createBitmap(getWindowSize()[0],
                getWindowSize()[1], Bitmap.Config.ARGB_8888);
//        miniBitmap = Bitmap.createBitmap(miniImageView.getWidth(),
//                miniImageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(backgroundBitmap);
//        Canvas miniCanvas = new Canvas(miniBitmap);
        backgroundCanvas.drawRect(new RectF(0, 0, getWindowSize()[0], getWindowSize()[1]), p);
//        miniCanvas.drawRect(new RectF(0, 0, miniImageView.getWidth(), miniImageView.getHeight(), ));


        backgroundImgView.setImageBitmap(backgroundBitmap);
    }

    public void saveToWallpaper(View view){

        try {
            wallpaperManager.setBitmap(backgroundBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reverseColors(View view){
        long tempColor = color0;
        color0 = color1;
        color1 = tempColor;

        createGradient(color0, color1);

        generateRandomHex();
    }

    public void newRandomGradient(View view){
        color0 = generateRandomHex();
        color1 = generateRandomHex();

        createGradient(color0, color1);

    }

    // should I have these outside of the class?
        // since it's just code that's being used by the
        // create randomhex function?
    private long generateRandomHex(){
        String hexCode = "FF";
        ArrayList<String> stringHexes = new ArrayList<String>();

        for(int i=0; i<3; i++){
            stringHexes.add(generatePartialHex());
        }

        if(!stringHexes.contains("FF")){
            System.out.println("FF Aint here Chief");
//            stringHexes.set(rand.nextInt(3),"FF");
        }

        for(int i=0; i<3; i++){
            hexCode += stringHexes.get(i);
        }

        return Long.parseLong(hexCode, 16);
    }

    private String generatePartialHex(){
        String hexCode = "";

        for(int i=0; i<2; i++){
            int hexInt = rand.nextInt(16);
            hexCode += Integer.toHexString(hexInt);
        }
        return hexCode;
    }

    public void angleGradient(View view){
        int xEighth = getWindowSize()[0]/6;
        int yEighth = getWindowSize()[1]/8;

        if(x0 == getWindowSize()[0] && x1 < getWindowSize()[0]){
            x1 += xEighth/2;
            if(x1 > getWindowSize()[0]){
                x1 = getWindowSize()[0];
            }
        }

        if(y1 == getWindowSize()[1] && y0 > 0){
            y0 -= yEighth;
            if(y0 < 0){
                y0 = 0;
            }
        }

        if(x1 == 0 && x0 < getWindowSize()[0]){
            x0 += xEighth;
            if(x0 > getWindowSize()[0]){
                x0 = getWindowSize()[0];
            }
        }

        if(y0 == getWindowSize()[1] && y1 < getWindowSize()[1]){
            y1 += yEighth;
            if(y1 > getWindowSize()[1]){
                y1 = getWindowSize()[1];
            }
        }

        if(y1 == 0 && y0 < getWindowSize()[1]){
            y0 += yEighth;
            if(y0 > getWindowSize()[1]){
                y0 = getWindowSize()[1];
            }
        }

        if(x0 == 0 && x1 > 0){
            x1 -= xEighth/2;
        }


        if(x1 == getWindowSize()[0] && x0 > 0){
            x0 -= xEighth;

            if(x0 < 0){
                x0 = 0;
            }
        }

        if(y0 == 0 && y1 > 0){
            y1 -= yEighth;

            if (y1 < 0) {
                y1 = 0;
            }
        }


        Log.i("gradientAxis x0", Integer.toString(x0));
        Log.i("gradientAxis x1", Integer.toString(x1));
        Log.i("gradientAxis y0", Integer.toString(y0));
        Log.i("gradientAxis y1", Integer.toString(y1));

        createGradient(color0, color1);
    }
}
