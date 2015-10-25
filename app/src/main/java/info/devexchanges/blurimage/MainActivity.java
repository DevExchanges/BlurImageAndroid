package info.devexchanges.blurimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textRadius;
    private ImageView imageView;
    private SeekBar seekBar;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textRadius = (TextView) findViewById(R.id.txt_radius);
        imageView = (ImageView) findViewById(R.id.image);
        seekBar = (SeekBar) findViewById(R.id.blur_radius);

        //decode a drawable resource to Bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ao_dai);
        imageView.setImageBitmap(bitmap);

        textRadius.setText("Blur radius: 0");

        //handling seek bar event
        seekBar.setOnSeekBarChangeListener(onSeekBarChanged());

    }

    private OnSeekBarChangeListener onSeekBarChanged() {
        return new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textRadius.setText("Blur radius: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float radius = (float) MainActivity.this.seekBar.getProgress();
                imageView.setImageBitmap(createBlurBitmap(bitmap, radius));
            }
        };
    }

    private Bitmap createBlurBitmap(Bitmap src, float r) {
        if (r <= 0) {
            r = 0.1f;
        } else if (r > 25) {
            r = 25.0f;
        }

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(this);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }
}