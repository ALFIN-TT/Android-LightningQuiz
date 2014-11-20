package com.cube.storm.ui.quiz.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cube.storm.UiSettings;
import com.cube.storm.ui.data.FragmentIntent;
import com.cube.storm.ui.data.FragmentPackage;
import com.cube.storm.ui.lib.adapter.StormPageAdapter;
import com.cube.storm.ui.quiz.R;
import com.cube.storm.ui.quiz.fragment.StormQuizFragment;
import com.cube.storm.ui.quiz.model.page.QuizPage;
import com.cube.storm.ui.quiz.model.quiz.QuizItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Storm quiz fragment used for displaying individual quiz questions. Use this class to display each
 * quiz question as a separate fragment in a view pager in conjunction to {@link com.cube.storm.ui.quiz.activity.StormQuizActivity}.
 * <p/>
 * To display a quiz page in a single list, use {@link com.cube.storm.ui.fragment.StormFragment} with {@link com.cube.storm.ui.activity.StormActivity}.
 *
 *
 * @author Callum Taylor
 * @project LightningQuiz
 */
public class StormQuizActivity extends ActionBarActivity implements OnPageChangeListener
{
	public static final String EXTRA_PAGE = "stormui.page";
	public static final String EXTRA_URI = "stormui.uri";
	public static final String EXTRA_QUESTION = "stormquiz.question";

	private StormPageAdapter pageAdapter;
	private QuizPage page;
	private ViewPager viewPager;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.quiz_view);

		viewPager = (ViewPager)findViewById(R.id.view_pager);

		if (getIntent().getExtras() == null)
		{
			Toast.makeText(this, "Failed to load page", Toast.LENGTH_SHORT).show();
			finish();

			return;
		}

		if (getIntent().getExtras().containsKey(EXTRA_PAGE))
		{
			page = (QuizPage)getIntent().getExtras().get(EXTRA_PAGE);
		}
		else if (getIntent().getExtras().containsKey(EXTRA_URI))
		{
			String pageUri = String.valueOf(getIntent().getExtras().get(EXTRA_URI));
			page = (QuizPage)UiSettings.getInstance().getViewBuilder().buildPage(Uri.parse(pageUri));
		}

		if (page != null)
		{
			loadQuiz();
		}
		else
		{
			// TODO: Error
			finish();
		}
	}

	protected void loadQuiz()
	{
		if (page.getTitle() != null)
		{
			String title = UiSettings.getInstance().getTextProcessor().process(page.getTitle().getContent());

			if (!TextUtils.isEmpty(title))
			{
				setTitle(title);
			}
		}

		((ViewGroup)findViewById(R.id.finish_container)).removeAllViews();
		((ViewGroup)findViewById(R.id.finish_container)).setVisibility(View.GONE);
		((ViewGroup)findViewById(R.id.quiz_container)).setVisibility(View.VISIBLE);

		Collection<FragmentPackage> fragmentPages = new ArrayList<FragmentPackage>();

		for (QuizItem question : page.getChildren())
		{
			if (question != null)
			{
				Bundle args = new Bundle();
				args.putSerializable(EXTRA_QUESTION, question);

				FragmentIntent intent = new FragmentIntent();
				intent.setFragment(StormQuizFragment.class); // TODO: Use UiSettings#intentFactory to resolve this instead
				intent.setArguments(args);

				FragmentPackage fragmentPackage = new FragmentPackage(intent, UiSettings.getInstance().getApp().findPageDescriptor(page));
				fragmentPages.add(fragmentPackage);
			}
		}

		((StormPageAdapter)pageAdapter).setPages(fragmentPages);
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
		pageAdapter.setIndex(0);
	}

	@Override public void onPageScrolled(int i, float v, int i2)
	{

	}

	@Override public void onPageSelected(int i)
	{

	}

	@Override public void onPageScrollStateChanged(int i)
	{

	}
}