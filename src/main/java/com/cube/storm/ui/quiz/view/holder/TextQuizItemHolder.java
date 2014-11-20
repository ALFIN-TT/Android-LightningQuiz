package com.cube.storm.ui.quiz.view.holder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cube.storm.UiSettings;
import com.cube.storm.ui.model.property.TextProperty;
import com.cube.storm.ui.quiz.R;
import com.cube.storm.ui.quiz.model.quiz.TextQuizItem;
import com.cube.storm.ui.view.holder.ViewHolder;
import com.cube.storm.ui.view.holder.ViewHolderController;

/**
 * View holder for {@link com.cube.storm.ui.quiz.model.quiz.TextQuizItem} in the adapter
 *
 * @author Callum Taylor
 * @project LightningQuiz
 */
public class TextQuizItemHolder extends ViewHolderController
{
	@Override public ViewHolder createViewHolder(ViewGroup parent)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_quiz_item_view, parent, false);
		mViewHolder = new TextQuizItemViewHolder(view);

		return mViewHolder;
	}

	private class TextQuizItemViewHolder extends ViewHolder<TextQuizItem>
	{
		protected TextView title;
		protected TextView hint;
		protected ViewGroup options;

		public TextQuizItemViewHolder(View view)
		{
			super(view);

			title = (TextView)view.findViewById(R.id.title);
			hint = (TextView)view.findViewById(R.id.hint);
			options = (ViewGroup)view.findViewById(R.id.options_container);
		}

		@Override public void populateView(TextQuizItem model)
		{
			title.setVisibility(View.GONE);
			hint.setVisibility(View.GONE);

			if (model.getTitle() != null)
			{
				String content = UiSettings.getInstance().getTextProcessor().process(model.getTitle().getContent());

				if (!TextUtils.isEmpty(content))
				{
					title.setText(content);
					title.setVisibility(View.VISIBLE);
				}
				else
				{
					title.setVisibility(View.GONE);
				}
			}

			if (model.getHint() != null)
			{
				String content = UiSettings.getInstance().getTextProcessor().process(model.getHint().getContent());

				if (!TextUtils.isEmpty(content))
				{
					hint.setText(content);
					hint.setVisibility(View.VISIBLE);
				}
				else
				{
					hint.setVisibility(View.GONE);
				}
			}

			options.removeAllViewsInLayout();

			for (TextProperty option : model.getOptions())
			{
				View row = LayoutInflater.from(options.getContext()).inflate(R.layout.text_quiz_item_item, options, false);

				if (option.getContent() != null)
				{
					String title = UiSettings.getInstance().getTextProcessor().process(option.getContent());

					if (!TextUtils.isEmpty(title))
					{
						((TextView)row.findViewById(R.id.title)).setText(title);
					}
				}

				row.setOnClickListener(new ModelClickListener(model));
				options.addView(row);
			}
		}

		private class ModelClickListener implements OnClickListener
		{
			private TextQuizItem model;

			private ModelClickListener(TextQuizItem model)
			{
				this.model = model;
			}

			@Override public void onClick(View v)
			{
				int index = options.indexOfChild(v);
				CheckBox checker = ((CheckBox)v.findViewById(R.id.checkbox));

				if (!checker.isChecked())
				{
					checker.setChecked(true);
					model.getSelectHistory().add(index);
				}
				else
				{
					checker.setChecked(false);
					model.getSelectHistory().remove((Integer)index);
				}

				// disable select if checked > limit
				if (model.getSelectHistory().size() > model.getLimit())
				{
					int remIndex = model.getSelectHistory().get(0);
					model.getSelectHistory().remove(0);
					((CheckBox)options.getChildAt(remIndex).findViewById(R.id.checkbox)).setChecked(false);
				}

				// check the answers in the history
				if (model.getAnswer() != null && model.getAnswer().size() == model.getSelectHistory().size())
				{
					for (int answer : model.getAnswer())
					{
						if (model.getSelectHistory().contains(answer))
						{
							model.setCorrect(true);
						}
						else
						{
							model.setCorrect(false);
							break;
						}
					}
				}
			}
		}
	}
}
