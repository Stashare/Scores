package ke.co.stashare.scores.samples.flexibleadapter.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flipview.FlipView;
import ke.co.stashare.scores.R;
import ke.co.stashare.scores.samples.flexibleadapter.ExampleAdapter;
import ke.co.stashare.scores.samples.flexibleadapter.MainActivity;
import ke.co.stashare.scores.samples.flexibleadapter.services.DatabaseService;
import ke.co.stashare.scores.utils.Utils;

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentExpandableMultiLevel extends AbstractFragment {

	public static final String TAG = FragmentExpandableMultiLevel.class.getSimpleName();

	private ExampleAdapter mAdapter;

	public static FragmentExpandableMultiLevel newInstance(int columnCount) {
		FragmentExpandableMultiLevel fragment = new FragmentExpandableMultiLevel();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FragmentExpandableMultiLevel() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Settings for FlipView
		FlipView.resetLayoutAnimationDelay(true, 1000L);

		// Create New Database and Initialize RecyclerView
		DatabaseService.getInstance().createExpandableMultiLevelDatabase(50);
		initializeRecyclerView(savedInstanceState);

		// Settings for FlipView
		FlipView.stopLayoutAnimation();
	}

	@SuppressWarnings({"ConstantConditions", "NullableProblems"})
	private void initializeRecyclerView(Bundle savedInstanceState) {
		// Initialize Adapter and RecyclerView
		// ExampleAdapter makes use of stableIds, I strongly suggest to implement 'item.hashCode()'
		mAdapter = new ExampleAdapter(DatabaseService.getInstance().getDatabaseList(), getActivity());
		// Experimenting NEW features (v5.0.0)
		mAdapter.expandItemsAtStartUp()
				.setAutoCollapseOnExpand(false)
				.setMinCollapsibleLevel(1) //Auto-collapse only items with level >= 1 (avoid to collapse also sections!)
				.setAutoScrollOnExpand(true);
		mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
		mRecyclerView.setLayoutManager(createNewLinearLayoutManager());
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setHasFixedSize(true); //Size of RV will not change
		// NOTE: Use default item animator 'canReuseUpdatedViewHolder()' will return true if
		// a Payload is provided. FlexibleAdapter is actually sending Payloads onItemChange.
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		// Add FastScroll to the RecyclerView, after the Adapter has been attached the RecyclerView!!!
		mAdapter.setFastScroller((FastScroller) getView().findViewById(R.id.fast_scroller),
				Utils.getColorAccent(getActivity()), (MainActivity) getActivity());
		// Experimenting NEW features (v5.0.0)
		mAdapter.setLongPressDragEnabled(true) //Enable long press to drag items
				.setHandleDragEnabled(true) //Enable handle drag
				.setSwipeEnabled(true); //Enable swipe items
				//.setDisplayHeadersAtStartUp(true); //Show Headers at startUp: (not necessary if Headers are also Expandable)

		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setEnabled(true);
		mListener.onFragmentChange(swipeRefreshLayout, mRecyclerView, SelectableAdapter.MODE_IDLE);

		// Add 2 Scrollable Headers
		mAdapter.addUserLearnedSelection(savedInstanceState == null);
		mAdapter.showLayoutInfo(savedInstanceState == null);
	}

	@Override
	public void showNewLayoutInfo(MenuItem item) {
		super.showNewLayoutInfo(item);
		mAdapter.showLayoutInfo(false);
	}

	@Override
	protected GridLayoutManager createNewGridLayoutManager() {
		GridLayoutManager gridLayoutManager = new SmoothScrollGridLayoutManager(getActivity(), mColumnCount);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				// NOTE: If you use simple integers to identify the ViewType,
				// here, you should use them and not Layout integers
				switch (mAdapter.getItemViewType(position)) {
					case R.layout.recycler_scrollable_layout_item:
					case R.layout.recycler_scrollable_uls_item:
					case R.layout.recycler_header_item:
					case R.layout.recycler_expandable_header_item:
					case R.layout.recycler_expandable_item:
						return mColumnCount;
					default:
						return 1;
				}
			}
		});
		return gridLayoutManager;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.v(TAG, "onCreateOptionsMenu called!");
		inflater.inflate(R.menu.menu_expandable, menu);
		mListener.initSearchView(menu);
		//TODO: Implement Filterable in the item interfaces
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_list_type)
			mAdapter.setAnimationOnScrolling(true);
		return super.onOptionsItemSelected(item);
	}

}