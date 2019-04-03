package com.android.mazhengyang.shudu;

import android.util.Log;

/**
 * Created by mazhengyang on 19-4-1.
 */

public class Sudoku {

    private static final String TAG = "Shudu." + Sudoku.class.getSimpleName();

    private int[][] matrix;
    private boolean mStop = false;
    private Listener mListener;
    private boolean mDone;
    private long startTime;

    public interface Listener {
        void showResult(int[][] matrix, long timeConsuming);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public void setStop(boolean stop) {
        Log.d(TAG, "setStop: stop=" + stop);
        mStop = stop;
    }

    public void setDone(boolean done) {
        this.mDone = done;
    }

    /**
     * 开始计算
     */
    public void start() {
        startTime = System.currentTimeMillis();
        backTrace(0, 0);
    }

    /**
     * 数独算法
     *
     * @param i 行号
     * @param j 列号
     */
    public void backTrace(int i, int j) {

        if (mStop || mDone) {
            Log.d(TAG, "backTrace: mStop=" + mStop + ", mDone=" + mDone);
            return;
        }

        // Log.d(TAG, "backTrace: i=" + i + ", j=" + j);
        if (i == 8 && j == 9) {
            //已经成功了，打印数组即可
            Log.d(TAG, "backTrace: success");
            printArray();
            if (mListener != null) {
                mListener.showResult(matrix, System.currentTimeMillis() - startTime);
            }
            mDone = true;
            return;
        }

        //已经到了列末尾了，还没到行尾，就换行
        if (j == 9) {
            i++;
            j = 0;
        }

        //如果i行j列是空格，那么才进入给空格填值的逻辑
        if (matrix[i][j] == 0) {
            for (int k = 1; k <= 9; k++) {
                //判断给i行j列放1-9中的任意一个数是否能满足规则
                if (check(i, j, k)) {
                    //将该值赋给该空格，然后进入下一个空格
                    matrix[i][j] = k;
                    backTrace(i, j + 1);
                    //初始化该空格
                    matrix[i][j] = 0;
                }
            }
        } else {
            //如果该位置已经有值了，就进入下一个空格进行计算
            backTrace(i, j + 1);
        }
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
        for (int i = 0; i < 9; i++) {
            if (matrix[row][i] == number || matrix[i][col] == number) {
                //   Log.d(TAG, "check: 该行该列有重复数字");
                return false;
            }
        }
        //判断小九宫格是否有重复
        int tempRow = row / 3;
        int tempLine = col / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[tempRow * 3 + i][tempLine * 3 + j] == number) {
                    //    Log.d(TAG, "check: 小九宫格有重复数字");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 打印矩阵
     */
    public void printArray() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                stringBuilder.append(String.valueOf(matrix[i][j]));
                // Log.d(TAG, "printArray: " + matrix[i][j]);
            }
            stringBuilder.append("\n");
            //Log.d(TAG, "printArray: \n");
        }

        Log.d(TAG, "printArray: \n" + stringBuilder.toString());
    }
}
