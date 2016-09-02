package com.pierce.ges;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.pierce.Util;

import java.util.ArrayList;

/**
 * Created by izhaowo on 16/9/2.
 */
public class GestureRecognizer implements GestureListener{

    public GestureRecognizer(int touchSlop){
        this.touchSlopSquare = touchSlop * touchSlop;
    }

    public static GestureRecognizer get(Context context){
        return get(ViewConfiguration.get(context));
    }
    public static GestureRecognizer get(ViewConfiguration config){
        return new GestureRecognizer(config.getScaledTouchSlop());
    }

    /************************************/
    @Override
    public void onClick(float x, float y) {
        if(listeners == null){return;}
        for(GestureListener gl : listeners){
            gl.onClick(x + offsetX, y + offsetY);
        }
    }

    @Override
    public void onScroll(float sx, float sy, float ex, float ey) {
        if(listeners == null){return;}
        for(GestureListener gl : listeners){
//            gl.onScroll(sx, sy, ex, ey);
            gl.onScroll(sx + offsetX, sy + offsetY, ex + offsetX, ey + offsetY);
        }
    }

    @Override
    public void onRotate(double angle, float cx, float cy) {
        if(listeners == null){return;}
        for(GestureListener gl : listeners){
            gl.onRotate(angle, cx + offsetX, cy + offsetY);
        }
    }

    @Override
    public void onScale(double scale, float cx, float cy) {
        if(listeners == null){return;}
        for(GestureListener gl : listeners){
            gl.onScale(scale, cx + offsetX, cy + offsetY);
        }
    }
    /************************************/

    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        float[] _px = null, _py = null;
        logAction(actionMasked, event);
        switch (actionMasked){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:{
                int pointerCount = event.getPointerCount();
                pointerCount = Math.min(2, pointerCount);
                _px = new float[pointerCount];
                _py = new float[pointerCount];
                for(int i=0;i<pointerCount;i++){
                    _px[i] = event.getX(i);
                    _py[i] = event.getY(i);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                int id = event.getActionIndex();
                if(id > 1){
                    //抬起的是第三个手指
                    _px = px;
                    _py = py;
                    break;
                }

                if(event.getPointerCount() == 2){
                    //剩余1个手指
                    if(id == 0){
                        //抬起的是第一个手指
                        _px = new float[]{px[1]};
                        _py = new float[]{py[1]};
                    }else{
                        //抬起的是第二个手指
                        _px = new float[]{px[0]};
                        _py = new float[]{py[0]};
                    }
                }else if(id == 0){
                    _px = new float[]{px[1], event.getX(2)};
                    _py = new float[]{py[1], event.getY(2)};
                }else{
                    _px = new float[]{px[0], event.getX(2)};
                    _py = new float[]{py[0], event.getY(2)};
                }
                break;
            }
        }

        switch (actionMasked){
            case MotionEvent.ACTION_DOWN:{
                reset();
                //TODO [通知]单指操作
                //记录firstDownXY,firstDownTime
                firstDownX = _px[0];
                firstDownY = _py[0];
                firstDownTime = System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                //排除第三个手指
                if(singleFinger(px)){
                    //之前还是单指操作,现在是双指操作
                    //TODO [通知]停止单指操作
                    //判断是否在滚动
                    if(isScolling){
                        isScolling = false;
                        //TODO [通知]滑动停止
                    }
                    //双指操作开始
                    multiTouching = true;
                    //TODO [通知]双指操作
                }
                hasDoubleFinger = true;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if(singleFinger(_px)){
                    //现在还是单指操作
                    if(isScolling){
                        //正在滑动
                        onScroll(px[0], py[0], _px[0], _py[0]);
                    }else
                        //判断是否在滑动(比较与最初点的滑动距离)
                        if(touchSlopSquare < Util.distanceSquare(_px[0], _py[0], firstDownX, firstDownY)){
                            isScolling = true;
                            //TODO [通知]滑动开始

                            //正在滑动
                            onScroll(firstDownX, firstDownY, _px[0], _py[0]);
                        }
                }else if(doubleFinger(_px)){
                    //计算缩放/旋转
                    final float p1x = px[0], p1y = py[0];
                    final float p2x = px[1], p2y = py[1];
                    final float _p1x = _px[0], _p1y = _py[0];
                    final float _p2x = _px[1], _p2y = _py[1];

                    final double angle = Util.getAngle(p1x, p1y, p2x, p2y);
                    final double _angle = Util.getAngle(_p1x, _p1y, _p2x, _p2y);

                    final float d1 = Util.distanceSquare(p1x, p1y, p2x, p2y);
                    final float d2 = Util.distanceSquare(_p1x, _p1y, _p2x, _p2y);
                    final double scale = Math.sqrt(d2/d1);

                    final float centerX = (_p1x + _p2x) * .5f;
                    final float centerY = (_p1y + _p2y) * .5f;

                    onScale(scale, centerX, centerY);
                    onRotate(_angle - angle, centerX, centerY);
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                if(isScolling){
                    isScolling = false;
                    //TODO [通知]滑动停止
                }else if(!hasDoubleFinger){
                    //判断是否为单击
                    if(System.currentTimeMillis() - firstDownTime <= ViewConfiguration.getTapTimeout()){
                        // [通知]单击
                        onClick(px[0], py[0]);
                    }
                }
                //TODO [通知]停止单指操作
                //重置
                reset();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                int id = event.getActionIndex();
                if(id > 1){
                    break;
                }
                if(singleFinger(_px)){
                    //当前是单指操作
                    //停止缩放/旋转计算
                    multiTouching = false;
                    //将第一个点的位置更换成当前点的位置
                    firstDownX = _px[0];
                    firstDownY = _py[0];
                    //TODO [通知]停止双指操作
                    //TODO [通知]开始单指操作
                }else if(id == 0){
                    //将第一个点的位置更换成当前点的位置
                    firstDownX = _px[0];
                    firstDownY = _py[0];
                }
                break;
            }
        }

        px = _px;
        py = _py;

        return true;
    }

    void reset() {
        hasDoubleFinger = false;
        isScolling = false;
        multiTouching = false;
    }
    boolean noFinger(float[] px){
        return px==null || px.length==0;
    }
    boolean singleFinger(float[] px){
        return px!=null&&px.length==1;
    }
    boolean doubleFinger(float[] px){
        return px!=null&&px.length==2;
    }

    final int touchSlopSquare;

    /**
     *
     */
    float offsetX = 0, offsetY = 0;
    /**
     * 记录手势过程的点坐标
     */
    float[]px, py;
    /**
     * 第一个手指按下的位置
     */
    float firstDownX,firstDownY;
    /**
     * 第一个手指按下的时间
     */
    long firstDownTime;

    /**
     * 手势过程是否有多指参与
     */
    boolean hasDoubleFinger = false;
    /**
     * 是否处在滑动手势过程中
     */
    boolean isScolling = false;
    /**
     *
     */
    boolean multiTouching = false;

    ArrayList<GestureListener> listeners;

    public void addListener(GestureListener listener){
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeListener(GestureListener listener){
        if(listeners == null){return;}
        listeners.remove(listener);
    }

    /********************************************/
    private void logAction(int actionMasked, MotionEvent event) {
        switch (actionMasked){
            case MotionEvent.ACTION_DOWN:{
                log("---------start----------");
                log("手指1按下");
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                log("手指"+(event.getActionIndex()+1)+"按下");
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                log("手指移动");
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                log("手指"+(event.getActionIndex()+1)+"抬起");
                break;
            }
            case MotionEvent.ACTION_UP:{
                log("手指1抬起");
                log("---------end----------\n");
                break;
            }
        }
    }

    static final String tag = GestureRecognizer.class.getSimpleName();
    private void log(String str) {
        Log.d(tag, str);
    }


}
