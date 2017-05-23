package com.example.lg.work11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LG on 2017-05-18.
 */

public class MyCanvas extends View {

    Bitmap mBitmap;
    Canvas mCanvas;
    Paint mPaint = new Paint();
    String opType = "";
    String lineColor = "RED";
    float scalenum = 1;
    boolean stamp = false,bluring=false,coloring=false,bigpen = false;
    int oldX = -1, oldY = -1;

    public MyCanvas(Context context) {
        super(context);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
        // 메모리에 bitmap설정 및 캔버스와 bitmap연결
    }

    private void drawStamp(int x,int y){ // 이미지 찍기
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        Bitmap p_img = Bitmap.createScaledBitmap(img, (int)(img.getWidth()*scalenum),
                (int) (img.getHeight()*scalenum), false);
        mCanvas.drawBitmap(p_img,x-p_img.getWidth()/2,y-p_img.getHeight()/2,mPaint);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap != null)
            canvas.drawBitmap(mBitmap,0,0,null); //메모리에 있는 비트맵 view에 그리기
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        if(stamp){//스탬프가 체크되어있으면
            if(event.getAction() == MotionEvent.ACTION_UP){
                if(bluring) { // 흐릿한 효과
                    BlurMaskFilter blur = new BlurMaskFilter(60, BlurMaskFilter.Blur.INNER);
                    mPaint.setMaskFilter(blur);
                }
                if(coloring) { // 색깔넣기
                    float[] matrixarray = {
                            2f, 0f, 0f, 0f, -25f, //여기안에 있는 숫자들 변경해서 색상 변경
                            0f, 2f, 0f, 0f, -25f,
                            0f, 0f, 2f, 0f, -25f,
                            0f, 0f, 0f, 1f, 0f,
                    };

                    ColorMatrix colorMatrix = new ColorMatrix(matrixarray);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                    mPaint.setColorFilter(filter);
                }
                if(opType.equals("ROTATE")){
                    mCanvas.save(); // 지금 캔버스 환경 저장
                    mCanvas.rotate(30, x,y); //30도 회전
                    drawStamp(x,y);
                    mCanvas.restore(); // 저장한 캔버스 환경 복구
                    opType = "";
                }
                else if(opType.equals("MOVE")){
                    mCanvas.save();
                    mCanvas.translate(100,100); // 100,100 평행이동
                    drawStamp(x,y);
                    mCanvas.restore();
                    opType = "";
                }
                else if(opType.equals("SCALE")){
                    mCanvas.save();
                    scalenum = 1.5f; // 1.5배 확대
                    drawStamp(x,y);
                    scalenum = 1;
                    mCanvas.restore();
                    scalenum = 1;
                    opType = "";
                }
                else if(opType.equals("SKEW")){
                    mCanvas.save();
                    mCanvas.skew(0.2f,0); // x축 0.2기울이기
                    drawStamp( x - 250 * y / 1245,y); // 위치 안맞는거 보정
                    mCanvas.restore();
                    opType = "";
                }
                else{
                    drawStamp(x,y);
                }
                mPaint = new Paint();
            }
        }
        else{
            if(lineColor.equals("RED"))
                mPaint.setColor(Color.RED); // 펜색깔 지정
            else
                mPaint.setColor(Color.BLUE);
            if(bigpen)
                mPaint.setStrokeWidth(5); // 펜 넓이 지정
            else
                mPaint.setStrokeWidth(3);
            if(event.getAction() == MotionEvent.ACTION_DOWN){ //터치하면 x,y축위치 저장
                oldX = x; oldY = y;
            }
            else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if (oldX != -1) {
                    mCanvas.drawLine(oldX, oldY, x, y, mPaint); //움직인만큼 선그리기
                    invalidate(); // 변경된 내용 나오게 해줌 ( onDraw()함수를 호출)
                    oldX = x; // x,y축 저장
                    oldY = y;
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                if (oldX != -1) { // -1은 터치가 안되었다는 뜻
                    mCanvas.drawLine(oldX, oldY, x, y, mPaint); // 선그려주기
                    invalidate();
                }
            }
        }

        return true;
    }

    public void setOpType(String opType){
        this.opType = opType;
        Log.d("opType",opType);
    }
    public void setLineColor(String lineColor){
        this.lineColor = lineColor;
    }
    public void setStamp(boolean stamp){
        this.stamp = stamp;
    }
    public void setBig(boolean bigpen){
        this.bigpen = bigpen;
    }
    public void setBluring(boolean bluring){
        this.bluring = bluring;
    }
    public void setColoring(boolean coloring){
        this.coloring = coloring;
    }
    public Bitmap getBitmap(){
        return mBitmap;
    }

    public void openimg(Bitmap img) {
        float width = img.getWidth()/1.414f;
        float height = img.getHeight()/1.414f;
        Bitmap printImg = Bitmap.createScaledBitmap(img,
                (int) width, (int) height, false);
        mCanvas.drawBitmap(printImg,
                getWidth()/2 - printImg.getWidth()/2,
                getHeight()/2 - printImg.getHeight()/2, null);
        invalidate();
    }

    public void clear() {
        mBitmap.eraseColor(Color.WHITE);
        invalidate();
    }
}
