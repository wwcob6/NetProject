package com.punuo.sys.sdk.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.punuo.sys.sdk.R;


/**
 * 1. shape 已支持，可以在xml直接用。
 * 2. html
 * 3. ImageSpan
 * 4. shadow 已支持，xml直接用，且xoffset、yoffset、blur、spread、color缺一不可，
 * 宽高必须是exactly，api18以上
 */
public class AdvancedTextView extends AppCompatTextView {

    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private boolean mShadowEnable;
    private ShadowBitmapDrawable mShadowBitmapDrawable;

    public AdvancedTextView(Context context) {
        this(context, null);
    }

    public AdvancedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int[] attrArray = new int[]{
                android.R.attr.layout_width,
                android.R.attr.layout_height
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrArray);
        int layout_width = 0;
        int layout_height = 0;
        try {
            layout_width = ta.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_height = ta.getDimensionPixelSize(1, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {

        } finally {
            ta.recycle();
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdvancedTextView);
        Drawable normal = generateDrawable(a);
        Drawable disable = generateDisableDrawable(a);
        Drawable selected = generateSelectedDrawable(a);
        mShadowEnable = addShadowToView(a, layout_width, layout_height);
        setFontWeight(a);
        setTextShadow(a);
        a.recycle();

        StateListDrawable stateDrawable = null;
        if (disable != null || selected != null) {
            stateDrawable = new StateListDrawable();
            if (disable != null) {
                stateDrawable.addState(new int[]{-android.R.attr.state_enabled}, disable);
            }
            if (selected != null) {
                stateDrawable.addState(new int[]{android.R.attr.state_selected}, selected);
            }
        }

        Drawable bgDrawable = null;
        if (normal != null) {
            if (stateDrawable != null) {
                stateDrawable.addState(new int[]{}, normal);
                bgDrawable = stateDrawable;
            } else {
                bgDrawable = normal;
            }
        } else {
            if (stateDrawable != null) {
                Drawable main = getBackground() != null ? getBackground() : new ColorDrawable(Color.TRANSPARENT);
                stateDrawable.addState(new int[]{}, main);
            } else {
                bgDrawable = null;
            }
        }

        if (bgDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(bgDrawable);
            } else {
                setBackgroundDrawable(bgDrawable);
            }
        }

        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null && drawables.length > 0 && drawables[0] != null) {
            mDrawableLeft = drawables[0];
        }
        if (drawables != null && drawables.length > 2 && drawables[2] != null) {
            mDrawableRight = drawables[2];
        }
    }

    private void setFontWeight(TypedArray a) {
        int weight = a.getInt(R.styleable.AdvancedTextView_at_font_weight, 0);
        int boldType = a.getInt(R.styleable.AdvancedTextView_at_bold_type, 0);
        if (weight > 0) {
            TextPaint textPaint = getPaint();
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setStrokeWidth(weight / 1000F);
        } else if (boldType > 0) {
            TextPaint textPaint = getPaint();
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setStrokeWidth(boldType / 1000F);
        }
    }

    private void setTextShadow(TypedArray a) {
        float radius = a.getFloat(R.styleable.AdvancedTextView_at_shadow_layer_radius, 0);
        float dx = a.getFloat(R.styleable.AdvancedTextView_at_shadow_layer_dx, 0);
        float dy = a.getFloat(R.styleable.AdvancedTextView_at_shadow_layer_dy, 0);
        int color = a.getColor(R.styleable.AdvancedTextView_at_shadow_layer_color, 0);
        if (radius != 0 || dx != 0 || dy != 0 || color != 0) {
            TextPaint textPaint = getPaint();
            textPaint.setShadowLayer(radius, dx, dy, color);
        }
    }

    private GradientDrawable generateDrawable(AdvancedParams params) {
        if (params.mBorderWidth == 0 && params.mBorderRadius == 0
                && params.mBorderRadiusBottomLeft == 0 && params.mBorderRadiusBottomRight == 0
                && params.mBorderRadiusTopLeft == 0 && params.mBorderRadiusTopRight == 0
                && params.mGradientDirection == 0) {
            return null;
        }
        GradientDrawable drawable = new GradientDrawable();
        if (params.mBorderWidth > 0) {
            drawable.setStroke(params.mBorderWidth, params.mBorderColor);
        }
        if (0 != params.mBorderRadius) {
            drawable.setCornerRadius(params.mBorderRadius);
        } else if(params.mBorderRadiusTopLeft != 0
                || params.mBorderRadiusTopRight != 0
                || params.mBorderRadiusBottomLeft != 0
                || params.mBorderRadiusBottomRight != 0 ) {
            drawable.setCornerRadii(new float[]{
                    params.mBorderRadiusTopLeft, params.mBorderRadiusTopLeft,
                    params.mBorderRadiusTopRight, params.mBorderRadiusTopRight,
                    params.mBorderRadiusBottomRight, params.mBorderRadiusBottomRight,
                    params.mBorderRadiusBottomLeft, params.mBorderRadiusBottomLeft
            });
        }

        if (params.getOrientation() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                drawable.setOrientation(params.getOrientation());
                drawable.setColors(new int[]{params.mGradientStartColor, params.mGradientEndColor});
            } else {
                drawable.setColor(params.mGradientStartColor);
            }
        } else {
            drawable.setColor(Color.TRANSPARENT);
        }
        return drawable;
    }

    private Drawable generateDrawable(TypedArray a) {
        AdvancedParams params = AdvancedParams.newParams(a);
        GradientDrawable drawable = generateDrawable(params);
        if (drawable != null) {
            if (getBackground() instanceof ColorDrawable) {
                drawable.setColor(((ColorDrawable) getBackground()).getColor());
            }
        }
        return drawable;
    }

    private Drawable generateDisableDrawable(TypedArray a) {
        AdvancedParams params = AdvancedParams.newDisableParams(a);
        GradientDrawable drawable = generateDrawable(params);
        if (drawable != null) {
            if (params.mDisableColor != 1) {
                drawable.setColor(params.mDisableColor);
            }
            return drawable;
        } else {
            if (params.mDisableColor != 1) {
                return new ColorDrawable(params.mDisableColor);
            } else {
                return null;
            }
        }
    }

    private Drawable generateSelectedDrawable(TypedArray a) {
        AdvancedParams params = AdvancedParams.newSelectedParams(a);
        GradientDrawable drawable = generateDrawable(params);
        if (drawable != null) {
            if (params.mSelectedColor != 1) {
                drawable.setColor(params.mSelectedColor);
            }
            return drawable;
        } else {
            if (params.mSelectedColor != 1) {
                return new ColorDrawable(params.mSelectedColor);
            } else {
                return null;
            }
        }
    }

    private boolean addShadowToView(TypedArray a, int viewWidth, int viewHeight) {
        AdvancedParams params = AdvancedParams.newShadowParams(a);
        if (!params.mShadowEnable) {
            return false;
        }
        if (viewWidth <= 0 || viewHeight <= 0) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.getOverlay().clear();
            int blur = params.mShadowBlur;
            int spread = params.mShadowSpread;
            int xOffset = params.mShadowXoffset;
            int yOffset = params.mShadowYoffset;
            int shadowColor = params.mShadowColor;
            int radius = params.mShadowRadius;

            int xIncrement = blur + spread + Math.abs(xOffset);
            int yIncrement = blur + spread + Math.abs(yOffset);
            int maxWidth = viewWidth + 2 * xIncrement;
            int maxHeight = viewHeight + 2 * yIncrement;
            Bitmap output = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(output);
            RectF shadowRect = new RectF(
                    0f, 0f,
                    viewWidth + 2f * spread, viewHeight + 2f * spread
            );

            float shadowDx = blur + 2f * xOffset;
            float shadowDy = blur + 2f * yOffset;

            shadowRect.offset(shadowDx, shadowDy);

            Paint shadowPaint = new Paint();
            shadowPaint.setAntiAlias(true);
            shadowPaint.setColor(shadowColor);
            shadowPaint.setStyle(Paint.Style.FILL);

            if (blur > 0) {
                shadowPaint.setMaskFilter(new BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL));
            }

            Path shadowPath = new Path();
            float[] shadowRadii = new float[8];
            float[] radii = new float[8];
            for (int i = 0; i < shadowRadii.length; i++) {
                shadowRadii[i] = radius == 0f ? 0f : radius + spread;
                radii[i] = radius;
            }
            shadowPath.addRoundRect(shadowRect, shadowRadii, Path.Direction.CCW);
            canvas.drawPath(shadowPath, shadowPaint);
            mShadowBitmapDrawable = new ShadowBitmapDrawable(getResources(), output,
                    new Point(xIncrement, yIncrement), new Rect(0, 0, viewWidth, viewHeight), radii);

            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).setClipChildren(false);
                ((ViewGroup) parent).setClipToPadding(false);
            }

            this.getOverlay().add(mShadowBitmapDrawable);

            //Relayout to ensure the shadows are fully drawn
            if (parent != null) {
                parent.requestLayout();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).invalidate(mShadowBitmapDrawable.getBounds());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) getBackground()).setColor(color);
        } else {
            super.setBackgroundColor(color);
        }
    }

    public void setBackgroundColors(int[] colors) {
        if (getBackground() instanceof GradientDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ((GradientDrawable) getBackground()).setColors(colors);
            } else {
                ((GradientDrawable) getBackground()).setColor(colors[0]);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawableLeft != null || mDrawableRight != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int totalWidth = getWidth();
            int drawablePadding = getCompoundDrawablePadding();
            float bodyWidth = textWidth;
            if (mDrawableLeft != null) {
                bodyWidth += drawablePadding;
                bodyWidth += mDrawableLeft.getIntrinsicWidth();
            }
            if (mDrawableRight != null) {
                bodyWidth += drawablePadding;
                bodyWidth += mDrawableRight.getIntrinsicWidth();
            }
            if (((int) (totalWidth - bodyWidth)) != getPaddingRight()) {
                setPadding(0, 0, (int) (totalWidth - bodyWidth), 0);
            }
            canvas.translate((totalWidth - bodyWidth) / 2, 0);
            if (mShadowBitmapDrawable != null) {
                // 画布被平移后，阴影也会随着平移，所以这里阴影要抵消这个平移。
                mShadowBitmapDrawable.translate(-(totalWidth - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mShadowEnable) {
            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).setClipChildren(false);
                ((ViewGroup) parent).setClipToPadding(false);
                ((ViewGroup) parent).invalidate();
            }
        }
    }

    static class AdvancedParams {
        int mBorderWidth;
        int mBorderRadius;
        int mBorderRadiusTopLeft;
        int mBorderRadiusTopRight;
        int mBorderRadiusBottomLeft;
        int mBorderRadiusBottomRight;
        int mBorderColor;
        int mGradientDirection;
        int mGradientStartColor;
        int mGradientEndColor;

        int mDisableColor;
        int mSelectedColor;

        boolean mShadowEnable;
        int mShadowXoffset;
        int mShadowYoffset;
        int mShadowBlur;
        int mShadowSpread;
        int mShadowColor;
        int mShadowRadius;

        static AdvancedParams newParams(TypedArray a) {
            AdvancedParams params = new AdvancedParams();
            params.mBorderWidth = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_width, 0);
            params.mBorderRadius = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_radius, 0);
            params.mBorderColor = a.getColor(R.styleable.AdvancedTextView_border_color, 0xffffffff);
            params.mGradientDirection = a.getInt(R.styleable.AdvancedTextView_gradient_direction, 0);
            params.mGradientStartColor = a.getColor(R.styleable.AdvancedTextView_gradient_startColor, 0xffffffff);
            params.mGradientEndColor = a.getColor(R.styleable.AdvancedTextView_gradient_endColor, 0xffffffff);
            setRadii(params, a);
            return params;
        }

        static AdvancedParams newDisableParams(TypedArray a) {
            AdvancedParams params = new AdvancedParams();
            params.mBorderWidth = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_width, 0);
            params.mBorderRadius = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_radius, 0);
            params.mBorderColor = a.getColor(R.styleable.AdvancedTextView_disable_border_color, 0xffffffff);
            params.mGradientDirection = a.getInt(R.styleable.AdvancedTextView_disable_gradient_direction, 0);
            params.mGradientStartColor = a.getColor(R.styleable.AdvancedTextView_disable_gradient_startColor, 0xffffffff);
            params.mGradientEndColor = a.getColor(R.styleable.AdvancedTextView_disable_gradient_endColor, 0xffffffff);
            params.mDisableColor = a.getColor(R.styleable.AdvancedTextView_disable_color, 1);
            params.mBorderRadiusTopLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_radius_top_left, 0);
            params.mBorderRadiusTopRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_radius_top_right, 0);
            params.mBorderRadiusBottomLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_radius_bottom_left, 0);
            params.mBorderRadiusBottomRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_disable_border_radius_bottom_right, 0);
            return params;
        }

        static AdvancedParams newSelectedParams(TypedArray a) {
            AdvancedParams params = new AdvancedParams();
            params.mBorderWidth = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_width, 0);
            params.mBorderRadius = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_radius, 0);
            params.mBorderColor = a.getColor(R.styleable.AdvancedTextView_selected_border_color, 0xffffffff);
            params.mGradientDirection = a.getInt(R.styleable.AdvancedTextView_selected_gradient_direction, 0);
            params.mGradientStartColor = a.getColor(R.styleable.AdvancedTextView_selected_gradient_startColor, 0xffffffff);
            params.mGradientEndColor = a.getColor(R.styleable.AdvancedTextView_selected_gradient_endColor, 0xffffffff);
            params.mSelectedColor = a.getColor(R.styleable.AdvancedTextView_selected_color, 1);
            params.mBorderRadiusTopLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_radius_top_left, 0);
            params.mBorderRadiusTopRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_radius_top_right, 0);
            params.mBorderRadiusBottomLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_radius_bottom_left, 0);
            params.mBorderRadiusBottomRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_selected_border_radius_bottom_right, 0);
            return params;
        }

        static void setRadii(AdvancedParams params, TypedArray a) {
            params.mBorderRadiusTopLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_radius_top_left, 0);
            params.mBorderRadiusTopRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_radius_top_right, 0);
            params.mBorderRadiusBottomLeft = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_radius_bottom_left, 0);
            params.mBorderRadiusBottomRight = a.getDimensionPixelSize(R.styleable.AdvancedTextView_border_radius_bottom_right, 0);
        }

        static AdvancedParams newShadowParams(TypedArray a) {
            AdvancedParams params = new AdvancedParams();
            params.mShadowEnable = a.hasValue(R.styleable.AdvancedTextView_shadow_xoffset)
                    && a.hasValue(R.styleable.AdvancedTextView_shadow_yoffset)
                    && a.hasValue(R.styleable.AdvancedTextView_shadow_blur)
                    && a.hasValue(R.styleable.AdvancedTextView_shadow_spread)
                    && a.hasValue(R.styleable.AdvancedTextView_shadow_color);
            params.mShadowXoffset = a.getDimensionPixelSize(R.styleable.AdvancedTextView_shadow_xoffset, 0);
            params.mShadowYoffset = a.getDimensionPixelSize(R.styleable.AdvancedTextView_shadow_yoffset, 0);
            params.mShadowBlur = a.getDimensionPixelSize(R.styleable.AdvancedTextView_shadow_blur, 0);
            params.mShadowSpread = a.getDimensionPixelSize(R.styleable.AdvancedTextView_shadow_spread, 0);
            params.mShadowColor = a.getColor(R.styleable.AdvancedTextView_shadow_color, 0);
            params.mShadowRadius = a.getDimensionPixelSize(R.styleable.AdvancedTextView_shadow_radius, 0);
            return params;
        }

        public GradientDrawable.Orientation getOrientation() {
            switch (mGradientDirection) {
                case 1:
                    return GradientDrawable.Orientation.LEFT_RIGHT;
                case 2:
                    return GradientDrawable.Orientation.RIGHT_LEFT;
                case 3:
                    return GradientDrawable.Orientation.TOP_BOTTOM;
                case 4:
                    return GradientDrawable.Orientation.BOTTOM_TOP;
                case 5:
                    return GradientDrawable.Orientation.TL_BR;
                case 6:
                    return GradientDrawable.Orientation.BR_TL;
                case 7:
                    return GradientDrawable.Orientation.TR_BL;
                case 8:
                    return GradientDrawable.Orientation.BL_TR;
                default:
                    return null;
            }
        }
    }

    private static class ShadowBitmapDrawable extends BitmapDrawable {
        private int paddingX;
        private int paddingY;
        private Path mContentPath;
        private float mDx;
        private float mDy;

        private ShadowBitmapDrawable(Resources resources, Bitmap bitmap, Point topLeft, Rect viewRect, float[] radii) {
            super(resources, bitmap);
            this.paddingX = topLeft.x;
            this.paddingY = topLeft.y;
            mContentPath = new Path();
            RectF rectF = new RectF(0f, 0f, viewRect.width(), viewRect.height());
            mContentPath.addRoundRect(rectF, radii, Path.Direction.CCW);
            setBounds(-paddingX, -paddingY, viewRect.width() + paddingX, viewRect.height() + paddingY);
        }

        public void translate(float dx, float dy) {
            mDx = dx;
            mDy = dy;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(mDx, mDy);
            Rect newRect = canvas.getClipBounds();
            newRect.inset(-paddingX, -paddingY);
            canvas.clipRect(newRect, Region.Op.INTERSECT);//使用INTERSECT, 配合ClipChildren, 只能在父容器里绘制，在listview中不会抖动。
//            canvas.clipRect(newRect, Region.Op.REPLACE);//使用REPLACE可以无限制超出当前canvas，但是在listview中会抖动。
            canvas.clipPath(mContentPath, Region.Op.DIFFERENCE);
            super.draw(canvas);
            canvas.restore();
        }
    }
}
