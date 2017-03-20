package ke.co.stashare.scores.samples.flexibleadapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ActionModeHelper;
import eu.davidea.flexibleadapter.helpers.UndoHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IFlexible;
import ke.co.stashare.scores.R;
import ke.co.stashare.scores.samples.flexibleadapter.dialogs.EditItemDialog;
import ke.co.stashare.scores.samples.flexibleadapter.dialogs.MessageDialog;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.AbstractFragment;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentAnimators;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentAsyncFilter;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentEndlessScrolling;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentExpandableMultiLevel;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentExpandableSections;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentHeadersSections;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentHolderSections;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentInstagramHeaders;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentOverall;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentSelectionModes;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.FragmentStaggeredLayout;
import ke.co.stashare.scores.samples.flexibleadapter.fragments.OnFragmentInteractionListener;
import ke.co.stashare.scores.samples.flexibleadapter.items.AbstractItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.ExpandableItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.HeaderItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.OverallItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.SimpleItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.StaggeredItem;
import ke.co.stashare.scores.samples.flexibleadapter.items.SubItem;
import ke.co.stashare.scores.samples.flexibleadapter.services.DatabaseConfiguration;
import ke.co.stashare.scores.samples.flexibleadapter.services.DatabaseService;
import ke.co.stashare.scores.samples.flexibleadapter.services.DatabaseType;
import ke.co.stashare.scores.samples.flexibleadapter.views.HeaderView;
import ke.co.stashare.scores.utils.ScrollAwareFABBehavior;
import ke.co.stashare.scores.utils.Utils;
/**
 * The Demo application is organized in Fragments with 1 Activity {@code MainActivity}
 * implementing most of the methods. Each Fragment shows a different example and can assemble
 * more functionalities at once.
 * <p>
 * <p>The Activity implementation is organized in this order:</p>
 * <p>
 * <ul>
 * <li>Activity management
 * <li>Initialization methods
 * <li>Navigation drawer & Fragment management
 * <li>Floating Action Button
 * <li>SearchView
 * <li>Option menu preparation & management
 * <li>Dialog listener implementation (for the example of onItemClick)
 * <li><b>FlexibleAdapter listeners implementation</b>
 * <li>ActionMode implementation
 * <li>Extras
 * </ul>
 * <p>
 * The Fragments <u>may</u> use Activity implementations or may override specific behaviors
 * themselves. Fragments have {@code AbstractFragment} in common to have some methods reusable.
 * <p>
 * <p>...more on
 * <a href="https://github.com/davideas/FlexibleAdapter/wiki/5.x-%7C-Demo-App">Demo app Wiki page</a>.</p>
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
public class MainActivity extends AppCompatActivity implements
		ActionMode.Callback, EditItemDialog.OnEditItemListener, SearchView.OnQueryTextListener,
		FlexibleAdapter.OnUpdateListener, UndoHelper.OnUndoListener,
		FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener,
		FlexibleAdapter.OnItemMoveListener, FlexibleAdapter.OnItemSwipeListener,
		FastScroller.OnScrollStateChangeListener,
		NavigationView.OnNavigationItemSelectedListener,
		OnFragmentInteractionListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	/**
	 * Bundle key representing the Active Fragment
	 */
	private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";

	/**
	 * FAB
	 */
	private FloatingActionButton mFab;

	/**
	 * RecyclerView and related objects
	 */
	private RecyclerView mRecyclerView;
	private FlexibleAdapter<AbstractFlexibleItem> mAdapter;
	private ActionModeHelper mActionModeHelper;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Toolbar mToolbar;
	private HeaderView mHeaderView;
	private DrawerLayout mDrawer;
	private NavigationView mNavigationView;
	private AbstractFragment mFragment;
	private SearchView mSearchView;

	private final Handler mRefreshHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
		public boolean handleMessage(Message message) {
			switch (message.what) {
				case 0: // Stop
					mSwipeRefreshLayout.setRefreshing(false);
					return true;
				case 1: // Start
					mSwipeRefreshLayout.setRefreshing(true);
					return true;
				case 2: // Show empty view
					ViewCompat.animate(findViewById(R.id.empty_view)).alpha(1);
					return true;
				default:
					return false;
			}
		}
	});

	/* ===================
	 * ACTIVITY MANAGEMENT
	 * =================== */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Utils.hasLollipop()) requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		super.onCreate(savedInstanceState);

		if (Utils.hasLollipop()) {
			getWindow().setEnterTransition(new Fade());
		}

		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");
		FlexibleAdapter.enableLogs(true);

		// Initialize Toolbar, Drawer & FAB
		initializeToolbar();
		initializeDrawer();
		initializeFab();
		// Initialize Fragment containing Adapter & RecyclerView
		initializeFragment(savedInstanceState);

		// With FlexibleAdapter v5.0.0 we don't need to call this function anymore
		// It is automatically called if Activity implements FlexibleAdapter.OnUpdateListener
		//updateEmptyView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState!");
		mAdapter.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, STATE_ACTIVE_FRAGMENT, mFragment);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore previous state
		if (savedInstanceState != null && mAdapter != null) {
			// Selection
			mAdapter.onRestoreInstanceState(savedInstanceState);
			mActionModeHelper.restoreSelection(this);
		}
	}

	@Override
	public void onFragmentChange(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, int mode) {
		mRecyclerView = recyclerView;
		mAdapter = (FlexibleAdapter) recyclerView.getAdapter();
		mSwipeRefreshLayout = swipeRefreshLayout;
		initializeSwipeToRefresh();
		initializeActionModeHelper(mode);
	}

	/* ======================
	 * INITIALIZATION METHODS
	 * ====================== */

	private void initializeActionModeHelper(int mode) {
		mActionModeHelper = new ActionModeHelper(mAdapter, mFragment.getContextMenuResId(), this) {
			@Override
			public void updateContextTitle(int count) {
				if (mActionMode != null) {//You can use the internal ActionMode instance
					mActionMode.setTitle(count == 1 ?
							getString(R.string.action_selected_one, Integer.toString(count)) :
							getString(R.string.action_selected_many, Integer.toString(count)));
				}
			}
		}.withDefaultMode(mode);
	}

	private void initializeFragment(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mFragment = (AbstractFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_ACTIVE_FRAGMENT);
		}
		if (mFragment == null) {
			mFragment = FragmentOverall.newInstance(2);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.recycler_view_container,
				mFragment).commit();
	}

	private void initializeSwipeToRefresh() {
		// Swipe down to force synchronize
		//mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setDistanceToTriggerSync(390);
		//mSwipeRefreshLayout.setEnabled(true); //Controlled by fragments!
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_purple, android.R.color.holo_blue_light,
				android.R.color.holo_green_light, android.R.color.holo_orange_light);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Passing true as parameter we always animate the changes between the old and the new data set
				DatabaseService.getInstance().updateNewItems();
				mAdapter.updateDataSet(DatabaseService.getInstance().getDatabaseList(), DatabaseConfiguration.animateOnUpdate);
				mSwipeRefreshLayout.setRefreshing(true);
				mRefreshHandler.sendEmptyMessageDelayed(0, 1500L); //Simulate network time
				mActionModeHelper.destroyActionModeIfCan();
			}
		});
	}

	private void initializeToolbar() {
		Log.d(TAG, "initializeToolbar as actionBar");
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mHeaderView = (HeaderView) findViewById(R.id.toolbar_header_view);
		mHeaderView.bindTo(getString(R.string.app_name), getString(R.string.overall));
		//mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
		// Toolbar will now take on default Action Bar characteristics
		setSupportActionBar(mToolbar);
	}

	private void initializeDrawer() {
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawer.addDrawerListener(toggle);
		toggle.syncState();

		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		// Version
		TextView appVersion = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.app_version);
		appVersion.setText(getString(R.string.about_version,
				Utils.getVersionName(this),
				Integer.toString(Utils.getVersionCode(this))));
	}

	private void initializeFab() {
		mFab = (FloatingActionButton) findViewById(R.id.fab);
		mFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActionModeHelper.destroyActionModeIfCan();
				mFragment.performFabAction();
			}
		});
		// No Fab on 1st fragment
		hideFabSilently();
	}

	@Override
	public void onFastScrollerStateChange(boolean scrolling) {
		if (scrolling) {
			hideFab();
		} else {
			showFab();
		}
	}

	/* =======================================
	 * NAVIGATION DRAWER & FRAGMENT MANAGEMENT
	 * ======================================= */

	/**
	 * IMPORTANT!! READ THE COMMENT FOR THE FRAGMENT REPLACE
	 */
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		hideFabSilently();
		CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
		ScrollAwareFABBehavior fabBehavior = ((ScrollAwareFABBehavior) layoutParams.getBehavior());
		fabBehavior.setEnabled(false);

		// Handle navigation view item clicks
		int id = item.getItemId();
		if (id == R.id.nav_overall) {
			mFragment = FragmentOverall.newInstance(2);
		} else if (id == R.id.nav_selection_modes) {
			mFragment = FragmentSelectionModes.newInstance(2);
		} else if (id == R.id.nav_filter) {
			mFragment = FragmentAsyncFilter.newInstance(true);
		} else if (id == R.id.nav_animator) {
			mFragment = FragmentAnimators.newInstance();
		} else if (id == R.id.nav_endless_scrolling) {
			mFragment = FragmentEndlessScrolling.newInstance(2);
		} else if (id == R.id.nav_instagram_headers) {
			mFragment = FragmentInstagramHeaders.newInstance();
		} else if (id == R.id.nav_headers_and_sections) {
			mFragment = FragmentHeadersSections.newInstance(2);
			fabBehavior.setEnabled(true);
		} else if (id == R.id.nav_multi_level_expandable) {
			mFragment = FragmentExpandableMultiLevel.newInstance(2);
		} else if (id == R.id.nav_expandable_sections) {
			mFragment = FragmentExpandableSections.newInstance(3);
		} else if (id == R.id.nav_staggered) {
			mFragment = FragmentStaggeredLayout.newInstance(2);
		} else if (id == R.id.nav_model_holders) {
			mFragment = FragmentHolderSections.newInstance();
		} else if (id == R.id.nav_viewpager) {
			Intent intent = new Intent(this, ViewPagerActivity.class);
			ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeBasic();
			ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
			// Close drawer
			mRecyclerView.post(new Runnable() {
				@Override
				public void run() {
					mDrawer.closeDrawer(GravityCompat.START);
				}
			});
			return true;
		} else if (id == R.id.nav_about) {
			MessageDialog.newInstance(
					R.drawable.ic_info_grey600_24dp,
					getString(R.string.about_title),
					getString(R.string.about_body,
							Utils.getVersionName(this),
							Integer.toString(Utils.getVersionCode(this))))
					.show(getFragmentManager(), MessageDialog.TAG);
			return true;
		} else if (id == R.id.nav_github) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://github.com/davideas/FlexibleAdapter"));
			startActivity(Intent.createChooser(intent, getString(R.string.intent_chooser)));
			return true;
		}
		// Insert the fragment by replacing any existing fragment
		if (mFragment != null) {
			// Highlight the selected item has been done by NavigationView
			item.setChecked(true);
			// THIS IS VERY IMPORTANT. Because you are going to inflate a new RecyclerView, its
			// Adapter will be null, therefore the following method cannot be called automatically!
			// If your StickyHeaderContainer is in the main view, you must call this method to clean
			// the previous sticky view. Alternatively you can move the <include> of StickyHeaderLayout
			// in the Fragment view.
			mAdapter.onDetachedFromRecyclerView(mRecyclerView);
			// Inflate the new Fragment with the new RecyclerView and a new Adapter
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.recycler_view_container, mFragment).commit();
			// Close drawer
			mRecyclerView.post(new Runnable() {
				@Override
				public void run() {
					mDrawer.closeDrawer(GravityCompat.START);
				}
			});
			//mToolbar.setSubtitle(item.getTitle());
			mHeaderView.bindTo(getString(R.string.app_name), item.getTitle());
			//mToolbarLayout.setTitle(getString(R.string.app_name));

			return true;
		}
		return false;
	}

	/* ======================
	 * FLOATING ACTION BUTTON
	 * ====================== */

	private void hideFabSilently() {
		mFab.setAlpha(0f);
	}

	private void hideFab() {
		ViewCompat.animate(mFab)
				.scaleX(0f).scaleY(0f)
				.alpha(0f).setDuration(100)
				.start();
	}

	private void showFab() {
		if (mFragment instanceof FragmentHeadersSections ||
				mFragment instanceof FragmentStaggeredLayout ||
				mFragment instanceof FragmentAsyncFilter) {
			ViewCompat.animate(mFab)
					.scaleX(1f).scaleY(1f)
					.alpha(1f).setDuration(200)
					.setStartDelay(300L)
					.start();
		}
	}

	/* ===========
	 * SEARCH VIEW
	 * =========== */

	@Override
	public void initSearchView(final Menu menu) {
		// Associate searchable configuration with the SearchView
		Log.d(TAG, "onCreateOptionsMenu setup SearchView!");
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		if (searchItem != null) {
			MenuItemCompat.setOnActionExpandListener(
					searchItem, new MenuItemCompat.OnActionExpandListener() {
						@Override
						public boolean onMenuItemActionExpand(MenuItem item) {
							MenuItem listTypeItem = menu.findItem(R.id.action_list_type);
							if (listTypeItem != null)
								listTypeItem.setVisible(false);
							//hideFab();
							return true;
						}

						@Override
						public boolean onMenuItemActionCollapse(MenuItem item) {
							MenuItem listTypeItem = menu.findItem(R.id.action_list_type);
							if (listTypeItem != null)
								listTypeItem.setVisible(true);
							//showFab();
							return true;
						}
					});
			mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			mSearchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
			mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
			mSearchView.setQueryHint(getString(R.string.action_search));
			mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			mSearchView.setOnQueryTextListener(this);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (mAdapter.hasNewSearchText(newText)) {
			Log.d(TAG, "onQueryTextChange newText: " + newText);
			mAdapter.setSearchText(newText);
			// Fill and Filter mItems with your custom list and automatically animate the changes
			// Watch out! The original list must be a copy
			mAdapter.filterItems(DatabaseService.getInstance().getDatabaseList(), DatabaseConfiguration.delay);
		}
		// Disable SwipeRefresh if search is active!!
		mSwipeRefreshLayout.setEnabled(!mAdapter.hasSearchText());
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.v(TAG, "onQueryTextSubmit called!");
		return onQueryTextChange(query);
	}

	/* ====================================
	 * OPTION MENU PREPARATION & MANAGEMENT
	 * ==================================== */

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu called!");

		if (mSearchView != null) {
			//Has searchText?
			if (!mAdapter.hasSearchText()) {
				Log.d(TAG, "onPrepareOptionsMenu Clearing SearchView!");
				mSearchView.setIconified(true);// This also clears the text in SearchView widget
			} else {
				//Necessary after the restoreInstanceState
				menu.findItem(R.id.action_search).expandActionView();//must be called first
				//This restores the text, must be after the expandActionView()
				mSearchView.setQuery(mAdapter.getSearchText(), false);//submit = false!!!
				mSearchView.clearFocus();//Optionally the keyboard can be closed
				//mSearchView.setIconified(false);//This is not necessary
			}
		}
		// Fast Scroller
		MenuItem fastScrollerItem = menu.findItem(R.id.action_fast_scroller);
		if (fastScrollerItem != null) {
			fastScrollerItem.setChecked(mAdapter.isFastScrollerEnabled());
		}
		// Animate on update?
		MenuItem animateUpdateMenuItem = menu.findItem(R.id.action_animate_on_update);
		if (animateUpdateMenuItem != null) {
			animateUpdateMenuItem.setChecked(DatabaseConfiguration.animateOnUpdate);
		}
		// Headers are shown?
		MenuItem headersMenuItem = menu.findItem(R.id.action_show_hide_headers);
		if (headersMenuItem != null) {
			headersMenuItem.setTitle(mAdapter.areHeadersShown() ? R.string.hide_headers : R.string.show_headers);
		}
		// Sticky Header item?
		MenuItem stickyItem = menu.findItem(R.id.action_sticky_headers);
		if (stickyItem != null) {
			stickyItem.setEnabled(mAdapter.areHeadersShown());
			stickyItem.setChecked(mAdapter.areHeadersSticky());
		}
		// Scrolling Animations?
		MenuItem animationMenuItem = menu.findItem(R.id.action_animation);
		if (animationMenuItem != null) {
			animationMenuItem.setChecked(DatabaseConfiguration.animateOnScrolling);
		}
		// Reverse scrolling animation?
		MenuItem reverseMenuItem = menu.findItem(R.id.action_reverse);
		if (reverseMenuItem != null) {
			reverseMenuItem.setEnabled(mAdapter.isAnimationOnScrollingEnabled());
			reverseMenuItem.setChecked(mAdapter.isAnimationOnReverseScrollingEnabled());
		}
		// DiffUtil?
		MenuItem diffUtilItem = menu.findItem(R.id.action_diff_util);
		if (diffUtilItem != null) {
			diffUtilItem.setChecked(DatabaseConfiguration.animateWithDiffUtil);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_animate_on_update) {
			DatabaseConfiguration.animateOnUpdate = !DatabaseConfiguration.animateOnUpdate;
			item.setChecked(DatabaseConfiguration.animateOnUpdate);
			Snackbar.make(findViewById(R.id.main_view), (DatabaseConfiguration.animateOnUpdate ? "Enabled" : "Disabled") +
					" animation on update, now refresh!\n(P = persistent)", Snackbar.LENGTH_SHORT).show();
		} else if (id == R.id.action_animation) {
			if (mAdapter.isAnimationOnScrollingEnabled()) {
				DatabaseConfiguration.animateOnScrolling = false;
				mAdapter.setAnimationOnScrolling(false);
				item.setChecked(false);
				Snackbar.make(findViewById(R.id.main_view), "Disabled scrolling animation, now reopen the page\n(* = persistent)", Snackbar.LENGTH_SHORT).show();
			} else {
				DatabaseConfiguration.animateOnScrolling = true;
				mAdapter.setAnimationOnScrolling(true);
				item.setChecked(true);
				Snackbar.make(findViewById(R.id.main_view), "Enabled scrolling animation, now reopen the page\n(* = persistent)", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.action_reverse) {
			if (mAdapter.isAnimationOnReverseScrollingEnabled()) {
				mAdapter.setAnimationOnReverseScrolling(false);
				item.setChecked(false);
				Snackbar.make(findViewById(R.id.main_view), "Disabled reverse scrolling animation", Snackbar.LENGTH_SHORT).show();
			} else {
				mAdapter.setAnimationOnReverseScrolling(true);
				item.setChecked(true);
				Snackbar.make(findViewById(R.id.main_view), "Enabled reverse scrolling animation", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.action_diff_util) {
			if (mAdapter.isAnimateChangesWithDiffUtil()) {
				DatabaseConfiguration.animateWithDiffUtil = false;
				mAdapter.setAnimateChangesWithDiffUtil(false);
				item.setChecked(false);
				Snackbar.make(findViewById(R.id.main_view), "Default calculation is used to animate changes\n(* = persistent)", Snackbar.LENGTH_SHORT).show();
			} else {
				DatabaseConfiguration.animateWithDiffUtil = true;
				mAdapter.setAnimateChangesWithDiffUtil(true);
				item.setChecked(true);
				Snackbar.make(findViewById(R.id.main_view), "DiffUtil is used to animate changes\n(* = persistent)", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.action_auto_collapse) {
			if (mAdapter.isAutoCollapseOnExpand()) {
				mAdapter.setAutoCollapseOnExpand(false);
				item.setChecked(false);
				Snackbar.make(findViewById(R.id.main_view), "Auto-Collapse is disabled", Snackbar.LENGTH_SHORT).show();
			} else {
				mAdapter.setAutoCollapseOnExpand(true);
				item.setChecked(true);
				Snackbar.make(findViewById(R.id.main_view), "Auto-Collapse is enabled", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.action_expand_collapse_all) {
			if (item.getTitle().equals(getString(R.string.expand_all))) {
				int total = mAdapter.expandAll();
				Toast.makeText(this, "Expanded " + total + " items", Toast.LENGTH_SHORT).show();
				item.setTitle(R.string.collapse_all);
			} else {
				int total = mAdapter.collapseAll();
				Toast.makeText(this, "Collapsed " + total + " items", Toast.LENGTH_SHORT).show();
				item.setTitle(R.string.expand_all);
			}
		} else if (id == R.id.action_show_hide_headers) {
			if (mAdapter.areHeadersShown()) {
				mAdapter.hideAllHeaders();
				item.setTitle(R.string.show_headers);
			} else {
				mAdapter.showAllHeaders();
				item.setTitle(R.string.hide_headers);
			}
		} else if (id == R.id.action_sticky_headers) {
			mAdapter.setStickyHeaders(!mAdapter.areHeadersSticky());
			item.setChecked(!mAdapter.areHeadersSticky());
			Snackbar.make(findViewById(R.id.main_view), "Sticky headers " +
					(mAdapter.areHeadersSticky() ? "disabled" : "enabled"), Snackbar.LENGTH_SHORT).show();
		} else if (id == R.id.action_selection_mode) {
			if (mAdapter.getMode() == SelectableAdapter.MODE_IDLE) {
				mAdapter.setMode(SelectableAdapter.MODE_SINGLE);
				mActionModeHelper.withDefaultMode(SelectableAdapter.MODE_SINGLE);
				item.setIcon(R.drawable.ic_select_off_white_24dp);
				item.setTitle(R.string.mode_idle);
				Snackbar.make(findViewById(R.id.main_view), "Selection MODE_SINGLE is enabled", Snackbar.LENGTH_SHORT).show();
			} else {
				mAdapter.setMode(SelectableAdapter.MODE_IDLE);
				mActionModeHelper.withDefaultMode(SelectableAdapter.MODE_IDLE);
				item.setIcon(R.drawable.ic_select_white_24dp);
				item.setTitle(R.string.mode_single);
				Snackbar.make(findViewById(R.id.main_view), "Selection MODE_IDLE is enabled", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.action_fast_scroller) {
			mAdapter.toggleFastScroller();
			item.setChecked(mAdapter.isFastScrollerEnabled());
		} else if (id == R.id.action_reset || id == R.id.action_delete) {
			showFab();
		}

		return super.onOptionsItemSelected(item);
	}

	/* ===============================================================
	 * DIALOG LISTENER IMPLEMENTATION (For the example of onItemClick)
	 * =============================================================== */

	@Override
	public void onTitleModified(int position, String newTitle) {
		AbstractFlexibleItem abstractItem = mAdapter.getItem(position);
		assert abstractItem != null;
		if (abstractItem instanceof AbstractItem) {
			AbstractItem exampleItem = (AbstractItem) abstractItem;
			exampleItem.setTitle(newTitle);
		} else if (abstractItem instanceof HeaderItem) {
			HeaderItem headerItem = (HeaderItem) abstractItem;
			headerItem.setTitle(newTitle);
		}
		mAdapter.updateItem(position, abstractItem, null);
	}

	/* ========================================================================
	 * FLEXIBLE ADAPTER LISTENERS IMPLEMENTATION
	 * Listeners implementation are in MainActivity to easily reuse the common
	 * components like SwipeToRefresh, ActionMode, NavigationView, etc...
	 * ======================================================================== */

	@Override
	public boolean onItemClick(int position) {
		IFlexible flexibleItem = mAdapter.getItem(position);
		if (flexibleItem instanceof OverallItem) {
			OverallItem overallItem = (OverallItem) flexibleItem;
			MenuItem menuItem = mNavigationView.getMenu().findItem(overallItem.getId());
			onNavigationItemSelected(menuItem);
			return false;
		}

		// Action on elements are allowed if Mode is IDLE, otherwise selection has priority
		if (mAdapter.getMode() != SelectableAdapter.MODE_IDLE && mActionModeHelper != null) {
			boolean activate = mActionModeHelper.onClick(position);
			Log.d(TAG, "Last activated position " + mActionModeHelper.getActivatedPosition());
			return activate;
		} else {
			// Notify the active callbacks or implement a custom action onClick
			if (flexibleItem instanceof SimpleItem || flexibleItem instanceof SubItem) {
				//TODO FOR YOU: call your custom Action on item click
				String title = extractTitleFrom(flexibleItem);
				EditItemDialog.newInstance(title, position).show(getFragmentManager(), EditItemDialog.TAG);
			}
			return false;
		}
	}

	@Override
	public void onItemLongClick(int position) {
		if (!(mFragment instanceof FragmentAsyncFilter))
			mActionModeHelper.onLongClick(this, position);
	}

	@Override
	public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		mSwipeRefreshLayout.setEnabled(actionState == ItemTouchHelper.ACTION_STATE_IDLE);
	}

	@Override
	public boolean shouldMoveItem(int fromPosition, int toPosition) {
		return true;
	}

	@Override
	public void onItemMove(int fromPosition, int toPosition) {
		//TODO FOR YOU: this doesn't work with all types of items (of course)..... we need to implement some custom logic. Consider to use also onActionStateChanged() when dragging is completed
//		DatabaseService.getInstance().swapItems(
//				DatabaseService.getInstance().getDatabaseList().indexOf(fromItem),
//				DatabaseService.getInstance().getDatabaseList().indexOf(toItem));
	}

	@Override
	public void onItemSwipe(final int position, int direction) {
		Log.i(TAG, "onItemSwipe position=" + position +
				" direction=" + (direction == ItemTouchHelper.LEFT ? "LEFT" : "RIGHT"));

		// Option 1 FULL_SWIPE: Direct action no Undo Action
		// Do something based on direction when item has been swiped:
		//   A) update item, set "read" if an email etc.
		//   B) remove the item from the adapter;

		// Option 2 FULL_SWIPE: Delayed action with Undo Action
		// Show action button and start a new Handler:
		//   A) on time out do something based on direction (open dialog with options);

		// Create list for single position (only in onItemSwipe)
		List<Integer> positions = new ArrayList<>(1);
		positions.add(position);
		// Build the message
		IFlexible abstractItem = mAdapter.getItem(position);
		StringBuilder message = new StringBuilder();
		message.append(extractTitleFrom(abstractItem)).append(" ");
		// Experimenting NEW feature
		if (abstractItem.isSelectable())
			mAdapter.setRestoreSelectionOnUndo(false);

		// Perform different actions
		// Here, option 2A) is implemented
		if (direction == ItemTouchHelper.LEFT) {
			message.append(getString(R.string.action_archived));

			// Example of UNDO color
			int actionTextColor;
			if (Utils.hasMarshmallow()) {
				actionTextColor = getColor(R.color.material_color_orange_500);
			} else {
				//noinspection deprecation
				actionTextColor = getResources().getColor(R.color.material_color_orange_500);
			}

			new UndoHelper(mAdapter, this)
					.withPayload(null) //You can pass any custom object (in this case Boolean is enough)
					.withAction(UndoHelper.ACTION_UPDATE, new UndoHelper.SimpleActionListener() {
						@Override
						public boolean onPreAction() {
							// Return true to avoid default immediate deletion.
							// Ask to the user what to do, open a custom dialog. On option chosen,
							// remove the item from Adapter list as usual.
							return true;
						}
					})
					.withActionTextColor(actionTextColor)
					.remove(positions, findViewById(R.id.main_view), message,
							getString(R.string.undo), UndoHelper.UNDO_TIMEOUT);

			//Here, option 1B) is implemented
		} else if (direction == ItemTouchHelper.RIGHT) {
			message.append(getString(R.string.action_deleted));
			mSwipeRefreshLayout.setRefreshing(true);
			new UndoHelper(mAdapter, this)
					.withPayload(null) //You can pass any custom object (in this case Boolean is enough)
					.withAction(UndoHelper.ACTION_REMOVE, new UndoHelper.SimpleActionListener() {
						@Override
						public void onPostAction() {
							// Handle ActionMode title
							if (mAdapter.getSelectedItemCount() == 0)
								mActionModeHelper.destroyActionModeIfCan();
							else
								mActionModeHelper.updateContextTitle(mAdapter.getSelectedItemCount());
						}
					})
					.remove(positions, findViewById(R.id.main_view), message,
							getString(R.string.undo), UndoHelper.UNDO_TIMEOUT);
		}
	}

	/**
	 * Handling RecyclerView when empty.
	 * <p><b>Note:</b> The order, how the 3 Views (RecyclerView, EmptyView, FastScroller)
	 * are placed in the Layout, is important!</p>
	 */
	@Override
	public void onUpdateEmptyView(int size) {
		Log.d(TAG, "onUpdateEmptyView size=" + size);
		FastScroller fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
		View emptyView = findViewById(R.id.empty_view);
		TextView emptyText = (TextView) findViewById(R.id.empty_text);
		if (emptyText != null)
			emptyText.setText(getString(R.string.no_items));
		if (size > 0) {
			fastScroller.setVisibility(View.VISIBLE);
			mRefreshHandler.removeMessages(2);
			emptyView.setAlpha(0);
		} else {
			emptyView.setAlpha(0);
			mRefreshHandler.sendEmptyMessage(2);
			fastScroller.setVisibility(View.GONE);
		}
		if (mAdapter != null) {
			String message = (mAdapter.hasSearchText() ? "Filtered " : "Refreshed ");
			message += size + " items in " + mAdapter.getTime() + "ms";
			Snackbar.make(findViewById(R.id.main_view), message, Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onUndoConfirmed(int action) {
		if (action == UndoHelper.ACTION_UPDATE) {
			//TODO: Complete click animation on swiped item
//			final RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForLayoutPosition(mSwipedPosition);
//			if (holder instanceof ItemTouchHelperCallback.ViewHolderCallback) {
//				final View view = ((ItemTouchHelperCallback.ViewHolderCallback) holder).getFrontView();
//				Animator animator = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0);
//				animator.addListener(new SimpleAnimatorListener() {
//					@Override
//					public void onAnimationCancel(Animator animation) {
//						view.setTranslationX(0);
//					}
//				});
//				animator.start();
//			}
		} else if (action == UndoHelper.ACTION_REMOVE) {
			// Custom action is restore deleted items
			mAdapter.restoreDeletedItems();
			// Disable Refreshing
			mSwipeRefreshLayout.setRefreshing(false);
			// Check also selection restoration
			if (mAdapter.isRestoreWithSelection()) {
				mActionModeHelper.restoreSelection(this);
			}
		}
	}

	@Override
	public void onDeleteConfirmed(int action) {
		// Disable Refreshing
		mSwipeRefreshLayout.setRefreshing(false);
		// Removing items from Database. Example:
		for (AbstractFlexibleItem adapterItem : mAdapter.getDeletedItems()) {
			try {
				// NEW! You can take advantage of AutoMap and differentiate logic by viewType using "switch" statement
				switch (adapterItem.getLayoutRes()) {
					case R.layout.recycler_sub_item:
						SubItem subItem = (SubItem) adapterItem;
						DatabaseService.getInstance().removeSubItem(mAdapter.getExpandableOfDeletedChild(subItem), subItem);
						Log.d(TAG, "Confirm removed " + subItem.getTitle());
						break;
					case R.layout.recycler_expandable_item:
						DatabaseService.getInstance().removeItem(adapterItem);
						Log.d(TAG, "Confirm removed " + adapterItem);
						break;
				}

			} catch (IllegalStateException e) {
				// AutoMap is disabled, fallback to if-else with "instanceof" statement
				if (adapterItem instanceof SubItem) {
					// SubItem
					SubItem subItem = (SubItem) adapterItem;
					IExpandable expandable = mAdapter.getExpandableOf(subItem);
					DatabaseService.getInstance().removeSubItem(expandable, subItem);
					Log.d(TAG, "Confirm removed " + subItem.getTitle());
				} else if (adapterItem instanceof SimpleItem || adapterItem instanceof ExpandableItem) {
					DatabaseService.getInstance().removeItem(adapterItem);
					Log.d(TAG, "Confirm removed " + adapterItem);
				}
			}
		}
	}

	/* ==========================
	 * ACTION MODE IMPLEMENTATION
	 * ========================== */

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		if (Utils.hasMarshmallow()) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
		} else if (Utils.hasLollipop()) {
			//noinspection deprecation
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light));
		}
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_select_all:
				mAdapter.selectAll();
				mActionModeHelper.updateContextTitle(mAdapter.getSelectedItemCount());
				// We consume the event
				return true;

			case R.id.action_delete:
				// Build message before delete, for the SnackBar
				StringBuilder message = new StringBuilder();
				message.append(getString(R.string.action_deleted)).append(" ");
				for (Integer pos : mAdapter.getSelectedPositions()) {
					message.append(extractTitleFrom(mAdapter.getItem(pos)));
					if (mAdapter.getSelectedItemCount() > 1)
						message.append(", ");
				}

				// Experimenting NEW feature
				mAdapter.setRestoreSelectionOnUndo(true);

				// New Undo Helper
				new UndoHelper(mAdapter, this)
						.withPayload(Payload.CHANGE)
						.withAction(UndoHelper.ACTION_REMOVE, new UndoHelper.OnActionListener() {
							@Override
							public boolean onPreAction() {
								// Don't consume the event
								// OR use UndoHelper.SimpleActionListener and Override only onPostAction()
								return false;
							}

							@Override
							public void onPostAction() {
								// Enable Refreshing
								mRefreshHandler.sendEmptyMessage(1);
								mRefreshHandler.sendEmptyMessageDelayed(0, 7000);
								// Finish the action mode
								mActionModeHelper.destroyActionModeIfCan();
							}
						})
						.remove(mAdapter.getSelectedPositions(),
								findViewById(R.id.main_view), message,
								getString(R.string.undo), 7000);

				// We consume the event
				return true;

			case R.id.action_merge:
				if (mAdapter.getSelectedItemCount() > 1) {
					// Selected positions are sorted by default, we take the first item of the set
					int mainPosition = mAdapter.getSelectedPositions().get(0);
					mAdapter.removeSelection(mainPosition);
					StaggeredItem mainItem = (StaggeredItem) mAdapter.getItem(mainPosition);
					for (Integer position : mAdapter.getSelectedPositions()) {
						// Merge item - Save the modification in the memory for next refresh
						DatabaseService.getInstance().mergeItem(mainItem, (StaggeredItem) mAdapter.getItem(position));
					}
					// Remove merged item from the list
					mAdapter.removeAllSelectedItems();
					// Keep selection on mainItem & Skip default notification by calling addSelection
					mAdapter.addSelection(mainPosition);
					// Custom notification to bind again (ripple only)
					mAdapter.notifyItemChanged(mainPosition, "blink");
					// New title for context
					mActionModeHelper.updateContextTitle(mAdapter.getSelectedItemCount());
				}
				// We consume always the event, never finish the ActionMode
				return true;

			case R.id.action_split:
				if (mAdapter.getSelectedItemCount() == 1) {
					StaggeredItem mainItem = (StaggeredItem) mAdapter.getItem(mAdapter.getSelectedPositions().get(0));
					if (mainItem.getMergedItems() != null) {
						List<StaggeredItem> itemsToSplit = new ArrayList<>(mainItem.getMergedItems());
						for (StaggeredItem itemToSplit : itemsToSplit) {
							// Split item - Save the modification in the memory for next refresh
							DatabaseService.getInstance().splitItem(mainItem, itemToSplit);
							// We know the section object, so we can insert directly the item at the right position
							// The calculated position is then returned
							int position = mAdapter.addItemToSection(itemToSplit, mainItem.getHeader(), new DatabaseService.ItemComparatorById());
							mAdapter.toggleSelection(position); //Execute default notification
							mAdapter.notifyItemChanged(position, "blink");
						}
						// Custom notification to bind again (ripple only)
						mAdapter.notifyItemChanged(mAdapter.getGlobalPositionOf(mainItem), "blink");
						// New title for context
						mActionModeHelper.updateContextTitle(mAdapter.getSelectedItemCount());
					}
				}
				// We consume always the event, never finish the ActionMode
				return true;

			default:
				// If an item is not implemented we don't consume the event, so we finish the ActionMode
				return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		if (Utils.hasMarshmallow()) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark_light, this.getTheme()));
		} else if (Utils.hasLollipop()) {
			//noinspection deprecation
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark_light));
		}
	}
	
	/* ======
	 * EXTRAS
	 * ====== */

	@Override
	public void onBackPressed() {
		// If Drawer is open, back key closes it
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawer(GravityCompat.START);
			return;
		}
		// If ActionMode is active, back key closes it
		if (mActionModeHelper.destroyActionModeIfCan()) return;
		// If SearchView is visible, back key cancels search and iconify it
		if (mSearchView != null && !mSearchView.isIconified()) {
			mSearchView.setIconified(true);
			return;
		}
		// Return to Overall View
		if (DatabaseService.getInstance().getDatabaseType() != DatabaseType.OVERALL) {
			MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_overall);
			onNavigationItemSelected(menuItem);
			return;
		}
		// Close the App
		DatabaseService.onDestroy();
		super.onBackPressed();
	}

	private String extractTitleFrom(IFlexible flexibleItem) {
		if (flexibleItem instanceof AbstractItem) {
			AbstractItem exampleItem = (AbstractItem) flexibleItem;
			String title = exampleItem.getTitle();
			if (exampleItem instanceof ExpandableItem) {
				ExpandableItem expandableItem = (ExpandableItem) flexibleItem;
				if (expandableItem.getSubItems() != null) {
					title += "(+" + expandableItem.getSubItems().size() + ")";
				}
			}
			return title;
		} else if (flexibleItem instanceof HeaderItem) {
			HeaderItem headerItem = (HeaderItem) flexibleItem;
			return headerItem.getTitle();
		}
		// We already covered all situations with instanceof
		return "";
	}

}