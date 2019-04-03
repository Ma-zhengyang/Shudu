package com.android.mazhengyang.shudu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhengyang on 19-4-1.
 */

public class Board extends View {

    private static final String TAG = "Shudu." + Board.class.getSimpleName();

    private Paint mPaint;
    private List<Box> boxs;
    private Box highBox;
    private int[][] matrix = new int[9][9];

    public Board(Context context) {
        super(context);
        Log.d(TAG, "Board: context");
        init(context);
    }

    public Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Board: context, attrs");
        init(context);
    }

    public Board(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "Board: context, attrs, defStyleAttr");
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(30 * density + 0.5f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        invalidate();
    }

    /**
     * 设置数字
     *
     * @param num
     */
    public void setNum(int num) {

        if (highBox != null) {
            int row = highBox.getRow();
            int col = highBox.getCol();
            if (num != 0) {
                if (!check(row, col, num)) {
                    return;
                }
            }
            highBox.setNum(String.valueOf(num));
            highBox.setBase(num == 0 ? false : true);
            matrix[row][col] = num;
            invalidate();
        }
    }

    public void resetHighBox() {
        highBox = null;
    }

    /**
     * 判断给某行某列赋值是否符合规则
     *
     * @param row    被赋值的行号
     * @param col    被赋值的列号
     * @param number 赋的值
     * @return
     */
    private boolean check(int row, int col, int number) {
        //判断该行该列是否有重复数字
        // Log.d(TAG, "check: row=" + row + ", col=" + col + ", number=" + number);
        Context context = getContext();
        for (int i = 0; i < 9; i++) {
            if (matrix[row][i] == number) {
                Toast.makeText(context, context.getString(R.string.row_duplicate), Toast.LENGTH_SHORT).show();
                return false;
            } else if (matrix[i][col] == number) {
                Toast.makeText(context, context.getString(R.string.col_duplicate), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //判断小九宫格是否有重复
        int tempRow = row / 3;
        int tempLine = col / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[tempRow * 3 + i][tempLine * 3 + j] == number) {
                    Toast.makeText(context, context.getString(R.string.box_duplicate), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 清空全部数据
     */
    public void cleanAll() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (boxs != null) {
                    Box box = boxs.get(i * 9 + j);
                    if (box != null) {
                        box.setBase(false);
                        box.setNum("0");
                        box.setHighlight(false);
                    }
                }
                matrix[i][j] = 0;
            }
        }
        invalidate();
    }

    /**
     * 返回数据矩阵
     *
     * @return
     */
    public int[][] getMatrixValue() {
        return matrix;
    }

    /**
     * 设置数据矩阵
     *
     * @param matrix
     */
    public void setResult(int[][] matrix) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                //         Log.d(TAG, "setResult: i=" + i + ", j=" + j + ", num=" + matrix[i][j]);
                this.matrix[i][j] = matrix[i][j];
                if (boxs != null) {
                    int index = i * 9 + j;
                    Box box = boxs.get(index);
                    box.setNum(String.valueOf(matrix[i][j]));
                }
            }
        }
        if (boxs != null) {
            invalidate();
        }
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        Log.d(TAG, "onDraw: width=" + width);
        Log.d(TAG, "onDraw: height=" + height);

        if (boxs == null) {
            initBox();
        }

        drawBackground(canvas);
        drawBoxLine(canvas);
        drawText(canvas);
    }

    /**
     * 绘制背景颜色
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        Log.d(TAG, "drawBackground: start");
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(80, 255, 255, 0));

        int width = getWidth();
        int w = width / 3;

        canvas.drawRect(w, 0, w * 2, w, mPaint);
        canvas.drawRect(0, w, w, w * 2, mPaint);
        canvas.drawRect(w * 2, w, w * 3, w * 2, mPaint);
        canvas.drawRect(w, w * 2, w * 2, w * 3, mPaint);
        Log.d(TAG, "drawBackground: end");
    }

    /**
     * 绘制格子线条
     *
     * @param canvas
     */
    private void drawBoxLine(Canvas canvas) {

        Log.d(TAG, "drawBoxLine: start");

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        int width = getWidth();
        int height = getHeight();
        float w = width / 9;

        //列
        float x = 0;
        for (int i = 0; i < 9; i++) {
            canvas.drawLine(x, 0, x, height, mPaint);
            x += w;
        }

        //行
        float y = 0;
        for (int j = 0; j < 9; j++) {
            canvas.drawLine(0, y, width, y, mPaint);
            y += w;
        }
        Log.d(TAG, "drawBoxLine: end");
    }

    /**
     * 初始化格子数据
     */
    private void initBox() {

        Log.d(TAG, "initBox: start " + matrix);

        boxs = new ArrayList<>();
        int width = getWidth();
        float w = width / 9;
        float left = 0;
        float top = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                RectF rectF = new RectF(left, top, left + w, top + w);
                Box box = new Box();
                box.setRow(i);
                box.setCol(j);
                box.setRectF(rectF);

                String num = String.valueOf(matrix[i][j]);
                //    Log.d(TAG, "initBox: i=" + i + ", j=" + j + ", num=" + num);
                box.setNum(num);

                left += w;
                boxs.add(box);
            }
            left = 0;
            top += w;
        }

        Log.d(TAG, "initBox: end");
    }

    /**
     * 绘制数据
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {

        Log.d(TAG, "drawText: start");

        for (int i = 0; i < boxs.size(); i++) {
            Box box = boxs.get(i);
            if ((box.isHighlight())) {
                mPaint.setStyle(Paint.Style.FILL);
            } else {
                mPaint.setStyle(Paint.Style.STROKE);
            }
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(box.getRectF(), mPaint);

            String num = box.getNum();
            if (num != null && !"0".equals(num)) {
                RectF rectF = box.getRectF();
                float textWidth = mPaint.measureText(num, 0, num.length());
                float x = rectF.centerX() - textWidth / 2;
                float y = rectF.centerY() + textWidth / 2;
                if (box.isBase) {
                    mPaint.setColor(Color.RED);
                } else {
                    mPaint.setColor(Color.GREEN);
                }
                canvas.drawText(num, x, y, mPaint);
            }
        }

        Log.d(TAG, "drawText: end");
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.d(TAG, "onMeasure: widthMeasureSpec=" + widthMeasureSpec);
        Log.d(TAG, "onMeasure: heightMeasureSpec" + heightMeasureSpec);

        int dw = 0;
//        int dh = 0;
        dw += (getPaddingLeft() + getPaddingRight());
//        dh += (getPaddingTop() + getPaddingBottom());
//
        final int measuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
//        final int measuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
//        setMeasuredDimension(measuredWidth, measuredHeight);

        setMeasuredDimension(measuredWidth, measuredWidth);
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "dispatchTouchEvent: ACTION_DOWN");
                Box box = getBox(x, y);
                highBox = box;
                box.setHighlight(true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "dispatchTouchEvent: ACTION_UP");
                break;
        }


        return super.dispatchTouchEvent(event);
    }

    /**
     * 返回格子
     *
     * @param x
     * @param y
     * @return
     */
    private Box getBox(float x, float y) {
        for (int i = 0; i < boxs.size(); i++) {
            boxs.get(i).setHighlight(false);
        }
        for (int i = 0; i < boxs.size(); i++) {
            Box box = boxs.get(i);
            if (box.getRectF().contains(x, y)) {
                Log.d(TAG, "getBox:(" + box.getRow() + "," + box.getCol() + ")");
                return box;
            }
        }
        return null;
    }

    /**
     * 格子类
     */
    class Box {

        private int row;//行
        private int col;//列
        private RectF rectF;//矩形类
        private boolean highlight;//是否是当前焦点格子
        private String num;//格子数字
        private boolean isBase;//是否是基数

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public RectF getRectF() {
            return rectF;
        }

        public void setRectF(RectF rectF) {
            this.rectF = rectF;
        }

        public boolean isHighlight() {
            return highlight;
        }

        public void setHighlight(boolean highlight) {
            this.highlight = highlight;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public boolean isBase() {
            return isBase;
        }

        public void setBase(boolean base) {
            isBase = base;
        }
    }

}
