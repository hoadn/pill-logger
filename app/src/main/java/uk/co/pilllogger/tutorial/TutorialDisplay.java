package uk.co.pilllogger.tutorial;

import android.content.Context;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.LayoutHelper;

/**
 * Created by alex on 28/01/2014.
 */
public class TutorialDisplay {
    private final View _view;
    private final View _container;
    private final Context _context;
    private final int _actionBarHeight;
    private final int _leftMargin;
    private final int _topMargin;
    private String _text = "";
    private int _textLeft = 0;
    private int _textTop = 0;
    private int _arrowLeft = 0;
    private int _arrowTop = 0;
    private VerticalPosition _verticalTextPosition = VerticalPosition.Custom;
    private HorizontalPosition _horizontalTextPosition = HorizontalPosition.Custom;
    private HorizontalPosition _horizontalArrowPosition = HorizontalPosition.Custom;
    private ArrowDirection _arrowDirection = ArrowDirection.None;

    final int _modifier;

    public TutorialDisplay(View view, View container, Context context) {
        _view = view;
        _container = container;
        _context = context;

        _actionBarHeight = (int)context.getResources().getDimension(R.dimen.action_bar_height);
        _modifier = (int) LayoutHelper.dpToPx(_context, 10);

        _leftMargin = _context.getResources().getDimensionPixelSize(R.dimen.tutorial_text_left_margin);
        _topMargin = _context.getResources().getDimensionPixelSize(R.dimen.tutorial_text_top_margin);
    }
    public TutorialDisplay(View view, View container, Context context, int textResourceId){
        this(view, container, context);
        _text = context.getString(textResourceId);
    }

    public int getArrowTop() {
        return _arrowTop;
    }

    public void setArrowTop(int arrowTop) {
        _arrowTop = arrowTop;
    }

    public ArrowDirection getArrowDirection() {
        return _arrowDirection;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        _arrowDirection = arrowDirection;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    public void setText(Context context, int resId){
        _text = context.getString(resId);
    }

    public VerticalPosition getVerticalTextPosition() {
        return _verticalTextPosition;
    }

    public void setVerticalTextPosition(VerticalPosition verticalTextPosition) {
        _verticalTextPosition = verticalTextPosition;
    }

    public HorizontalPosition getHorizontalTextPosition() {
        return _horizontalTextPosition;
    }

    public void setHorizontalTextPosition(HorizontalPosition horizontalTextPosition) {
        _horizontalTextPosition = horizontalTextPosition;
    }

    public HorizontalPosition getHorizontalArrowPosition() {
        return _horizontalArrowPosition;
    }

    public void setHorizontalArrowPosition(HorizontalPosition horizontalArrowPosition) {
        _horizontalArrowPosition = horizontalArrowPosition;
    }

    public int getTextLeft() {
        return _textLeft;
    }

    public void setTextLeft(int textLeft) {
        _textLeft = textLeft;
    }

    public int getTextTop() {
        return _textTop;
    }

    public void setTextTop(int textTop) {
        _textTop = textTop;
    }

    public int getArrowLeft() {
        return _arrowLeft;
    }

    public void setArrowLeft(int arrowLeft) {
        _arrowLeft = arrowLeft;
    }

    public int getArrowRotation(){
        int rotation = 0;
        switch(_arrowDirection){
            case Up:
                rotation = 0;
                break;

            case Right:
                rotation = 90;
                break;

            case Down:
                rotation = 180;
                break;

            case Left:
                rotation = 270;
                break;
        }

        return rotation;
    }

    public int getTextTopPosition(){
        int layoutHeight = _container.getHeight() - _actionBarHeight;

        int position = _actionBarHeight;

        switch(getVerticalTextPosition()){
            case Custom:
                position += getTextTop();
                break;

            case Top:
                break;

            case Middle:
                position = layoutHeight / 2 - (_view.getMeasuredHeight() / 2);
                break;

            case Bottom:
                position = layoutHeight - _view.getTop() - _view.getMeasuredHeight() - _modifier;
                break;
        }

        return position;
    }

    public int getTextLeftPosition(){
        switch(getHorizontalTextPosition()){

            case Custom:
                return getTextLeft();

            case Left:
                return 0;

            case Middle:
                return _container.getWidth() / 2 - (_view.getMeasuredWidth() / 2) - (_leftMargin / 2);

            case Right:
                return _container.getWidth();
        }

        return 0;
    }

    public int getArrowLeftPosition(){
        switch(getHorizontalArrowPosition()){

            case Custom:
                return getArrowLeft();

            case Left:
                return 0;

            case Middle:
                return (int)LayoutHelper.dpToPx(_context, _view.getLeft()) + (_view.getMeasuredWidth() / 2) + (int)(_leftMargin * 1.5f);

            case Right:
                return _container.getWidth();
        }

        return 0;
    }

    public int getArrowTopPosition(){
        switch (getArrowDirection()) {
            case Up:
                return getTextTopPosition() - _view.getMeasuredHeight();

            case Right:
                break;

            case Down:
                return getTextTopPosition() + _view.getMeasuredHeight();

            case Left:
                break;
        }

        return 0;
    }

    public TutorialDisplay createCopy(){
        TutorialDisplay copy = new TutorialDisplay(_view, _container, _context);

        copy.setArrowDirection(this.getArrowDirection());
        copy.setArrowLeft(this.getArrowLeft());
        copy.setArrowTop(this.getArrowTop());
        copy.setHorizontalArrowPosition(this.getHorizontalArrowPosition());
        copy.setHorizontalTextPosition(this.getHorizontalTextPosition());
        copy.setText(this.getText());
        copy.setTextLeft(this.getTextLeft());
        copy.setTextTop(this.getTextTop());
        copy.setVerticalTextPosition(this.getVerticalTextPosition());

        return copy;
    }

    public enum VerticalPosition {
        Custom,
        Top,
        Middle,
        Bottom
    }

    public enum HorizontalPosition{
        Custom,
        Left,
        Middle,
        Right
    }

    public enum ArrowDirection{
        Up,
        Right,
        Down,
        Left,
        None
    }
}


