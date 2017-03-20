package ke.co.stashare.scores.samples.flexibleadapter.items;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import ke.co.stashare.scores.samples.flexibleadapter.services.DatabaseConfiguration;
import ke.co.stashare.scores.utils.Utils;
import eu.davidea.viewholders.FlexibleViewHolder;
import ke.co.stashare.scores.R;

/**
 * Item dedicated only for User Learns Selection view (located always at the top in the Adapter).
 * This item is a Scrollable Header.
 */
public class ScrollableULSItem extends AbstractItem<ScrollableULSItem.ULSViewHolder> {

	public ScrollableULSItem(String id) {
		super(id);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.recycler_scrollable_uls_item;
	}

	@Override
	public ULSViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new ULSViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, ULSViewHolder holder, int position, List payloads) {
		holder.mImageView.setImageResource(R.drawable.ic_account_circle_white_24dp);
		holder.itemView.setActivated(true);
		holder.mTitle.setSelected(true);//For marquee!!
		holder.mTitle.setText(Utils.fromHtmlCompat(getTitle()));
		holder.mSubtitle.setText(Utils.fromHtmlCompat(getSubtitle()));
	}

	/**
	 * Used for UserLearnsSelection.
	 */
	class ULSViewHolder extends FlexibleViewHolder {

		ImageView mImageView;
		TextView mTitle;
		TextView mSubtitle;
		ImageView mDismissIcon;

		ULSViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			mTitle = (TextView) view.findViewById(R.id.title);
			mSubtitle = (TextView) view.findViewById(R.id.subtitle);
			mImageView = (ImageView) view.findViewById(R.id.image);
			mDismissIcon = (ImageView) view.findViewById(R.id.dismiss_icon);
			mDismissIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DatabaseConfiguration.userLearnedSelection = true;
					//Don't need anymore to set permanent deletion for Scrollable Headers and Footers
					//mAdapter.setPermanentDelete(true);
					//noinspection unchecked
					mAdapter.removeScrollableHeader(ScrollableULSItem.this);
					//mAdapter.setPermanentDelete(false);
				}
			});

			//Support for StaggeredGridLayoutManager
			if (itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
				((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
			}
		}

		@Override
		public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
			AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView());
		}
	}

	@Override
	public String toString() {
		return "ScrollableULSItem[" + super.toString() + "]";
	}

}