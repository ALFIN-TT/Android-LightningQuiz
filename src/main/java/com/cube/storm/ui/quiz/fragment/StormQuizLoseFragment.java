package com.cube.storm.ui.quiz.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cube.storm.UiSettings;
import com.cube.storm.ui.activity.StormActivity;
import com.cube.storm.ui.model.property.LinkProperty;
import com.cube.storm.ui.quiz.R;
import com.cube.storm.ui.quiz.activity.StormQuizResultsActivity;
import com.cube.storm.ui.quiz.model.page.QuizPage;
import com.cube.storm.ui.quiz.model.quiz.QuizItem;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class StormQuizLoseFragment extends Fragment implements OnClickListener
{
	@Getter private QuizPage page;
	protected TextView loseTitle;
	protected Button retake;
	protected Button home;
	protected ViewGroup remember;
	protected ViewGroup embeddedLinksContainer;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.quiz_lose_view, container, false);

		loseTitle = (TextView)v.findViewById(R.id.lose_title);
		retake = (Button)v.findViewById(R.id.retake);
		home = (Button)v.findViewById(R.id.home_button);
		remember = (ViewGroup)v.findViewById(R.id.remember_container);
		embeddedLinksContainer = (ViewGroup)v.findViewById(R.id.related_container);

		retake.setText("Try Again?");
		home.setText("Home");
		home.setOnClickListener(this);
		retake.setOnClickListener(this);

		return v;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		boolean[] answers = getArguments().getBooleanArray(StormQuizResultsActivity.EXTRA_RESULTS);

		if (getArguments().containsKey(StormActivity.EXTRA_PAGE))
		{
			page = (QuizPage)getArguments().get(StormActivity.EXTRA_PAGE);
		}
		else if (getArguments().containsKey(StormActivity.EXTRA_URI))
		{
			String pageUri = getArguments().getString(StormActivity.EXTRA_URI);
			page = (QuizPage)UiSettings.getInstance().getViewBuilder().buildPage(Uri.parse(pageUri));
		}
		else
		{
			Toast.makeText(getActivity(), "Failed to load page", Toast.LENGTH_SHORT).show();
			getActivity().finish();

			return;
		}

		if (page.getLoseMessage() != null && !TextUtils.isEmpty(page.getLoseMessage().getContent()))
		{
			loseTitle.setText(UiSettings.getInstance().getTextProcessor().process(page.getLoseMessage().getContent()));
		}
		else
		{
			loseTitle.setVisibility(View.GONE);
		}

		if (answers != null)
		{
			int index = 0;
			for (QuizItem question : page.getChildren())
			{
				if (!answers[index])
				{
					View row = LayoutInflater.from(getActivity()).inflate(R.layout.quiz_remember_row, remember, false);
					if (row != null && question != null)
					{
						((TextView)row.findViewById(R.id.annotation)).setText("" + (index + 1));

						((TextView)row.findViewById(R.id.title)).setText(UiSettings.getInstance().getTextProcessor().process(question.getTitle().getContent()));
						((TextView)row.findViewById(R.id.description)).setText(UiSettings.getInstance().getTextProcessor().process(question.getFailure().getContent()));
						(row.findViewById(R.id.description)).setVisibility(View.VISIBLE);

						remember.addView(row);
					}
				}

				index++;
			}
		}

		if (page.getLoseRelatedLinks() != null)
		{
			embeddedLinksContainer.removeAllViews();

			int index = 0;
			for (LinkProperty link : page.getLoseRelatedLinks())
			{
				if (link == null) continue;
				final LinkProperty property = link;

				View embeddedLinkView = LayoutInflater.from(embeddedLinksContainer.getContext()).inflate(R.layout.button_embedded_link, embeddedLinksContainer, false);
				if (embeddedLinkView != null)
				{
					Button button = (Button)embeddedLinkView.findViewById(R.id.button);
					button.setText(property.getTitle().getContent());

					button.setOnClickListener(new View.OnClickListener()
					{
						@Override public void onClick(View v)
						{
							UiSettings.getInstance().getLinkHandler().handleLink(v.getContext(), property);
						}
					});

					embeddedLinksContainer.setVisibility(View.VISIBLE);
					embeddedLinksContainer.addView(button);
				}
			}

			if (page.getLoseRelatedLinks().size() > 0)
			{
				embeddedLinksContainer.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override public void onClick(View v)
	{
		if (v == home)
		{
			getActivity().finish();
		}
		else if (v == retake)
		{

		}
		else if (v.getId() == R.id.button)
		{
			int index = (Integer)v.getTag();

			List<LinkProperty> list = new ArrayList<LinkProperty>(page.getLoseRelatedLinks());

			LinkProperty link = list.get(index);
			UiSettings.getInstance().getLinkHandler().handleLink(v.getContext(), link);
		}
	}
}