package in.innovateria.ultimate_scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

import java.util.Objects;

import in.innovateria.ultimate_scanner.R;

public final class CodeScannerView extends ViewGroup {

    private static final boolean DEFAULT_AUTO_FOCUS_BUTTON_VISIBLE = true;
    private static final boolean DEFAULT_FLASH_BUTTON_VISIBLE = true;
    private static final int DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY = VISIBLE;
    private static final int DEFAULT_FLASH_BUTTON_VISIBILITY = VISIBLE;
    private static final int DEFAULT_MASK_COLOR = 0x77000000;
    private static final int DEFAULT_FRAME_COLOR = Color.WHITE;
    private static final int DEFAULT_AUTO_FOCUS_BUTTON_COLOR = Color.WHITE;
    private static final int DEFAULT_FLASH_BUTTON_COLOR = Color.WHITE;
    private static final int HINT_VIEW_INDEX = 4;
    private static final int MAX_CHILD_COUNT = 5;
    private static final float DEFAULT_FRAME_THICKNESS_DP = 2f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f;
    private static final float DEFAULT_FRAME_CORNER_SIZE_DP = 50f;
    private static final float DEFAULT_FRAME_CORNERS_RADIUS_DP = 0f;
    private static final float DEFAULT_FRAME_SIZE = 0.75f;
    private static final float DEFAULT_FRAME_VERTICAL_BIAS = 0.5f;
    private static final float DEFAULT_BUTTON_PADDING_DP = 16f;
    private static final float FOCUS_AREA_SIZE_DP = 20f;
    private static final ButtonPosition DEFAULT_AUTO_FOCUS_BUTTON_POSITION =
            ButtonPosition.TOP_START;
    private static final ButtonPosition DEFAULT_FLASH_BUTTON_POSITION = ButtonPosition.TOP_END;
    private SurfaceView mPreviewView;
    private ViewFinder mViewFinder;
    private ImageView mAutoFocusButton;
    private ButtonPosition mAutoFocusButtonPosition;
    private int mAutoFocusButtonPaddingHorizontal;
    private int mAutoFocusButtonPaddingVertical;
    private int mAutoFocusButtonColor;
    private Drawable mAutoFocusButtonOnIcon;
    private Drawable mAutoFocusButtonOffIcon;
    private ImageView mFlashButton;
    private ButtonPosition mFlashButtonPosition;
    private int mFlashButtonPaddingHorizontal;
    private int mFlashButtonPaddingVertical;
    private int mFlashButtonColor;
    private Drawable mFlashButtonOnIcon;
    private Drawable mFlashButtonOffIcon;
    private Point mPreviewSize;
    private SizeListener mSizeListener;
    private CodeScanner mCodeScanner;
    private int mFocusAreaSize;

    /**
     * A view to display code scanner preview
     *
     * @see CodeScanner
     */
    public CodeScannerView(@NonNull final Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    /**
     * A view to display code scanner preview
     *
     * @see CodeScanner
     */
    public CodeScannerView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    /**
     * A view to display code scanner preview
     *
     * @see CodeScanner
     */
    public CodeScannerView(@NonNull final Context context, @Nullable final AttributeSet attrs,
                           @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    /**
     * A view to display code scanner preview
     *
     * @see CodeScanner
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CodeScannerView(final Context context, final AttributeSet attrs,
                           @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(@NonNull final Context context, @Nullable final AttributeSet attrs,
                            @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        mPreviewView = new SurfaceView(context);
        mViewFinder = new ViewFinder(context);
        final float density = context.getResources().getDisplayMetrics().density;
        final int defaultButtonPadding = Math.round(density * DEFAULT_BUTTON_PADDING_DP);
        mFocusAreaSize = Math.round(density * FOCUS_AREA_SIZE_DP);
        mAutoFocusButton = new ImageView(context);
        mAutoFocusButton.setScaleType(ImageView.ScaleType.CENTER);
        mAutoFocusButton.setOnClickListener(new AutoFocusClickListener());
        mFlashButton = new ImageView(context);
        mFlashButton.setScaleType(ImageView.ScaleType.CENTER);
        mFlashButton.setOnClickListener(new FlashClickListener());
        if (attrs == null) {
            mViewFinder.setFrameAspectRatio(DEFAULT_FRAME_ASPECT_RATIO_WIDTH,
                    DEFAULT_FRAME_ASPECT_RATIO_HEIGHT);
            mViewFinder.setMaskColor(DEFAULT_MASK_COLOR);
            mViewFinder.setFrameColor(DEFAULT_FRAME_COLOR);
            mViewFinder.setFrameThickness(Math.round(DEFAULT_FRAME_THICKNESS_DP * density));
            mViewFinder.setFrameCornersSize(Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density));
            mViewFinder.setFrameCornersRadius(
                    Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density));
            mViewFinder.setFrameSize(DEFAULT_FRAME_SIZE);
            mViewFinder.setFrameVerticalBias(DEFAULT_FRAME_VERTICAL_BIAS);
            mAutoFocusButton.setColorFilter(DEFAULT_AUTO_FOCUS_BUTTON_COLOR);
            mFlashButton.setColorFilter(DEFAULT_FLASH_BUTTON_COLOR);
            mAutoFocusButton.setVisibility(DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY);
            mAutoFocusButtonPosition = DEFAULT_AUTO_FOCUS_BUTTON_POSITION;
            mFlashButton.setVisibility(DEFAULT_FLASH_BUTTON_VISIBILITY);
            mFlashButtonPosition = DEFAULT_FLASH_BUTTON_POSITION;
            mAutoFocusButtonPaddingHorizontal = defaultButtonPadding;
            mAutoFocusButtonPaddingVertical = defaultButtonPadding;
            mFlashButtonPaddingHorizontal = defaultButtonPadding;
            mFlashButtonPaddingVertical = defaultButtonPadding;
            mAutoFocusButton.setPadding(defaultButtonPadding, defaultButtonPadding,
                    defaultButtonPadding, defaultButtonPadding);
            mFlashButton.setPadding(defaultButtonPadding, defaultButtonPadding,
                    defaultButtonPadding, defaultButtonPadding);
            mAutoFocusButtonOnIcon =
                    Utils.getDrawable(context, R.drawable.ic_code_scanner_auto_focus_on);
            mAutoFocusButtonOffIcon =
                    Utils.getDrawable(context, R.drawable.ic_code_scanner_auto_focus_off);
            mFlashButtonOnIcon = Utils.getDrawable(context, R.drawable.ic_code_scanner_flash_on);
            mFlashButtonOffIcon = Utils.getDrawable(context, R.drawable.ic_code_scanner_flash_off);
        } else {
            TypedArray a = null;
            try {
                a = context.getTheme()
                        .obtainStyledAttributes(attrs, R.styleable.CodeScannerView, defStyleAttr,
                                defStyleRes);
                setMaskColor(a.getColor(R.styleable.CodeScannerView_maskColor, DEFAULT_MASK_COLOR));
                setFrameColor(
                        a.getColor(R.styleable.CodeScannerView_frameColor, DEFAULT_FRAME_COLOR));
                setFrameThickness(
                        a.getDimensionPixelOffset(R.styleable.CodeScannerView_frameThickness,
                                Math.round(DEFAULT_FRAME_THICKNESS_DP * density)));
                setFrameCornersSize(
                        a.getDimensionPixelOffset(R.styleable.CodeScannerView_frameCornersSize,
                                Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density)));
                setFrameCornersRadius(
                        a.getDimensionPixelOffset(R.styleable.CodeScannerView_frameCornersRadius,
                                Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density)));
                setFrameAspectRatio(a.getFloat(R.styleable.CodeScannerView_frameAspectRatioWidth,
                                DEFAULT_FRAME_ASPECT_RATIO_WIDTH),
                        a.getFloat(R.styleable.CodeScannerView_frameAspectRatioHeight,
                                DEFAULT_FRAME_ASPECT_RATIO_HEIGHT));
                setFrameSize(a.getFloat(R.styleable.CodeScannerView_frameSize, DEFAULT_FRAME_SIZE));
                setFrameVerticalBias(a.getFloat(R.styleable.CodeScannerView_frameVerticalBias,
                        DEFAULT_FRAME_VERTICAL_BIAS));
                setAutoFocusButtonVisible(
                        a.getBoolean(R.styleable.CodeScannerView_autoFocusButtonVisible,
                                DEFAULT_AUTO_FOCUS_BUTTON_VISIBLE));
                setAutoFocusButtonColor(a.getColor(R.styleable.CodeScannerView_autoFocusButtonColor,
                        DEFAULT_AUTO_FOCUS_BUTTON_COLOR));
                setAutoFocusButtonPosition(buttonPositionFromAttr(
                        a.getInt(R.styleable.CodeScannerView_autoFocusButtonPosition,
                                indexOfButtonPosition(DEFAULT_AUTO_FOCUS_BUTTON_POSITION))));
                setAutoFocusButtonPaddingHorizontal(a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_autoFocusButtonPaddingHorizontal,
                        defaultButtonPadding));
                setAutoFocusButtonPaddingVertical(a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_autoFocusButtonPaddingVertical,
                        defaultButtonPadding));
                final Drawable autoFocusButtonOnIcon =
                        a.getDrawable(R.styleable.CodeScannerView_autoFocusButtonOnIcon);
                setAutoFocusButtonOnIcon(autoFocusButtonOnIcon != null ? autoFocusButtonOnIcon :
                        Utils.getDrawable(context, R.drawable.ic_code_scanner_auto_focus_on));
                final Drawable autoFocusButtonOffIcon =
                        a.getDrawable(R.styleable.CodeScannerView_autoFocusButtonOffIcon);
                setAutoFocusButtonOffIcon(autoFocusButtonOffIcon != null ? autoFocusButtonOffIcon :
                        Utils.getDrawable(context, R.drawable.ic_code_scanner_auto_focus_off));
                setFlashButtonVisible(a.getBoolean(R.styleable.CodeScannerView_flashButtonVisible,
                        DEFAULT_FLASH_BUTTON_VISIBLE));
                setFlashButtonColor(a.getColor(R.styleable.CodeScannerView_flashButtonColor,
                        DEFAULT_FLASH_BUTTON_COLOR));
                setFlashButtonPosition(buttonPositionFromAttr(
                        a.getInt(R.styleable.CodeScannerView_flashButtonPosition,
                                indexOfButtonPosition(DEFAULT_FLASH_BUTTON_POSITION))));
                setFlashButtonPaddingHorizontal(a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_flashButtonPaddingHorizontal,
                        defaultButtonPadding));
                setFlashButtonPaddingVertical(a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_flashButtonPaddingVertical,
                        defaultButtonPadding));
                final Drawable flashButtonOnIcon =
                        a.getDrawable(R.styleable.CodeScannerView_flashButtonOnIcon);
                setFlashButtonOnIcon(flashButtonOnIcon != null ? flashButtonOnIcon :
                        Utils.getDrawable(context, R.drawable.ic_code_scanner_flash_on));
                final Drawable flashButtonOffIcon =
                        a.getDrawable(R.styleable.CodeScannerView_flashButtonOffIcon);
                setFlashButtonOffIcon(flashButtonOffIcon != null ? flashButtonOffIcon :
                        Utils.getDrawable(context, R.drawable.ic_code_scanner_flash_off));
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
        if (isInEditMode()) {
            setAutoFocusEnabled(true);
            setFlashEnabled(true);
        }
        addView(mPreviewView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mViewFinder,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mAutoFocusButton, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mFlashButton, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount > MAX_CHILD_COUNT) {
            throw new IllegalStateException("CodeScannerView can have zero or one child");
        }
        measureChildWithMargins(mPreviewView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(mViewFinder, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(mAutoFocusButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(mFlashButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
        if (childCount == MAX_CHILD_COUNT) {
            final Rect frameRect = mViewFinder.getFrameRect();
            measureChildWithMargins(getChildAt(HINT_VIEW_INDEX), widthMeasureSpec, 0,
                    heightMeasureSpec, frameRect != null ? frameRect.getBottom() : 0);
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right,
                            final int bottom) {
        final int childCount = getChildCount();
        if (childCount > MAX_CHILD_COUNT) {
            throw new IllegalStateException("CodeScannerView can have zero or one child");
        }
        final int width = right - left;
        final int height = bottom - top;
        final Point previewSize = mPreviewSize;
        if (previewSize == null) {
            mPreviewView.layout(0, 0, width, height);
        } else {
            int frameLeft = 0;
            int frameTop = 0;
            int frameRight = width;
            int frameBottom = height;
            final int previewWidth = previewSize.getX();
            if (previewWidth > width) {
                final int d = (previewWidth - width) / 2;
                frameLeft -= d;
                frameRight += d;
            }
            final int previewHeight = previewSize.getY();
            if (previewHeight > height) {
                final int d = (previewHeight - height) / 2;
                frameTop -= d;
                frameBottom += d;
            }
            mPreviewView.layout(frameLeft, frameTop, frameRight, frameBottom);
        }
        mViewFinder.layout(0, 0, width, height);
        layoutButton(mAutoFocusButton, mAutoFocusButtonPosition, width, height);
        layoutButton(mFlashButton, mFlashButtonPosition, width, height);
        if (childCount == MAX_CHILD_COUNT) {
            final Rect frameRect = mViewFinder.getFrameRect();
            final int viewTop = frameRect != null ? frameRect.getBottom() : 0;
            final View hintView = getChildAt(HINT_VIEW_INDEX);
            final int paddingLeft = getPaddingLeft();
            final int paddingTop = getPaddingTop();
            if (hintView.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) hintView.getLayoutParams();
                final int childLeft = paddingLeft + lp.leftMargin;
                final int childTop = paddingTop + lp.topMargin + viewTop;
                hintView.layout(childLeft, childTop, childLeft + hintView.getMeasuredWidth(),
                        childTop + hintView.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth,
                                 final int oldHeight) {
        final SizeListener listener = mSizeListener;
        if (listener != null) {
            listener.onSizeChanged(width, height);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        final CodeScanner codeScanner = mCodeScanner;
        final Rect frameRect = getFrameRect();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        if (codeScanner != null && frameRect != null &&
                codeScanner.isAutoFocusSupportedOrUnknown() && codeScanner.isTouchFocusEnabled() &&
                event.getAction() == MotionEvent.ACTION_DOWN && frameRect.isPointInside(x, y)) {
            final int areaSize = mFocusAreaSize;
            codeScanner.performTouchFocus(
                    new Rect(x - areaSize, y - areaSize, x + areaSize, y + areaSize).fitIn(
                            frameRect));
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected boolean checkLayoutParams(@Nullable final ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @NonNull
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(@Nullable final AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @NonNull
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(@NonNull final ViewGroup.LayoutParams p) {
        if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        } else {
            return new LayoutParams(p);
        }
    }

    @NonNull
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @ColorInt
    public int getMaskColor() {
        return mViewFinder.getMaskColor();
    }

    public void setMaskColor(@ColorInt final int color) {
        mViewFinder.setMaskColor(color);
    }

    @ColorInt
    public int getFrameColor() {
        return mViewFinder.getFrameColor();
    }

    public void setFrameColor(@ColorInt final int color) {
        mViewFinder.setFrameColor(color);
    }

    @Px
    public int getFrameThickness() {
        return mViewFinder.getFrameThickness();
    }

    public void setFrameThickness(@Px final int thickness) {
        if (thickness < 0) {
            throw new IllegalArgumentException("Frame thickness can't be negative");
        }
        mViewFinder.setFrameThickness(thickness);
    }

    @Px
    public int getFrameCornersSize() {
        return mViewFinder.getFrameCornersSize();
    }

    public void setFrameCornersSize(@Px final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Frame corners size can't be negative");
        }
        mViewFinder.setFrameCornersSize(size);
    }

    @Px
    public int getFrameCornersRadius() {
        return mViewFinder.getFrameCornersRadius();
    }

    public void setFrameCornersRadius(@Px final int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Frame corners radius can't be negative");
        }
        mViewFinder.setFrameCornersRadius(radius);
    }

    @FloatRange(from = 0.1, to = 1.0)
    public float getFrameSize() {
        return mViewFinder.getFrameSize();
    }

    public void setFrameSize(@FloatRange(from = 0.1, to = 1) final float size) {
        if (size < 0.1 || size > 1) {
            throw new IllegalArgumentException(
                    "Max frame size value should be between 0.1 and 1, inclusive");
        }
        mViewFinder.setFrameSize(size);
    }

    @FloatRange(from = 0.0f, to = 1.0f)
    public float getFrameVerticalBias() {
        return mViewFinder.getFrameVerticalBias();
    }

    public void setFrameVerticalBias(@FloatRange(from = 0.0f, to = 1.0f) final float bias) {
        if (bias < 0f || bias > 1f) {
            throw new IllegalArgumentException(
                    "Max frame size value should be between 0 and 1, inclusive");
        }
        mViewFinder.setFrameVerticalBias(bias);
    }

    @FloatRange(from = 0, fromInclusive = false)
    public float getFrameAspectRatioWidth() {
        return mViewFinder.getFrameAspectRatioWidth();
    }

    public void setFrameAspectRatioWidth(
            @FloatRange(from = 0, fromInclusive = false) final float ratioWidth) {
        if (ratioWidth <= 0) {
            throw new IllegalArgumentException(
                    "Frame aspect ratio values should be greater than zero");
        }
        mViewFinder.setFrameAspectRatioWidth(ratioWidth);
    }

    @FloatRange(from = 0, fromInclusive = false)
    public float getFrameAspectRatioHeight() {
        return mViewFinder.getFrameAspectRatioHeight();
    }

    public void setFrameAspectRatioHeight(
            @FloatRange(from = 0, fromInclusive = false) final float ratioHeight) {
        if (ratioHeight <= 0) {
            throw new IllegalArgumentException(
                    "Frame aspect ratio values should be greater than zero");
        }
        mViewFinder.setFrameAspectRatioHeight(ratioHeight);
    }

    public void setFrameAspectRatio(
            @FloatRange(from = 0, fromInclusive = false) final float ratioWidth,
            @FloatRange(from = 0, fromInclusive = false) final float ratioHeight) {
        if (ratioWidth <= 0 || ratioHeight <= 0) {
            throw new IllegalArgumentException(
                    "Frame aspect ratio values should be greater than zero");
        }
        mViewFinder.setFrameAspectRatio(ratioWidth, ratioHeight);
    }

    public boolean isMaskVisible() {
        return mViewFinder.getVisibility() == VISIBLE;
    }

    public void setMaskVisible(final boolean visible) {
        mViewFinder.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public boolean isAutoFocusButtonVisible() {
        return mAutoFocusButton.getVisibility() == VISIBLE;
    }

    public void setAutoFocusButtonVisible(final boolean visible) {
        mAutoFocusButton.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    @ColorInt
    public int getAutoFocusButtonColor() {
        return mAutoFocusButtonColor;
    }

    public void setAutoFocusButtonColor(@ColorInt final int color) {
        mAutoFocusButtonColor = color;
        mAutoFocusButton.setColorFilter(color);
    }

    @NonNull
    public ButtonPosition getAutoFocusButtonPosition() {
        return mAutoFocusButtonPosition;
    }

    public void setAutoFocusButtonPosition(@NonNull final ButtonPosition position) {
        Objects.requireNonNull(position);
        final boolean changed = position != mAutoFocusButtonPosition;
        mAutoFocusButtonPosition = position;
        if (changed && isLaidOut()) {
            requestLayout();
        }
    }

    @Px
    public int getAutoFocusButtonPaddingHorizontal() {
        return mAutoFocusButtonPaddingHorizontal;
    }

    public void setAutoFocusButtonPaddingHorizontal(@Px final int padding) {
        if (padding < 0) {
            throw new IllegalArgumentException("Padding should be equal to or grater then zero");
        }
        final boolean changed = padding != mAutoFocusButtonPaddingHorizontal;
        mAutoFocusButtonPaddingHorizontal = padding;
        if (changed) {
            invalidateAutoFocusButtonPadding();
        }
    }

    @Px
    public int getAutoFocusButtonPaddingVertical() {
        return mAutoFocusButtonPaddingVertical;
    }

    public void setAutoFocusButtonPaddingVertical(@Px final int padding) {
        if (padding < 0) {
            throw new IllegalArgumentException("Padding should be equal to or grater then zero");
        }
        final boolean changed = padding != mAutoFocusButtonPaddingVertical;
        mAutoFocusButtonPaddingVertical = padding;
        if (changed) {
            invalidateAutoFocusButtonPadding();
        }
    }

    public boolean isFlashButtonVisible() {
        return mFlashButton.getVisibility() == VISIBLE;
    }

    @NonNull
    public Drawable getAutoFocusButtonOnIcon() {
        return mAutoFocusButtonOnIcon;
    }

    public void setAutoFocusButtonOnIcon(@NonNull final Drawable icon) {
        Objects.requireNonNull(icon);
        final boolean changed = icon != mAutoFocusButtonOnIcon;
        mAutoFocusButtonOnIcon = icon;
        final CodeScanner codeScanner = mCodeScanner;
        if (changed && codeScanner != null) {
            setAutoFocusEnabled(codeScanner.isAutoFocusEnabled());
        }
    }

    @NonNull
    public Drawable getAutoFocusButtonOffIcon() {
        return mAutoFocusButtonOffIcon;
    }

    public void setAutoFocusButtonOffIcon(@NonNull final Drawable icon) {
        Objects.requireNonNull(icon);
        final boolean changed = icon != mAutoFocusButtonOffIcon;
        mAutoFocusButtonOffIcon = icon;
        final CodeScanner codeScanner = mCodeScanner;
        if (changed && codeScanner != null) {
            setAutoFocusEnabled(codeScanner.isAutoFocusEnabled());
        }
    }

    public void setFlashButtonVisible(final boolean visible) {
        mFlashButton.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    @ColorInt
    public int getFlashButtonColor() {
        return mFlashButtonColor;
    }

    public void setFlashButtonColor(@ColorInt final int color) {
        mFlashButtonColor = color;
        mFlashButton.setColorFilter(color);
    }

    @NonNull
    public ButtonPosition getFlashButtonPosition() {
        return mFlashButtonPosition;
    }

    public void setFlashButtonPosition(@NonNull final ButtonPosition position) {
        Objects.requireNonNull(position);
        final boolean changed = position != mFlashButtonPosition;
        mFlashButtonPosition = position;
        if (changed) {
            requestLayout();
        }
    }

    @Px
    public int getFlashButtonPaddingHorizontal() {
        return mFlashButtonPaddingHorizontal;
    }

    public void setFlashButtonPaddingHorizontal(@Px final int padding) {
        if (padding < 0) {
            throw new IllegalArgumentException("Padding should be equal to or grater then zero");
        }
        final boolean changed = padding != mFlashButtonPaddingHorizontal;
        mFlashButtonPaddingHorizontal = padding;
        if (changed) {
            invalidateFlashButtonPadding();
        }
    }

    @Px
    public int getFlashButtonPaddingVertical() {
        return mFlashButtonPaddingVertical;
    }

    public void setFlashButtonPaddingVertical(@Px final int padding) {
        if (padding < 0) {
            throw new IllegalArgumentException("Padding should be equal to or grater then zero");
        }
        final boolean changed = padding != mFlashButtonPaddingVertical;
        mFlashButtonPaddingVertical = padding;
        if (changed) {
            invalidateFlashButtonPadding();
        }
    }

    @NonNull
    public Drawable getFlashButtonOnIcon() {
        return mFlashButtonOnIcon;
    }

    public void setFlashButtonOnIcon(@NonNull final Drawable icon) {
        Objects.requireNonNull(icon);
        final boolean changed = icon != mFlashButtonOnIcon;
        mFlashButtonOnIcon = icon;
        final CodeScanner codeScanner = mCodeScanner;
        if (changed && codeScanner != null) {
            setFlashEnabled(codeScanner.isFlashEnabled());
        }
    }

    @NonNull
    public Drawable getFlashButtonOffIcon() {
        return mFlashButtonOffIcon;
    }

    public void setFlashButtonOffIcon(@NonNull final Drawable icon) {
        Objects.requireNonNull(icon);
        final boolean changed = icon != mFlashButtonOffIcon;
        mFlashButtonOffIcon = icon;
        final CodeScanner codeScanner = mCodeScanner;
        if (changed && codeScanner != null) {
            setFlashEnabled(codeScanner.isFlashEnabled());
        }
    }

    @NonNull
    SurfaceView getPreviewView() {
        return mPreviewView;
    }

    @NonNull
    ViewFinder getViewFinder() {
        return mViewFinder;
    }

    @Nullable
    Rect getFrameRect() {
        return mViewFinder.getFrameRect();
    }

    void setPreviewSize(@Nullable final Point previewSize) {
        mPreviewSize = previewSize;
        requestLayout();
    }

    void setSizeListener(@Nullable final SizeListener sizeListener) {
        mSizeListener = sizeListener;
    }

    void setCodeScanner(@NonNull final CodeScanner codeScanner) {
        if (mCodeScanner != null) {
            throw new IllegalStateException("Code scanner has already been set");
        }
        mCodeScanner = codeScanner;
        setAutoFocusEnabled(codeScanner.isAutoFocusEnabled());
        setFlashEnabled(codeScanner.isFlashEnabled());
    }

    void setAutoFocusEnabled(final boolean enabled) {
        mAutoFocusButton.setImageDrawable(
                enabled ? mAutoFocusButtonOnIcon : mAutoFocusButtonOffIcon);
    }

    void setFlashEnabled(final boolean enabled) {
        mFlashButton.setImageDrawable(enabled ? mFlashButtonOnIcon : mFlashButtonOffIcon);
    }

    private void layoutButton(final View button, final ButtonPosition position,
                              final int parentWidth, final int parentHeight) {
        final int width = button.getMeasuredWidth();
        final int height = button.getMeasuredHeight();
        final int layoutDirection = getLayoutDirection();
        switch (position) {
            case TOP_START: {
                if (layoutDirection == LayoutDirection.RTL) {
                    button.layout(parentWidth - width, 0, parentWidth, height);
                } else {
                    button.layout(0, 0, width, height);
                }
                break;
            }
            case TOP_END: {
                if (layoutDirection == LayoutDirection.RTL) {
                    button.layout(0, 0, width, height);
                } else {
                    button.layout(parentWidth - width, 0, parentWidth, height);
                }
                break;
            }
            case BOTTOM_START: {
                if (layoutDirection == LayoutDirection.RTL) {
                    button.layout(parentWidth - width, parentHeight - height, parentWidth,
                            parentHeight);
                } else {
                    button.layout(0, parentHeight - height, width, parentHeight);
                }
                break;
            }
            case BOTTOM_END: {
                if (layoutDirection == LayoutDirection.RTL) {
                    button.layout(0, parentHeight - height, width, parentHeight);
                } else {
                    button.layout(parentWidth - width, parentHeight - height, parentWidth,
                            parentHeight);
                }
                break;
            }
        }
    }

    private void invalidateAutoFocusButtonPadding() {
        final int autoFocusButtonHorizontalPadding = mAutoFocusButtonPaddingHorizontal;
        final int autoFocusButtonVerticalPadding = mAutoFocusButtonPaddingVertical;
        mAutoFocusButton.setPadding(autoFocusButtonHorizontalPadding,
                autoFocusButtonVerticalPadding, autoFocusButtonHorizontalPadding,
                autoFocusButtonVerticalPadding);
    }

    private void invalidateFlashButtonPadding() {
        final int flashButtonHorizontalPadding = mFlashButtonPaddingHorizontal;
        final int flashButtonVerticalPadding = mFlashButtonPaddingVertical;
        mFlashButton.setPadding(flashButtonHorizontalPadding, flashButtonVerticalPadding,
                flashButtonHorizontalPadding, flashButtonVerticalPadding);
    }

    @NonNull
    private static ButtonPosition buttonPositionFromAttr(final int value) {
        switch (value) {
            case 1: {
                return ButtonPosition.TOP_END;
            }
            case 2: {
                return ButtonPosition.BOTTOM_START;
            }
            case 3: {
                return ButtonPosition.BOTTOM_END;
            }
            default: {
                return ButtonPosition.TOP_START;
            }
        }
    }

    private static int indexOfButtonPosition(@NonNull final ButtonPosition value) {
        switch (value) {
            case TOP_END: {
                return 1;
            }
            case BOTTOM_START: {
                return 2;
            }
            case BOTTOM_END: {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }

    interface SizeListener {
        void onSizeChanged(int width, int height);
    }

    public static final class LayoutParams extends MarginLayoutParams {

        public LayoutParams(@NonNull final Context c, @Nullable final AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(final int width, final int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull final MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull final ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private final class AutoFocusClickListener implements OnClickListener {
        @Override
        public void onClick(final View view) {
            final CodeScanner scanner = mCodeScanner;
            if (scanner == null || !scanner.isAutoFocusSupportedOrUnknown()) {
                return;
            }
            final boolean enabled = !scanner.isAutoFocusEnabled();
            scanner.setAutoFocusEnabled(enabled);
            setAutoFocusEnabled(enabled);
        }
    }

    private final class FlashClickListener implements OnClickListener {
        @Override
        public void onClick(final View view) {
            final CodeScanner scanner = mCodeScanner;
            if (scanner == null || !scanner.isFlashSupportedOrUnknown()) {
                return;
            }
            final boolean enabled = !scanner.isFlashEnabled();
            scanner.setFlashEnabled(enabled);
            setFlashEnabled(enabled);
        }
    }
}
