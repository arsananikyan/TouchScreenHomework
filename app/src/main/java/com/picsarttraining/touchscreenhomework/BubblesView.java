package com.picsarttraining.touchscreenhomework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Arsen on 16.03.2016.
 */
public class BubblesView extends View {
    private static final float TOUCH_MOVE_THRESHOLD = 10f;
    private static final long CLICK_DURATION_LIMIT_MS = 2000;

    private PointF actionDownPoint;

    private Paint paint;
    private Random random;
    private Circle selectedCircle;
    boolean isClick = true;

    private List<Circle> circleList;

    public BubblesView(Context context) {
        this(context, null);
    }

    public BubblesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        actionDownPoint = new PointF();
        circleList = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.RED);
        random = new Random();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Circle circle : circleList) {
            paint.setColor(circle.color);
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        final int action = event.getActionMasked();

        boolean handled = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDownPoint.set(event.getX(), event.getY());
                selectedCircle = findCircleIn(event.getX(), event.getY());
                handled = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                handled = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float dist = (float) Math.hypot(x - actionDownPoint.x, y - actionDownPoint.y);
                if (dist > TOUCH_MOVE_THRESHOLD) {
                    if (selectedCircle != null) {
                        selectedCircle.x = x;
                        selectedCircle.y = y;
                    }
                    isClick = false;
                } else {
                    handled = true;
                    isClick = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - event.getDownTime() <= CLICK_DURATION_LIMIT_MS && isClick) {
                    x = event.getX();
                    y = event.getY();
                    if (selectedCircle != null) {
                        circleList.remove(selectedCircle);
                        break;
                    }
                    addRandomCircleIn(x, y);
                    handled = true;
                } else {
                    handled = false;
                }
                break;

            default:
                break;
        }

        invalidate();
        return handled;
    }

    private void addRandomCircleIn(float x, float y) {
        int randomColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        float randomRadius = (float) random.nextInt(50) + 50;
        circleList.add(new Circle(x, y, randomRadius, randomColor));
    }

    private Circle findCircleIn(float x, float y) {
        //We need to find Circle with the biggest z-index
        for (int i=circleList.size()-1;i>=0;i--) {
            if (Math.hypot(circleList.get(i).x - x, circleList.get(i).y - y) < circleList.get(i).radius) {
                return circleList.get(i);
            }
        }
        return null;
    }

    public static class Circle {
        private float x;
        private float y;
        private int color;
        private float radius;

        public Circle(float x, float y, float radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }
}
