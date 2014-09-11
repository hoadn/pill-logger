package com.echo.holographlibrary;

import android.graphics.Path;
import android.graphics.Region;
import android.util.FloatMath;
import android.util.Log;
import android.view.animation.AnimationUtils;

import java.util.Date;

/**
 * Created by alex on 30/11/2013.
 */
public class StackBarSection {
    /**
     * Used to compare floats, if the difference is smaller than this, they are
     * considered equal
     */
    private static final float TOLERANCE = 0.01f;

    private int _color;
    private int _strokeColour;
    private float _value;
    private float _targetValue;
    private String _valueString = null;

    private Path _path = null;
    private Region _region = null;
    private boolean _translucent;
    private long _lastTime;
    private float _springiness;
    private float _velocity;
    private float _damping;
    private int _pillId;

    public int getColor() {
        return _color;
    }
    public void setColor(int color) {
        this._color = color;
    }
    public int getStrokeColor(){return _strokeColour;}
    public void setStrokeColor(int strokeColor){this._strokeColour = strokeColor;}
    public float getValue() {
        return _value;
    }
    public void setTargetValue(float value) {
        this._targetValue = value;
        _lastTime = AnimationUtils.currentAnimationTimeMillis();
    }

    public String getValueString()
    {
        if (_valueString != null) {
            return _valueString;
        } else {
            return String.valueOf((int) _value);
        }
    }

    public void setValueString(final String valueString)
    {
        _valueString = valueString;
    }

    public Path getPath() {
        return _path;
    }
    public void setPath(Path path) {
        this._path = path;
    }
    public Region getRegion() {
        return _region;
    }
    public void setRegion(Region region) {
        this._region = region;
    }

    public boolean isTranslucent() {
        return _translucent;
    }

    public void setTranslucent(boolean translucent) {
        _translucent = translucent;
    }

    public StackBarSection(float springiness, float dampingRatio) {
        this._springiness = springiness;
        this._damping = dampingRatio * 2 * FloatMath.sqrt(springiness);
    }

    public void update(long now) {
        float dt = Math.min(now - _lastTime, 50) / 1000f;
        float x = _value - _targetValue;
        float acceleration = -_springiness * x - _damping * _velocity;
        _velocity += acceleration * dt;
        _value += _velocity * dt;
        _lastTime = now;
    }

    public boolean isAtRest() {
        final boolean standingStill = Math.abs(_velocity) < TOLERANCE;
        final boolean isAtTarget = (_targetValue - _value) < TOLERANCE;
        return (standingStill && isAtTarget);
    }

    public boolean isDrawable(){
        return getTargetValue() > TOLERANCE || getValue() > TOLERANCE;
    }

    public float getTargetValue() {
        return _targetValue;
    }

    public int getPillId() {
        return _pillId;
    }

    public void setPillId(int pillId) {
        _pillId = pillId;
    }
}
