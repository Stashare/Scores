package ke.co.stashare.scores.samples.flexibleadapter.items;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import ke.co.stashare.scores.R;

/**
 * Model object representing Overall functionality as CardView.
 * This Model object is bound via METHOD B: {@code OverallAdapter} implements the 3 methods to
 * bind this item.
 *
 * @author Davide Steduto
 * @see ke.co.stashare.scores.samples.flexibleadapter.OverallAdapter
 * @since 12/04/2016
 */
public class OverallItem extends AbstractFlexibleItem<OverallItem.LabelViewHolder> {

	private int id;
	private String title;
	private String description;
	private Drawable icon;

	public OverallItem(int id, String title) {
		this.id = id;
		this.title = title;
		setSelectable(false);
		//Allow dragging
		setDraggable(true);
	}

	public OverallItem withDescription(String description) {
		this.description = description;
		return this;
	}

	public OverallItem withIcon(Drawable icon) {
		this.icon = icon;
		return this;
	}

	public OverallItem withEnabled(boolean enabled) {
		setEnabled(enabled);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OverallItem that = (OverallItem) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}


	public static class LabelViewHolder extends FlexibleViewHolder {

		public TextView mTitle;
		public TextView mSubtitle;
		public ImageView mIcon;

		public LabelViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			mTitle = (TextView) view.findViewById(R.id.title);
			mSubtitle = (TextView) view.findViewById(R.id.subtitle);
			mIcon = (ImageView) view.findViewById(R.id.label_background);
		}

		@Override
		public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
			if (mAdapter.getRecyclerView().getLayoutManager() instanceof GridLayoutManager) {
				if (position % 2 != 0)
					AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
				else
					AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
			} else {
				if (isForward)
					AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.getRecyclerView());
				else
					AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView());
			}
		}
	}

}