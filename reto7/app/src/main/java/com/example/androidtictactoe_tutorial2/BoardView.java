package com.example.androidtictactoe_tutorial2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;

public class BoardView extends View {
    // Width of the board grid lines
    public static final int GRID_WIDTH = 6;
    
    private Drawable mHumanDrawable;
    private Drawable mComputerDrawable;
    private Paint mPaint;
    
    // Game board state
    private String[][] mBoard = new String[3][3];
    
    // Click listener interface
    public interface OnCellClickListener {
        void onCellClick(int row, int col);
    }
    
    private OnCellClickListener mClickListener;
    
    public BoardView(Context context) {
        super(context);
        initialize();
    }
    
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }
    
    public void initialize() {
        mHumanDrawable = ContextCompat.getDrawable(getContext(), R.drawable.x_img);
        mComputerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.o_img);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        // Initialize empty board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mBoard[i][j] = "";
            }
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Get the width and height of the board
        int boardWidth = getWidth();
        int boardHeight = getHeight();
        
        // Set paint properties for drawing lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);
        
        // Draw two vertical lines
        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);
        
        // Draw two horizontal lines
        int cellHeight = boardHeight / 3;
        canvas.drawLine(0, cellHeight, boardWidth, cellHeight, mPaint);
        canvas.drawLine(0, cellHeight * 2, boardWidth, cellHeight * 2, mPaint);
        
        // Draw X's and O's
        drawSymbols(canvas, cellWidth, cellHeight);
    }
    
    private void drawSymbols(Canvas canvas, int cellWidth, int cellHeight) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!mBoard[i][j].equals("")) {
                    // Calculate the position and size for the symbol
                    int left = j * cellWidth + cellWidth / 4;
                    int top = i * cellHeight + cellHeight / 4;
                    int right = left + cellWidth / 2;
                    int bottom = top + cellHeight / 2;
                    
                    if (mBoard[i][j].equals("X") && mHumanDrawable != null) {
                        mHumanDrawable.setBounds(left, top, right, bottom);
                        mHumanDrawable.draw(canvas);
                    } else if (mBoard[i][j].equals("O") && mComputerDrawable != null) {
                        mComputerDrawable.setBounds(left, top, right, bottom);
                        mComputerDrawable.draw(canvas);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            
            int cellWidth = getWidth() / 3;
            int cellHeight = getHeight() / 3;
            
            int row = y / cellHeight;
            int col = x / cellWidth;
            
            // Make sure we're within bounds
            if (row >= 0 && row < 3 && col >= 0 && col < 3) {
                // Only handle click if cell is empty
                if (mBoard[row][col].equals("") && mClickListener != null) {
                    mClickListener.onCellClick(row, col);
                }
            }
        }
        return true;
    }
    
    public void setOnCellClickListener(OnCellClickListener listener) {
        mClickListener = listener;
    }
    
    public void setCellValue(int row, int col, String value) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            mBoard[row][col] = value;
            invalidate(); // Redraw the view
        }
    }
    
    public String getCellValue(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            return mBoard[row][col];
        }
        return "";
    }
    
    public void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mBoard[i][j] = "";
            }
        }
        invalidate(); // Redraw the view
    }

    public char[] getBoardState() {
        char[] boardState = new char[9];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String cellValue = mBoard[i][j];
                if (cellValue.equals("")) {
                    boardState[index] = ' ';
                } else {
                    boardState[index] = cellValue.charAt(0);
                }
                index++;
            }
        }
        return boardState;
    }

    public void setBoardState(char[] boardState) {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char cellValue = boardState[index];
                if (cellValue == ' ') {
                    mBoard[i][j] = "";
                } else {
                    mBoard[i][j] = String.valueOf(cellValue);
                }
                index++;
            }
        }
        invalidate(); // Redraw the view
    }
}
