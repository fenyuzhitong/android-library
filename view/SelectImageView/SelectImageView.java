

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * author Xianzhuo Rong
 * time   2016/6/28.
 * email rongxianzhuo@gmail.com
 * github https://github.com/rongxianzhuo
 * 选择图片View 目前最多可以选择6张
 */
public class SelectImageView extends LinearLayout {

    public interface OnImageChangeListener {
        void insertImage(int position);
        void removeImage(int position);
    }

    public static final int MAX_IMAGES_COUNT = 6;

    private View view;
    private ImageView[] imageViews = new ImageView[MAX_IMAGES_COUNT];
    private Bitmap[] bitmaps = new Bitmap[MAX_IMAGES_COUNT];
    private View[] removeButtons = new View[MAX_IMAGES_COUNT], viewArea = new View[MAX_IMAGES_COUNT];
    private BaseActivity activity;
    private View line2;
    private OnImageChangeListener listener = null;

    public SelectImageView(Context context) {
        super(context);
    }

    public SelectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.view_select_image, this, true);
        line2 = view.findViewById(R.id.line2);
        imageAreaInit();
        refreshImageView();
    }

    public void setup(BaseActivity activity) {
        this.activity = activity;
    }

    public void setup(BaseActivity activity, Bitmap[] bitmaps) {
        this.activity = activity;
        int i = 0;
        while (i < MAX_IMAGES_COUNT) {
            if (i < bitmaps.length) {
                this.bitmaps[i] = bitmaps[i];
                Log.e("rxz", "" + bitmaps[i]);
            }
            else this.bitmaps[i] = null;
            i++;
        }
        refreshImageView();
    }

    private void refreshImageView() {
        line2.setVisibility(GONE);
        int i = 0;
        while (i < MAX_IMAGES_COUNT) {
            if (bitmaps[i] == null) {
                imageViews[i].setImageBitmap(null);
                if (i > 0 && bitmaps[i - 1] == null) viewArea[i].setVisibility(View.INVISIBLE);
                else {
                    viewArea[i].setVisibility(View.VISIBLE);
                    imageViews[i].setImageResource(R.drawable.add_image);
                    removeButtons[i].setVisibility(View.INVISIBLE);
                    if (i >= 3) line2.setVisibility(VISIBLE);
                }
            }
            else {
                imageViews[i].setImageBitmap(bitmaps[i]);
                removeButtons[i].setVisibility(View.VISIBLE);
                viewArea[i].setVisibility(View.VISIBLE);
                if (i >= 3) line2.setVisibility(VISIBLE);
            }
            i++;
        }
    }

    public void clearImages() {
        int i = 0;
        while (i < MAX_IMAGES_COUNT) bitmaps[i++] = null;
        refreshImageView();
    }

    private void imageAreaInit() {
        imageViews[0] = (ImageView) view.findViewById(R.id.image1);
        imageViews[1] = (ImageView) view.findViewById(R.id.image2);
        imageViews[2] = (ImageView) view.findViewById(R.id.image3);
        imageViews[3] = (ImageView) view.findViewById(R.id.image4);
        imageViews[4] = (ImageView) view.findViewById(R.id.image5);
        imageViews[5] = (ImageView) view.findViewById(R.id.image6);
        removeButtons[0] = view.findViewById(R.id.remove1);
        removeButtons[1] = view.findViewById(R.id.remove2);
        removeButtons[2] = view.findViewById(R.id.remove3);
        removeButtons[3] = view.findViewById(R.id.remove4);
        removeButtons[4] = view.findViewById(R.id.remove5);
        removeButtons[5] = view.findViewById(R.id.remove6);
        viewArea[0] = view.findViewById(R.id.view_area1);
        viewArea[1] = view.findViewById(R.id.view_area2);
        viewArea[2] = view.findViewById(R.id.view_area3);
        viewArea[3] = view.findViewById(R.id.view_area4);
        viewArea[4] = view.findViewById(R.id.view_area5);
        viewArea[5] = view.findViewById(R.id.view_area6);
        int i = 0;
        while (i < MAX_IMAGES_COUNT) imageViews[i].setOnClickListener(getImageClickListener(i++));
        i = 0;
        while (i < MAX_IMAGES_COUNT) removeButtons[i].setOnClickListener(getRemoveClickListener(i++));
    }

    private View.OnClickListener getImageClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectImageDialog(activity, new SelectImageDialog.GetPictureCallBack() {
                    @Override
                    public void getPicture(Bitmap bitmap) {
                        if (listener != null) listener.insertImage(position);
                        bitmaps[position] = bitmap;
                        refreshImageView();
                    }
                });
            }
        };
    }

    private View.OnClickListener getRemoveClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.removeImage(position);
                int i = position;
                while (i + 1 < MAX_IMAGES_COUNT) {
                    bitmaps[i] = bitmaps[i + 1];
                    i++;
                }
                bitmaps[i] = null;
                refreshImageView();
            }
        };
    }

    public Bitmap[] getBitmaps() {
        return bitmaps;
    }

    public void setListener(OnImageChangeListener listener) {
        this.listener = listener;
    }
}
