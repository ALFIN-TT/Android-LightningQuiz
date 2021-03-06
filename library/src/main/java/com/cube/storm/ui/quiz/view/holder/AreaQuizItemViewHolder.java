package com.cube.storm.ui.quiz.view.holder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cube.storm.ui.QuizSettings;
import com.cube.storm.ui.lib.helper.ImageHelper;
import com.cube.storm.ui.quiz.R;
import com.cube.storm.ui.quiz.lib.QuizEventHook;
import com.cube.storm.ui.quiz.model.property.CoordinateProperty;
import com.cube.storm.ui.quiz.model.property.ZoneProperty;
import com.cube.storm.ui.quiz.model.quiz.AreaQuizItem;
import com.cube.storm.ui.view.ImageView;
import com.cube.storm.ui.view.TextView;
import com.cube.storm.ui.view.holder.ViewHolder;
import com.cube.storm.ui.view.holder.ViewHolderFactory;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * // TODO: Add class description
 *
 * @author Matt Allen
 * @project Hazards
 */
public class AreaQuizItemViewHolder extends ViewHolder<AreaQuizItem>
{
	private transient Bitmap overlay;
	private ImageView image;
	private ImageView canvas;
	protected TextView title;
	protected TextView hint;

	public AreaQuizItemViewHolder(View itemView)
	{
		super(itemView);

		title = (TextView)itemView.findViewById(R.id.title);
		hint = (TextView)itemView.findViewById(R.id.hint);
		image = (ImageView)itemView.findViewById(R.id.image_view);
		canvas = (ImageView)itemView.findViewById(R.id.canvas);

		Drawable drawable = itemView.getResources().getDrawable(R.drawable.area_select);
		if (drawable != null)
		{
			if (drawable instanceof BitmapDrawable)
			{
				overlay = ((BitmapDrawable)drawable).getBitmap();
			}
			else
			{
				overlay = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);

				Canvas canvas = new Canvas(overlay);
				drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
				drawable.draw(canvas);
			}
		}
	}

	public static class Factory extends ViewHolderFactory
	{
		@Override public AreaQuizItemViewHolder createViewHolder(ViewGroup parent)
		{
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_area_selection, parent, false);
			return new AreaQuizItemViewHolder(view);
		}
	}

	@Override public void populateView(final AreaQuizItem model)
	{
		title.populate(model.getTitle());
		hint.populate(model.getHint());

		if (model.getImage() != null)
		{
			ImageHelper.displayImage(image, model.getImage(), new ImageLoadingListener()
			{
				@Override public void onLoadingStarted(String imageUri, View view)
				{
				}

				@Override public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
				}

				@Override public void onLoadingCancelled(String imageUri, View view)
				{
				}

				@Override public void onLoadingComplete(String imageUri, final View view, final Bitmap loadedImage)
				{
					view.post(new Runnable()
					{
						@Override public void run()
						{
							canvas.setLayoutParams(new FrameLayout.LayoutParams(view.getMeasuredWidth(), view.getMeasuredHeight()));
							canvas.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));

							if (model.getTouchCoordinate() != null)
							{
								drawTouchZone(canvas, model.getTouchCoordinate());
							}
						}
					});
				}

			});
			canvas.setOnTouchListener(new OnTouchListener()
			{
				private long downTime = 0;

				@Override public boolean onTouch(View v, MotionEvent event)
				{
					if (event.getAction() == MotionEvent.ACTION_DOWN)
					{
						downTime = event.getDownTime();
						return true;
					}

					if (v.getId() == R.id.canvas && event.getAction() == MotionEvent.ACTION_UP && event.getEventTime() - downTime >= 40 && event.getEventTime() - downTime <= 150)
					{
						float x = event.getX();
						float y = event.getY();
						float xpc = x / v.getMeasuredWidth();
						float ypc = y / v.getMeasuredHeight();

						CoordinateProperty touchCoordinate = new CoordinateProperty();
						touchCoordinate.setX(xpc);
						touchCoordinate.setY(ypc);
						model.setTouchCoordinate(touchCoordinate);

						drawTouchZone(canvas, touchCoordinate);

						if (model.getAnswer() != null)
						{
							for (ZoneProperty z : model.getAnswer())
							{
								model.setCorrect(z.contains(model.getTouchCoordinate().getX(), model.getTouchCoordinate().getY()));

								if (model.isCorrect())
								{
									break;
								}
							}
						}

						for (QuizEventHook quizEventHook : QuizSettings.getInstance().getEventHooks())
						{
							quizEventHook.onQuizOptionSelected(v.getContext(), itemView, model, new float[]{model.getTouchCoordinate().getX(), model.getTouchCoordinate().getY()});
							quizEventHook.onQuizItemAnswersChanged(v.getContext(), model);
						}

						return true;
					}

					return false;
				}
			});
		}
	}

	private void drawTouchZone(ImageView target, CoordinateProperty touchCoordinate)
	{
		float x = touchCoordinate.getX() * target.getMeasuredWidth();
		float y = touchCoordinate.getY() * target.getMeasuredHeight();
		x -= (overlay.getWidth() / 2.0f);
		y -= (overlay.getHeight() / 2.0f);

		Bitmap bounds = Bitmap.createBitmap(target.getMeasuredWidth(), target.getMeasuredHeight(), Config.ARGB_8888);
		Canvas selectCanvas = new Canvas(bounds);
		selectCanvas.drawBitmap(overlay, x, y, new Paint());
		((ImageView)target).setImageBitmap(bounds);
	}
}
