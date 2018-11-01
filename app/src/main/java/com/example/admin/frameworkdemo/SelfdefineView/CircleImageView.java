package com.example.admin.frameworkdemo.SelfdefineView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
//import android.widget.ImageView;
//import android.support.v7.widget.AppCompatImageView

import com.example.admin.frameworkdemo.R;

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
    //��Բ�Ŀ��
    private int outCircleWidth;

    //��Բ����ɫ
    private int outCircleColor = Color.BLUE;

    //����
    private Paint paint;

    //view�Ŀ�Ⱥ͸߶�
    private int viewWidth;
    private int viewHeigth;

    private Bitmap image;

    public CircleImageView(Context context) {
        super(context);
        Log.d("yjm", "aaaaaaaaa");
        // TODO Auto-generated constructor stub
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("yjm", "bbbbbbbbb");
        initAttrs(context, attrs, defStyleAttr);
    }

    /**
     * ��ʼ����Դ�ļ�
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = null;
        if (attrs != null) {
            //�ڶ�����������������styles.xml�ļ��е�<declare-styleable>��ǩ
            //�����Լ��ϵı�ǩ����R�ļ�������ΪR.styleable+name
            array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);

            int len = array.length();

            //��һ������Ϊ���Լ�����������ԣ�R�ļ����ƣ�R.styleable+���Լ�������+�»���+��������
            //�ڶ�������Ϊ�����û������������ԣ������õ�Ĭ�ϵ�ֵ
            for (int i = 0; i < len; i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    //��ȡ����Բ����ɫ
                    case R.styleable.CircleImageView_outCircleColor:
                        this.outCircleColor = array.getColor(attr, Color.WHITE);
                        break;
                    //��ȡ����Բ�İ뾶
                    case R.styleable.CircleImageView_outCircleWidth:
                        //getDimensionPixelSize���ص�ֵ��XML�������õ�ֵ������Ļ�ܶ�
                        this.outCircleWidth = (int) array.getDimensionPixelSize(attr, 50);
                        Log.d("yjm", "outCircleWidth--->"+outCircleWidth);
                        break;
                }
            }
        }
        paint = new Paint();
        paint.setColor(outCircleColor);//��ɫ
        paint.setAntiAlias(true);//���ÿ����
        array.recycle();  //����
    }

    /**
     * view�Ĳ���
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureWith(widthMeasureSpec);
        int height = measureWith(heightMeasureSpec);

        //viewWith=616,  width=656   
        //viewHegth=1030   heigth=1070
        viewWidth = width - outCircleWidth * 2;
        viewHeigth = height - outCircleWidth * 2;

        //���ø÷�����������Ŀ�͸����ý�ȥ����ɲ���������
        setMeasuredDimension(width, height);
    }

    /**
     * ������͸ߣ���һ�������һ��ģ�����(AndroidȺӢ��)
     * @param widthMeasureSpec
     * @return
     */
    private int measureWith(int widthMeasureSpec) {
        int result = 0;
        //��MeasureSpec��������ȡ��������Ĳ���ģʽ�ʹ�С
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //������ģʽ����ȷ
            result = size;
        } else {
            result = viewWidth;
        }
        return result;
    }

    /**
     * ����
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //����ͼƬ
        loadImage();

        if (image != null) {
            //�õ���С��ֵ(��������Ҫȡ����С��)
            int min = Math.min(viewWidth, viewHeigth);

            int circleCenter = min / 2;

            image = Bitmap.createScaledBitmap(image, min, min, false);

            //��Բ,������Բ��X�� Բ��y���뾶������
            canvas.drawCircle(circleCenter + outCircleWidth, circleCenter + outCircleWidth, circleCenter + outCircleWidth, paint);

            //��ͼ��
            canvas.drawBitmap(createCircleBitmap(image, min), outCircleWidth, outCircleWidth, null);
        }


    }

    /**
     * ����һ��Բ�ε�bitmap
     *
     * @param image  �����image
     * @param min
     * @return
     */
    private Bitmap createCircleBitmap(Bitmap image, int min) {

        Bitmap bitmap = null;


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        bitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        //��һ����ͼƬ��С��ȵĻ���
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(image, 0, 0, paint);


        return bitmap;
    }

    /**
     * ����Image
     */
    private void loadImage() {
        //ͨ��XML�����android:src��ȡ
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null) {
            image = bitmapDrawable.getBitmap();
        }
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    }

    /**
     * �����ṩ�Ŀ���������Բ����ɫ�ķ���
     * @param outCircleColor
     */
    public void setOutCircleColor(int outCircleColor) {
        if (null != paint) {
            paint.setColor(outCircleColor);
        }
        this.invalidate();
    }

    /**
     * �����ṩ����������Բ�Ŀ�ȴ�С�ķ���
     * @param outCircleWidth
     */
    public void setOutCircleWidth(int outCircleWidth) {
        this.outCircleWidth = outCircleWidth;
        this.invalidate();
    }

}