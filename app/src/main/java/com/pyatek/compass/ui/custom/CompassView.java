package com.pyatek.compass.ui.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.pyatek.compass.R;

public class CompassView extends View implements View.OnClickListener{
    int bearing;
    int destinationBearing;
    private Paint markerPaint;
    private Paint textPaint;
    private Paint smallTextPaint;
    private Paint circlePaint;
    private String northString;
    private String eastString;
    private String southString;
    private String westString;
    private RectF oval;
    private InverseBindingListener inverseBindingListener;
    private int textHeight;
    private int smallTextHeight;

    @BindingAdapter("bearing")
    public static void setBearing(CompassView view, int _bearing) {
        if (view.bearing != _bearing) {
            view.bearing = _bearing;
            view.invalidate();
        }
    }

    @BindingAdapter("destination_bearing")
    public static void setDestinationBearing(CompassView view, int _destinationBearing) {
        if (view.destinationBearing != _destinationBearing) {
            view.destinationBearing = _destinationBearing;
        }
    }

    @InverseBindingAdapter(attribute = "bearing")
    public static int getBearing(CompassView view) {
        return view.bearing;
    }

    @InverseBindingAdapter(attribute = "destination_bearing")
    public static int getDestinationBearing(CompassView view) {
        return view.bearing;
    }

    @BindingAdapter(value = "bearingAttrChanged")
    public static void setBearingChangeListener(CompassView view, final InverseBindingListener listener){
        view.inverseBindingListener = listener;
    }

    @BindingAdapter(value = "destination_bearingAttrChanged")
    public static void setDestinationBearingChangeListener(CompassView view, final InverseBindingListener listener){
        view.inverseBindingListener = listener;
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompassView();
        setOnClickListener(this);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCompassView();
        setOnClickListener(this);
    }

    public CompassView(Context context) {
        super(context);
        initCompassView();
        setOnClickListener(this);
    }

    public CompassView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initCompassView();
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        inverseBindingListener.onChange();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // The compass is a circle that fills as much space as possible. // Set the measured dimensions by figuring out the shortest boundary, // height or width.

        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    @Override protected void onDraw(Canvas canvas) {
        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() /2 ;
        int radius = Math.min(px, py);
        canvas.drawCircle(px, py, radius, circlePaint);
        canvas.save();
        canvas.rotate(-bearing, px, py);
        int textWidth = (int)textPaint.measureText("W");
        int cardinalX = px-textWidth/2;
        int cardinalY = py-radius+textHeight;

        for (int i = 0; i < 36; i++) {
            canvas.drawLine(px, py-radius, px, (py - radius + 30), markerPaint);
            canvas.save();
            canvas.translate(0, textHeight);

            int currentAngle = i * 10;
            int nextAngle = (i + 1) * 10;
            if (destinationBearing > currentAngle && destinationBearing < nextAngle) {
                changePaintColor(textPaint, R.color.direction_text_color);
                oval.set(px - radius, py - radius, px + radius, py + radius);
                canvas.drawArc(oval, 0, 10, true, textPaint);
                changePaintColor(textPaint, R.color.text_color);
            }

            if (i % 9 == 0) {
                String dirString = "";
                switch (i) {
                    case 0: {
                        dirString = northString;
                        break;
                    }
                    case 9: {
                        dirString = eastString;
                        break;
                    }
                    case 18: {
                        dirString = southString;
                        break;
                    }
                    case 27: {
                        dirString = westString;
                        break;
                    }
                }
                changePaintColor(textPaint, R.color.direction_text_color);
                canvas.drawText(dirString, cardinalX, cardinalY - 20, textPaint);
                changePaintColor(textPaint, R.color.text_color);
            } else if (i % 3 == 0) {
                String angle = String.valueOf(currentAngle);
                float angleTextWidth = textPaint.measureText(angle);

                int angleTextX = (int) (px - angleTextWidth / 2);
                int angleTextY = py - radius + textHeight - 20;
                canvas.drawText(angle, angleTextX, angleTextY, textPaint);
            } else {
                String angle = String.valueOf(currentAngle);
                float angleTextWidth = smallTextPaint.measureText(angle);
                int angleTextX = (int) (px - angleTextWidth / 2);
                int angleTextY = py - radius + smallTextHeight + 10;
                canvas.drawText(angle, angleTextX, angleTextY - 20, smallTextPaint);
            }

            canvas.restore();
            canvas.rotate(10, px, py);
        }

        canvas.restore();
    }

    protected void initCompassView() {
        setFocusable(true);
        Resources resources = this.getResources();
        setupBackgroundCircle(resources);
        setupDirectionString(resources);
        setupTextPaint(resources);
        setupMarkerPaint(resources);
        setupSmallDegreeTextPaint(resources);
        setupDirectionRect();
    }

    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }
        return result;
    }

    private void setupDirectionRect() {
        oval = new RectF();
    }

    private void changePaintColor(Paint paint, @ColorRes int colorId) {
        paint.setColor(this.getResources().getColor(colorId));
    }

    private void setupBackgroundCircle(Resources resources) {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(resources.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void setupDirectionString(Resources resources) {
        northString = resources.getString(R.string.cardinal_north);
        eastString = resources.getString(R.string.cardinal_east);
        southString = resources.getString(R.string.cardinal_south);
        westString = resources.getString(R.string.cardinal_west);
    }

    private void setupTextPaint(Resources resources) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(resources.getColor(R.color.text_color));
        textPaint.setTextSize(50f);
        textPaint.setColor(resources.getColor(R.color.text_color));
        textHeight = (int) textPaint.measureText("yY");
    }

    private void setupMarkerPaint(Resources resources) {
        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(resources.getColor(R.color.marker_color));
    }

    private void setupSmallDegreeTextPaint(Resources resources) {
        smallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallTextPaint.setColor(resources.getColor(R.color.text_color));
        smallTextPaint.setTextSize(20f);
        smallTextHeight = (int) smallTextPaint.measureText("yY");
    }
}
