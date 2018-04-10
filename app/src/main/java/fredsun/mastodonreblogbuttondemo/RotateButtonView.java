package fredsun.mastodonreblogbuttondemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class RotateButtonView extends View {
    private PathMeasure mPathMeasure;
    Path path, pathTriangle, pathTriangleRight, pathTrans, pathTransRight;
    Paint paint, paintTriangle, paintTrans;
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private RectF mRectF;
    private int mCenterX, mCenterY;
    private int mWidth, mHeight;
    float mAnimatorValue;
    float rectWidth, rectHeight;
    float triangleWidth, triangleHeight;
    float offset, offsetTrans;
    Xfermode xfermode;
    float strokeWidth;

    public RotateButtonView(Context context) {
        super(context);
    }

    public RotateButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        pathTriangle = new Path();
        pathTriangleRight = new Path();
        pathTrans = new Path();
        pathTransRight = new Path();
        paint = new Paint();
        paintTriangle = new Paint();
        paintTrans = new Paint();
        mPathMeasure = new PathMeasure();
        mRectF = new RectF();

        Drawable background = getBackground();
        if (background instanceof ColorDrawable){
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            paintTrans.setColor(color);
        }else if (background instanceof BitmapDrawable){
            throw new AssertionError("you can't set a bitmap as background ");
        }else {
            paintTrans.setColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public RotateButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w/2;
        mCenterY = h/2;
        pos[0] = -w/4;
        pos[1] = 0;
        rectWidth = mWidth * 12 / 42;
        rectHeight =  mHeight* 9 / 36;
        triangleWidth = mWidth * 18 / 42;
        triangleHeight = mHeight * 12 / 36;
        offset = mHeight   / 36 ;
        offsetTrans = mHeight * 3 / 36;
        strokeWidth = mWidth * 6 / 42;

//        path.moveTo(-rectWidth, -offset);//左侧中间点
//        path.lineTo(-rectWidth, -rectHeight);
//        path.lineTo(rectWidth, -rectHeight);
//        path.lineTo(rectWidth, rectHeight);
//        path.lineTo(-rectWidth, rectHeight);
//        path.lineTo(-rectWidth, -offset);

//        path.moveTo(-rectWidth, -offset);//左侧中间点
//        path.lineTo(-rectWidth, -(rectHeight-strokeWidth));
//        RectF rectF = new RectF(-rectWidth, -rectHeight, -(rectWidth - 2 * strokeWidth), -(rectHeight - 2 * strokeWidth));
//        path.arcTo(rectF, -180, 90, false);
//        path.lineTo(-(rectWidth-strokeWidth), -rectHeight );
//        path.lineTo(rectWidth - strokeWidth, -rectHeight);
//        RectF rectRightTop = new RectF(rectWidth - 2 * strokeWidth, -rectHeight, rectWidth, -(rectHeight - 2 * strokeWidth));
//        path.arcTo(rectRightTop, -90, 90);
//        path.lineTo(rectWidth, rectHeight - strokeWidth);
//        RectF rectRightBottom = new RectF(rectWidth - 2 * strokeWidth, rectHeight - 2 * strokeWidth, rectWidth, rectHeight);
//        path.arcTo(rectRightBottom, 0,90,false);
//        path.lineTo(-(rectWidth-strokeWidth), rectHeight);
//        RectF rectLeftBottom = new RectF(-rectWidth, rectHeight - 2 * strokeWidth, -(rectWidth - 2 * strokeWidth), rectHeight);
//        path.arcTo(rectLeftBottom, 90, 90, false);
//        path.lineTo(-rectWidth, -offset);

        path.moveTo(-rectWidth, -offset);//左侧中间点
        path.lineTo(-rectWidth, -(rectHeight-strokeWidth));
        RectF rectF = new RectF(-rectWidth, -rectHeight, -(rectWidth - 2 * strokeWidth/2), -(rectHeight - 2 * strokeWidth/2));
        path.arcTo(rectF, -180+22.5f, 45, false);
        path.lineTo(-(rectWidth-strokeWidth/2), -rectHeight );
        path.lineTo(rectWidth - strokeWidth/2, -rectHeight);
        RectF rectRightTop = new RectF(rectWidth - 2 * strokeWidth/2, -rectHeight, rectWidth, -(rectHeight - 2 * strokeWidth/2));
        path.arcTo(rectRightTop, -90+22.5f, 45);
        path.lineTo(rectWidth, rectHeight - strokeWidth/2);
        RectF rectRightBottom = new RectF(rectWidth - 2 * strokeWidth/2, rectHeight - 2 * strokeWidth/2, rectWidth, rectHeight);
        path.arcTo(rectRightBottom, 22.5f,45,false);
        path.lineTo(-(rectWidth-strokeWidth/2), rectHeight);
        RectF rectLeftBottom = new RectF(-rectWidth, rectHeight - 2 * strokeWidth/2, -(rectWidth - 2 * strokeWidth/2), rectHeight);
        path.arcTo(rectLeftBottom, 125f, 45, false);
        path.lineTo(-rectWidth, -offset);

        mPathMeasure.setPath(path, true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(getResources().getColor(R.color.colorAccent));

        pathTrans.moveTo(offset,-triangleWidth/2);
        pathTrans.lineTo(triangleHeight+offset,0);
        pathTrans.lineTo(offset,triangleWidth/2);
        paintTrans.setStyle(Paint.Style.STROKE);
        paintTrans.setStrokeWidth(strokeWidth/2);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
        paintTrans.setXfermode(xfermode);

        paintTriangle.setStyle(Paint.Style.FILL);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(50);
        paintTriangle.setPathEffect(cornerPathEffect);
        paintTriangle.setColor(getResources().getColor(R.color.colorAccent));
        pathTriangle.lineTo(0,-triangleWidth / 2);
        pathTriangle.lineTo(triangleHeight,0);
        pathTriangle.lineTo(0,triangleWidth / 2);
        pathTriangle.close();

        pathTriangleRight.lineTo(0,-triangleWidth/2);
        pathTriangleRight.lineTo(-triangleHeight,0);
        pathTriangleRight.lineTo(0,triangleWidth/2);
        pathTriangleRight.close();

        pathTransRight.moveTo(-offset,-triangleWidth/2);
        pathTransRight.lineTo(-triangleHeight-offset,0);
        pathTransRight.lineTo(-offset,triangleWidth/2);


    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mAnimatorValue == 1){
            paint.setColor(getResources().getColor(R.color.colorYellow));
            paintTriangle.setColor(getResources().getColor(R.color.colorYellow));
        }
        mPathMeasure.getPosTan(mAnimatorValue * mPathMeasure.getLength()/2, pos, tan);

        canvas.save();
        canvas.translate(mWidth/2, mHeight/2);//坐标系原点切到控件1/2处
        canvas.drawPath(path, paint);

        float degree = (float) (Math.atan2(tan[1], tan[0])*180.0/ Math.PI);
        Log.i("LineView","degree"+degree);
        canvas.translate(pos[0],pos[1]);
        canvas.rotate(degree);
//        canvas.drawLine(0,-200, 200, 0, paintTriangle);
//        canvas.drawLine(200,0, 0, 200, paintTriangle);
//        canvas.drawLine(0,200, 0, -200, paintTriangle);
        int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangle, paintTriangle);
        canvas.drawPath(pathTrans, paintTrans);
        canvas.restoreToCount(i);

        canvas.rotate(-degree);
        canvas.translate(-pos[0], -pos[1]);
        canvas.translate(-pos[0], -pos[1]);
        canvas.rotate(degree);

        int j = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangleRight, paintTriangle);
        canvas.drawPath(pathTransRight, paintTrans);
        canvas.restoreToCount(j);

        canvas.restore();
        Log.i("LineView","pos[0]+"+pos[0]);
        Log.i("LineView","pos[1]+"+pos[1]);
        Log.i("LineView","tan[0]+"+tan[0]);
        Log.i("LineView","tan[1]+"+tan[1]);
        Log.i("LineView","mAnimatorValue+"+mAnimatorValue);


    }

    void startMove(){

        //创建一个值从0到xxx的动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(1200);
        //每过10毫秒 调用一次
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                Log.i("LineView","mAnimatorValue+"+mAnimatorValue);
                Log.i("LineView","distance+"+mAnimatorValue * mPathMeasure.getLength());

                postInvalidate();
            }
        });
        valueAnimator.start();
    }


}
