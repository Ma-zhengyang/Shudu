package com.android.mazhengyang.shudu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Sudoku.Listener, View.OnClickListener {

    private static final String TAG = "Shudu." + MainActivity.class.getSimpleName();

    private Sudoku sudoku = new Sudoku();

    @BindView(R.id.board)
    Board mBoard;
    @BindView(R.id.oneBtn)
    Button oneBtn;
    @BindView(R.id.twoBtn)
    Button twoBtn;
    @BindView(R.id.threeBtn)
    Button threeBtn;
    @BindView(R.id.fourBtn)
    Button fourBtn;
    @BindView(R.id.fiveBtn)
    Button fiveBtn;
    @BindView(R.id.sixBtn)
    Button sixBtn;
    @BindView(R.id.sevenBtn)
    Button sevenBtn;
    @BindView(R.id.eightBtn)
    Button eightBtn;
    @BindView(R.id.nineBtn)
    Button nineBtn;
    @BindView(R.id.cleanBtn)
    Button cleanBtn;
    @BindView(R.id.allCleanBtn)
    Button allCleanBtn;
    @BindView(R.id.calculateBtn)
    Button calculateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sudoku.setListener(MainActivity.this);

        oneBtn.setOnClickListener(this);
        twoBtn.setOnClickListener(this);
        threeBtn.setOnClickListener(this);
        fourBtn.setOnClickListener(this);
        fiveBtn.setOnClickListener(this);
        sixBtn.setOnClickListener(this);
        sevenBtn.setOnClickListener(this);
        eightBtn.setOnClickListener(this);
        nineBtn.setOnClickListener(this);
        cleanBtn.setOnClickListener(this);
        allCleanBtn.setOnClickListener(this);
        calculateBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        sudoku.setStop(false);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        sudoku.setStop(true);
    }

    /**
     * 显示结果
     *
     * @param matrix
     */
    @Override
    public void showResult(final int[][] matrix, long timeConsuming) {
        boolean success = true;
        if (matrix == null) {
            success = false;
            Log.d(TAG, "showResult: matrix is null");
        } else if (matrix.length != 9 || matrix[0].length != 9) {
            success = false;
            Log.d(TAG, "showResult: " + matrix.length + " " + matrix[0].length);
        }

        if (!success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    allCleanBtn.setEnabled(true);
                    Toast.makeText(MainActivity.this, R.string.data_error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mBoard.setResult(matrix);
            String time = String.valueOf(timeConsuming / 1000.0);
            if (time.indexOf('.') > 0) {
                time = time.replaceAll("0+?$", "");//去掉多余的0
                time = time.replaceAll("[.]$", "");//如最后一位是.则去掉
            }
            Log.d(TAG, "showResult: timeConsuming=" + timeConsuming + ", time=" + time);
            final String s = String.format(getString(R.string.calculate_done), time);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    allCleanBtn.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oneBtn:
                mBoard.setNum(1);
                break;
            case R.id.twoBtn:
                mBoard.setNum(2);
                break;
            case R.id.threeBtn:
                mBoard.setNum(3);
                break;
            case R.id.fourBtn:
                mBoard.setNum(4);
                break;
            case R.id.fiveBtn:
                mBoard.setNum(5);
                break;
            case R.id.sixBtn:
                mBoard.setNum(6);
                break;
            case R.id.sevenBtn:
                mBoard.setNum(7);
                break;
            case R.id.eightBtn:
                mBoard.setNum(8);
                break;
            case R.id.nineBtn:
                mBoard.setNum(9);
                break;
            case R.id.cleanBtn:
                mBoard.setNum(0);
                break;
            case R.id.allCleanBtn:
                sudoku.setDone(false);
                mBoard.cleanAll();
                oneBtn.setEnabled(true);
                twoBtn.setEnabled(true);
                threeBtn.setEnabled(true);
                fourBtn.setEnabled(true);
                fiveBtn.setEnabled(true);
                sixBtn.setEnabled(true);
                sevenBtn.setEnabled(true);
                eightBtn.setEnabled(true);
                nineBtn.setEnabled(true);
                cleanBtn.setEnabled(true);
                allCleanBtn.setEnabled(true);
                calculateBtn.setEnabled(true);
                break;
            case R.id.calculateBtn:
                STATE state = checkData();
                if (state == STATE.TOO_LESS_NUM) {
                    createDialog(R.string.too_less_num);
                } else {
                    createDialog(R.string.start);
                }
                return;
        }

        STATE state = checkData();
        if (state == STATE.NO_NUM) {
            calculateBtn.setEnabled(false);
            allCleanBtn.setEnabled(false);
        } else {
            calculateBtn.setEnabled(true);
            allCleanBtn.setEnabled(true);
        }
    }

    /**
     * 创建对话框
     *
     * @param resId
     */
    private void createDialog(final int resId) {
        Log.d(TAG, "createConfirmDialog: ");

        new AlertDialog.Builder(this)
                .setMessage(resId)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                start();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                            }
                        }).show();
    }

    /**
     * 检查数据是否合理
     */

    enum STATE {
        NO_NUM, TOO_LESS_NUM, START
    }

    private STATE checkData() {
        int[][] matrix = mBoard.getMatrixValue();

        int validNumCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (matrix[i][j] != 0) {
                    validNumCount++;
                }
            }
        }

        if (validNumCount == 0) {
            return STATE.NO_NUM;
        } else if (validNumCount < 10) {
            return STATE.TOO_LESS_NUM;
        } else {
            return STATE.START;
        }

    }

    /**
     * 开始计算
     */
    private void start() {

        oneBtn.setEnabled(false);
        twoBtn.setEnabled(false);
        threeBtn.setEnabled(false);
        fourBtn.setEnabled(false);
        fiveBtn.setEnabled(false);
        sixBtn.setEnabled(false);
        sevenBtn.setEnabled(false);
        eightBtn.setEnabled(false);
        nineBtn.setEnabled(false);
        cleanBtn.setEnabled(false);
        allCleanBtn.setEnabled(false);
        calculateBtn.setEnabled(false);
        mBoard.resetHighBox();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int[][] matrix = mBoard.getMatrixValue();

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        stringBuilder.append(String.valueOf(matrix[i][j]));
                    }
                    stringBuilder.append("\n");
                }
                Log.d(TAG, "createConfirmDialog: \n" + stringBuilder.toString());

                sudoku.setMatrix(matrix);
                sudoku.start();
            }
        });
        thread.start();
    }

}
